@echo off
REM Script para ejecutar solo las pruebas Cucumber BDD
echo ====================================================================
echo EJECUTANDO PRUEBAS CUCUMBER BDD
echo ====================================================================
echo.

cd /d "%~dp0"

call mvn test -Dtest=CucumberTestRunner

if exist "target\cucumber-reports.html" (
    echo.
    echo Abriendo reporte HTML...
    start target\cucumber-reports.html
)

pause
@echo off
REM Script para ejecutar las pruebas de integración del sistema
REM Autor: Sistema de Microservicios
REM Fecha: 2025

echo ====================================================================
echo SISTEMA DE PRUEBAS DE INTEGRACION - MICROSERVICIOS
echo ====================================================================
echo.

REM Verificar que Java está instalado
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java no esta instalado o no esta en el PATH
    echo Por favor instala Java 21 antes de continuar
    pause
    exit /b 1
)

REM Verificar que Maven está instalado
mvn -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven no esta instalado o no esta en el PATH
    echo Por favor instala Maven 3.8+ antes de continuar
    pause
    exit /b 1
)

echo [OK] Prerequisitos verificados
echo.

REM Cambiar al directorio de pruebas de integración
cd /d "%~dp0"

echo ====================================================================
echo PASO 1: Verificando servicios
echo ====================================================================
echo.
echo Verificando que los servicios esten corriendo...
echo.

REM Verificar msvc-security (puerto 8080)
curl -s http://localhost:8080/api/health >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [WARNING] msvc-security no responde en puerto 8080
    echo.
    echo Por favor inicia los servicios con: docker-compose up -d
    choice /C YN /M "Deseas continuar de todas formas"
    if errorlevel 2 exit /b 1
) else (
    echo [OK] msvc-security esta disponible
)

REM Verificar msvc-saludo (puerto 80)
curl -s http://localhost:80/api/health >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] msvc-saludo esta disponible
)

REM Verificar msvc-consumer (puerto 8081)
curl -s http://localhost:8081/api/health >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] msvc-consumer esta disponible
)

REM Verificar msvc-orchestrator (puerto 8083)
curl -s http://localhost:8083/api/health >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] msvc-orchestrator esta disponible
)

echo.
echo ====================================================================
echo PASO 2: Limpiando compilaciones anteriores
echo ====================================================================
echo.

call mvn clean
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo al limpiar el proyecto
    pause
    exit /b 1
)

echo.
echo ====================================================================
echo PASO 3: Ejecutando pruebas de integracion
echo ====================================================================
echo.
echo Esto puede tomar varios minutos...
echo.

call mvn test
set TEST_RESULT=%ERRORLEVEL%

echo.
echo ====================================================================
echo RESULTADOS
echo ====================================================================
echo.

if %TEST_RESULT% EQU 0 (
    echo [EXITO] Todas las pruebas de integracion pasaron correctamente
    echo.
    echo Reportes generados:
    echo   - target\surefire-reports\
    echo   - target\cucumber-reports.html
    echo.
) else (
    echo [FALLO] Algunas pruebas fallaron
    echo.
    echo Por favor revisa los reportes en:
    echo   - target\surefire-reports\
    echo.
)

echo ====================================================================
echo.

REM Abrir reporte HTML si existe
if exist "target\cucumber-reports.html" (
    choice /C YN /M "Deseas abrir el reporte HTML de Cucumber"
    if errorlevel 1 if not errorlevel 2 (
        start target\cucumber-reports.html
    )
)

pause
exit /b %TEST_RESULT%

