@echo off
REM Script para ejecutar las pruebas de integración desde el directorio raíz
echo ====================================================================
echo SISTEMA DE PRUEBAS DE INTEGRACION - MICROSERVICIOS
echo ====================================================================
echo.

cd /d "%~dp0integration-tests"

if not exist pom.xml (
    echo [ERROR] No se encuentra el modulo de integration-tests
    echo Por favor ejecuta este script desde el directorio raiz del proyecto
    pause
    exit /b 1
)

call run-tests.cmd

