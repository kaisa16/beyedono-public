# file: backend/tests/test_menu.py
import pytest
from httpx import AsyncClient, ASGITransport
from app.main import app

@pytest.mark.asyncio
async def test_get_menu():
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as ac:
        response = await ac.get("/v1/menu")
        assert response.status_code == 200
        assert "categories" in response.json()
        assert "products" in response.json()