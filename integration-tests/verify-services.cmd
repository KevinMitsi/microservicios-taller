@echo off
echo ============================================
echo Verificando estado de microservicios
echo ============================================
echo.

echo Verificando msvc-security (puerto 8080)...
curl -s -o nul -w "Status: %%{http_code}\n" http://localhost:8080/actuator/health
if errorlevel 1 (
    echo [FALLO] msvc-security no responde
) else (
    echo [OK] msvc-security responde
)
echo.

echo Verificando msvc-saludo (puerto 80)...
curl -s -o nul -w "Status: %%{http_code}\n" http://localhost:80/actuator/health
if errorlevel 1 (
    echo [FALLO] msvc-saludo no responde
) else (
    echo [OK] msvc-saludo responde
)
echo.

echo Verificando msvc-consumer (puerto 8081)...
curl -s -o nul -w "Status: %%{http_code}\n" http://localhost:8081/actuator/health
if errorlevel 1 (
    echo [FALLO] msvc-consumer no responde
) else (
    echo [OK] msvc-consumer responde
)
echo.

echo Verificando msvc-orchestrator (puerto 8083)...
curl -s -o nul -w "Status: %%{http_code}\n" http://localhost:8083/actuator/health
if errorlevel 1 (
    echo [FALLO] msvc-orchestrator no responde
) else (
    echo [OK] msvc-orchestrator responde
)
echo.

echo ============================================
echo Intentando registro de usuario de prueba
echo ============================================
echo.

curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser_%RANDOM%\",\"email\":\"test%RANDOM%@test.com\",\"password\":\"Test123!\",\"firstName\":\"Test\",\"lastName\":\"User\"}"

echo.
echo.
echo ============================================
echo Verificacion completa
echo ============================================
echo.
echo Si todos los servicios respondieron OK, puedes ejecutar los tests.
echo Si alguno fallo, asegurate de iniciar los servicios con: docker-compose up -d
echo.
pause

