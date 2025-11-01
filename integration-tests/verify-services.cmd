@echo off
REM Verificación rápida del estado de los servicios
echo ====================================================================
echo VERIFICACION RAPIDA DE SERVICIOS
echo ====================================================================
echo.

cd /d "%~dp0"

echo Ejecutando smoke test...
echo.

call mvn test -Dtest=SmokeTest -q

echo.
pause

