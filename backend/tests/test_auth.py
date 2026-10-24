# file: backend/tests/test_auth.py
import pytest
from httpx import AsyncClient, ASGITransport
from app.main import app

@pytest.mark.asyncio
async def test_register_and_login():
    async with AsyncClient(transport=ASGITransport(app=app), base_url="http://test") as ac:
        reg_response = await ac.post("/v1/auth/register", json={
            "name": "Test User",
            "email": "test@test.com",
            "password": "password123"
        })
        assert reg_response.status_code in [200, 400]

        login_response = await ac.post("/v1/auth/login", json={
            "email": "test@test.com",
            "password": "password123"
        })
        assert login_response.status_code == 200
        assert "access_token" in login_response.json()