#!/usr/bin/env python3
import requests
import sys

def check_service(name, url, expected_codes=[200]):
    try:
        response = requests.get(url, timeout=5)
        status = response.status_code

        if status in expected_codes:
            print(f"✓ {name} [DISPONIBLE] - Puerto {url.split(':')[-1].split('/')[0]} (HTTP {status})")
            return True
        else:
            print(f"✗ {name} [NO DISPONIBLE] - HTTP {status}")
            return False
    except requests.exceptions.ConnectionError:
        print(f"✗ {name} [NO DISPONIBLE] - Sin conexión")
        return False
    except Exception as e:
        print(f"✗ {name} [ERROR] - {str(e)}")
        return False

def main():
    print("=== VERIFICACIÓN RÁPIDA DE SERVICIOS ===\n")

    services = [
        ("msvc-security", "http://localhost:8080/health", [200, 403]),
        ("msvc-saludo", "http://localhost:80/health", [200, 403]),
        ("msvc-consumer", "http://localhost:8081/health", [200, 404]),  # 404 está bien
        ("msvc-orchestrator", "http://localhost:8083/health", [200]),
        ("msvc-monitoring", "http://localhost:8000/health", [200]),
    ]

    all_available = True
    for name, url, expected in services:
        if not check_service(name, url, expected):
            all_available = False

    print("\n" + "="*50)
    if all_available:
        print("RESULTADO: ✓ TODOS LOS SERVICIOS ESTÁN DISPONIBLES")
        sys.exit(0)
    else:
        print("RESULTADO: ✗ ALGUNOS SERVICIOS NO ESTÁN DISPONIBLES")
        sys.exit(1)

if __name__ == "__main__":
    main()
