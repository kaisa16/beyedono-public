# file: backend/app/services/yookassa.py
import uuid
import httpx
from app.core.config import settings

async def create_yookassa_payment(amount: float, order_id: str, return_url: str) -> dict:
    idempotence_key = str(uuid.uuid4())
    auth = (settings.YOOKASSA_SHOP_ID, settings.YOOKASSA_SECRET_KEY)
    
    payload = {
        "amount": {
            "value": f"{amount:.2f}",
            "currency": "RUB"
        },
        "confirmation": {
            "type": "redirect",
            "return_url": return_url
        },
        "capture": True,
        "description": f"Оплата заказа #{order_id}",
        "metadata": {
            "order_id": order_id
        }
    }
    
    headers = {
        "Idempotence-Key": idempotence_key,
        "Content-Type": "application/json"
    }

    async with httpx.AsyncClient() as client:
        response = await client.post(
            "https://api.yookassa.ru/v3/payments",
            json=payload,
            auth=auth,
            headers=headers
        )
        response.raise_for_status()
        return response.json()