# file: backend/app/core/config.py
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    PROJECT_NAME: str = "Beyedono API"
    API_V1_STR: str = "/v1"
    
    SECRET_KEY: str = "super_secret_jwt_key_change_in_production"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 7
    
    DATABASE_URL: str = "postgresql+asyncpg://beyedono:beyedono_secret@localhost:5432/beyedono_db"
    
    YOOKASSA_SHOP_ID: str = "your_shop_id"
    YOOKASSA_SECRET_KEY: str = "your_secret_key"
    YOOKASSA_RETURN_URL: str = "beyedono://payment/return"
    
    class Config:
        env_file = ".env"

settings = Settings()