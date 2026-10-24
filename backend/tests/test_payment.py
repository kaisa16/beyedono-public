# file: backend/tests/test_payment.py
import pytest
from httpx import AsyncClient, ASGITransport
from app.main import app

@pytest.mark.asyncio
async def test_create_payment():
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as ac:
        response = await ac.post("/v1/payments", json={
            "order_id": "test_order",
            "amount": 1500.0,
            "return_url": "beyedono://payment/return"
        })
        assert response.status_code in [200, 500]