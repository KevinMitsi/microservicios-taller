@echo off
REM Script para ejecutar solo las pruebas JUnit
echo ====================================================================
echo EJECUTANDO PRUEBAS JUNIT
echo ====================================================================
echo.

cd /d "%~dp0"

call mvn test -Dtest=SystemIntegrationTest

pause

