import smtplib
from email.message import EmailMessage
from typing import List
import logging

class EmailSender:
    def __init__(self, smtp_server: str, smtp_port: int, username: str, app_password: str):
        self.smtp_server = smtp_server
        self.smtp_port = smtp_port
        self.username = username
        self.app_password = app_password

    def send_email(self, subject: str, body: str, to_emails: List[str]):
        msg = EmailMessage()
        msg["Subject"] = subject
        msg["From"] = self.username
        msg["To"] = ", ".join(to_emails)
        msg.set_content(body)
        try:
            with smtplib.SMTP_SSL(self.smtp_server, self.smtp_port) as smtp:
                smtp.login(self.username, self.app_password)
                smtp.send_message(msg)
            logging.info(f"Correo enviado a: {to_emails}")
        except Exception as e:
            logging.error(f"Error enviando correo: {e}")
            raise

# Ejemplo de configuración para Gmail:
# sender = EmailSender(
#     smtp_server="smtp.gmail.com",
#     smtp_port=465,
#     username="tu_email@gmail.com",
#     app_password="tu_clave_de_aplicacion"
# )
# sender.send_email("Asunto", "Cuerpo del mensaje", ["destino1@gmail.com", "destino2@gmail.com"])

