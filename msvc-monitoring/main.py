import asyncio
from fastapi import FastAPI
import httpx
from apscheduler.schedulers.asyncio import AsyncIOScheduler
from contextlib import asynccontextmanager

MSVCS = [
    {"name": "msvc-security", "host": "msvc-security", "port": 8080},
    {"name": "msvc-orchestrator", "host": "msvc-orchestrator", "port": 8083},
    {"name": "msvc-delivery", "host": "msvc-delivery", "port": 8082},
    {"name": "msvc-consumer", "host": "msvc-consumer", "port": 8081},
    {"name": "msvc-saludo", "host": "msvc-consumer", "port": 80},
    # Agrega más microservicios aquí
]

ENDPOINTS = ["/health", "/health/ready", "/health/live"]

health_status = {}

async def check_health(msvc):
    results = {}
    base_url = f"http://{msvc['host']}:{msvc['port']}"
    async with httpx.AsyncClient(timeout=5) as client:
        for endpoint in ENDPOINTS:
            url = f"{base_url}{endpoint}"
            try:
                response = await client.get(url)
                if response.status_code == 200:
                    results[endpoint] = {"status": "OK", "data": response.json()}
                else:
                    results[endpoint] = {"status": "ENDPOINT_ERROR", "code": response.status_code}
            except httpx.RequestError:
                results[endpoint] = {"status": "CONNECTION_ERROR"}
            except Exception as e:
                results[endpoint] = {"status": "UNKNOWN_ERROR", "error": str(e)}
    health_status[msvc["name"]] = results

async def scheduled_health_check():
    tasks = [check_health(msvc) for msvc in MSVCS]
    await asyncio.gather(*tasks)

@asynccontextmanager
async def lifespan(_app):
    scheduler = AsyncIOScheduler()
    scheduler.add_job(scheduled_health_check, "interval", seconds=30)
    scheduler.start()
    await scheduled_health_check()  # primer chequeo inmediato
    try:
        yield
    finally:
        scheduler.shutdown()

app = FastAPI(lifespan=lifespan)

@app.get("/monitor")
async def get_health_status():
    return health_status