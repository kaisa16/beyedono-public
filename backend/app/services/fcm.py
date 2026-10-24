# file: backend/app/services/fcm.py
import httpx

async def send_push_notification(token: str, title: str, body: str, data: dict = None):
    """
    Заглушка для отправки Push через FCM HTTP v1.
    В продакшене требует OAuth2 токен сервисного аккаунта.
    """
    # payload = {
    #     "message": {
    #         "token": token,
    #         "notification": {"title": title, "body": body},
    #         "data": data or {}
    #     }
    # }
    # await httpx.AsyncClient().post("https://fcm.googleapis.com/v1/projects/YOUR_PROJECT/messages:send", json=payload, headers=...)
    pass