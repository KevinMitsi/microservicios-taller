# settings.py (o donde definas tu configuración)
from pydantic_settings import BaseSettings, SettingsConfigDict
from pydantic import Field

class Settings(BaseSettings):
    # Configura Pydantic para buscar el archivo .env
    model_config = SettingsConfigDict(env_file='.env', env_file_encoding='utf-8')

    MAIL_USERNAME: str
    MAIL_PASSWORD: str


# Instancia la configuración para usarla en tu app
settings = Settings()