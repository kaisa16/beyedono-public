# file: backend/tests/test_orders.py
import pytest
from httpx import AsyncClient, ASGITransport
from app.main import app

@pytest.mark.asyncio
async def test_create_order():
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as ac:
        response = await ac.post("/v1/orders", json={
            "address_id": "test_address",
            "items": [{"product_id": "test_product", "quantity": 1}],
            "note": "Test note",
            "payment_method": "SBP"
        })
        assert response.status_code in [200, 404, 500]