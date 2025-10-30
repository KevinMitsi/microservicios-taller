from pydantic import BaseModel, EmailStr
from typing import List

class MicroserviceDTO(BaseModel):
    name: str
    endpoint: str  # URL base, ej: http://host:port
    frequency: int  # segundos
    emails: List[EmailStr]

