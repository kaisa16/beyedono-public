# file: backend/app/main.py
import os
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.routers import auth, menu, orders, payment, profile

app = FastAPI(title=settings.PROJECT_NAME, openapi_url=f"{settings.API_V1_STR}/openapi.json")

# Безопасная настройка CORS
# Берем домены из переменной окружения (через запятую), по умолчанию разрешаем только локальную разработку
allowed_origins_str = os.getenv("ALLOWED_ORIGINS", "http://localhost:3000,http://127.0.0.1:3000")
allowed_origins = [origin.strip() for origin in allowed_origins_str.split(",") if origin.strip()]

app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins,
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"],
    allow_headers=["Authorization", "Content-Type", "Accept"],
)

app.include_router(auth.router, prefix=f"{settings.API_V1_STR}/auth", tags=["auth"])
app.include_router(menu.router, prefix=f"{settings.API_V1_STR}/menu", tags=["menu"])
app.include_router(orders.router, prefix=f"{settings.API_V1_STR}/orders", tags=["orders"])
app.include_router(payment.router, prefix=f"{settings.API_V1_STR}/payments", tags=["payments"])
app.include_router(profile.router, prefix=f"{settings.API_V1_STR}/profile", tags=["profile"])

@app.get("/")
def root():
    return {"message": "Beyedono API is running"}
