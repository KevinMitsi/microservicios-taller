import pytest
from fastapi.testclient import TestClient
from unittest.mock import patch, MagicMock
from main import app, registered_msvcs, health_status

client = TestClient(app)

@pytest.fixture(autouse=True)
def clear_state():
    registered_msvcs.clear()
    health_status.clear()
    # Inicializa el scheduler si no existe
    if not hasattr(app.state, "scheduler"):
        app.state.scheduler = MagicMock()

def test_register_microservice_success():
    data = {
        "name": "test",
        "endpoint": "http://localhost:8000",
        "frequency": 10,
        "emails": ["test@example.com"]
    }
    resp = client.post("/register", json=data)
    assert resp.status_code == 200
    assert resp.json()["name"] == "test"

def test_register_microservice_duplicate():
    data = {
        "name": "test",
        "endpoint": "http://localhost:8000",
        "frequency": 10,
        "emails": ["test@example.com"]
    }
    client.post("/register", json=data)
    resp = client.post("/register", json=data)
    assert resp.status_code == 400

def test_get_health_status_empty():
    resp = client.get("/health")
    assert resp.status_code == 200
    assert resp.json() == {}

def test_get_health_status_msvc_not_found():
    resp = client.get("/health/noexiste")
    assert resp.status_code == 404

@patch("main.email_sender.send_email")
@patch("main.httpx.AsyncClient")
def test_check_health_and_notify(mock_client, mock_send_email):
    mock_response_ok = MagicMock(status_code=200, json=lambda: {"ok": True})
    mock_response_err = MagicMock(status_code=500)
    mock_client.return_value.__aenter__.return_value.get.side_effect = [
        mock_response_ok, mock_response_err, Exception("fail")
    ]
    from main import check_health
    registered_msvcs["test"] = {
        "endpoint": "http://localhost:8000",
        "frequency": 10,
        "emails": ["test@example.com"]
    }
    import asyncio
    asyncio.run(check_health("test"))
    assert "test" in health_status
    mock_send_email.assert_called_once()