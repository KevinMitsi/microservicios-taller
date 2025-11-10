@echo off
echo === Probando endpoints de smoke tests ===
echo.

echo Probando msvc-security en puerto 8080...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/smoke
echo.

echo Probando msvc-saludo en puerto 9090...
curl -s -o nul -w "%%{http_code}" http://localhost:9090/smoke
echo.

echo Probando msvc-consumer en puerto 8081...
curl -s -o nul -w "%%{http_code}" http://localhost:8081/smoke
echo.

echo Probando msvc-orchestrator en puerto 8083...
curl -s -o nul -w "%%{http_code}" http://localhost:8083/health
echo.

echo Probando msvc-monitoring en puerto 8000...
curl -s -o nul -w "%%{http_code}" http://localhost:8000/health
echo.

echo === Test completo ===
