import asyncio
import logging
from fastapi import FastAPI, HTTPException
import httpx
from httpx import HTTPError
from apscheduler.schedulers.asyncio import AsyncIOScheduler
from contextlib import asynccontextmanager
from typing import Dict
from email_sender import EmailSender
from DTO.MicroserviceDTO import MicroserviceDTO
from config.ConfigEnv import settings

logging.basicConfig(level=logging.INFO)

registered_msvcs: Dict[str, dict] = {}
health_status: Dict[str, dict] = {}

EMAIL_USER = settings.MAIL_USERNAME
EMAIL_PASS = settings.MAIL_PASSWORD
EMAIL_SMTP = "smtp.gmail.com"
EMAIL_PORT = 465
email_sender = EmailSender(EMAIL_SMTP, EMAIL_PORT, EMAIL_USER, EMAIL_PASS)

async def check_health(msvc_name: str):
    msvc = registered_msvcs[msvc_name]
    results = {}
    endpoints = ["/health", "/health/ready", "/health/live"]
    async with httpx.AsyncClient(timeout=5) as client:
        for endpoint in endpoints:
            url = f"{msvc['endpoint']}{endpoint}"
            try:
                response = await client.get(url)
                if response.status_code == 200:
                    results[endpoint] = {"status": "OK", "data": response.json()}
                else:
                    results[endpoint] = {"status": "ENDPOINT_ERROR", "code": response.status_code}
            except HTTPError:
                results[endpoint] = {"status": "CONNECTION_ERROR"}
            except Exception as e:
                results[endpoint] = {"status": "UNKNOWN_ERROR", "error": str(e)}
    health_status[msvc_name] = results
    if any(r["status"] != "OK" for r in results.values()):
        await notify_users(msvc_name, msvc["emails"], results)

async def notify_users(msvc_name, emails, results):
    subject = f"Alerta: Microservicio '{msvc_name}' caído/alarmado"
    body = f"El microservicio '{msvc_name}' presenta problemas:\n{results}"
    try:
        email_sender.send_email(subject, body, emails)
        logging.info(f"Correo de alerta enviado a: {emails}")
    except Exception as e:
        logging.error(f"Error enviando correo de alerta: {e}")

@asynccontextmanager
async def lifespan(app: FastAPI):
    logging.info("Entrando en lifespan")
    scheduler = AsyncIOScheduler()
    app.state.scheduler = scheduler
    for msvc_name, msvc in registered_msvcs.items():
        scheduler.add_job(check_health, "interval", [msvc_name], seconds=msvc["frequency"], id=msvc_name)
    scheduler.start()
    logging.info("Esperando 20 segundos de gracia antes del primer chequeo de salud...")
    await asyncio.sleep(20)
    for msvc_name in registered_msvcs:
        await check_health(msvc_name)
    try:
        yield
    finally:
        logging.info("Cerrando lifespan")
        scheduler.shutdown()

app = FastAPI(lifespan=lifespan)

@app.post("/register")
async def register_microservice(msvc: MicroserviceDTO):
    if msvc.name in registered_msvcs:
        raise HTTPException(status_code=400, detail="Microservicio ya registrado")
    registered_msvcs[msvc.name] = msvc.model_dump()
    scheduler = app.state.scheduler
    scheduler.add_job(check_health, "interval", [msvc.name], seconds=msvc.frequency, id=msvc.name)
    await check_health(msvc.name)
    return {"message": "Microservicio registrado", "name": msvc.name}

@app.get("/health")
async def get_health_status():
    return health_status

@app.get("/health/{msvc_name}")
async def get_health_status_msvc(msvc_name: str):
    if msvc_name not in health_status:
        raise HTTPException(status_code=404, detail="Microservicio no encontrado")
    return health_status[msvc_name]