@echo off
REM Script para configurar Java 21 antes de ejecutar Maven

REM Buscar la instalación de Java 21
set JAVA_21_HOME=

REM Verificar ubicaciones comunes de instalación de Java 21
if exist "C:\Program Files\Java\jdk-21" (
    set JAVA_21_HOME=C:\Program Files\Java\jdk-21
) else if exist "C:\Program Files\Java\jdk-21.0.7" (
    set JAVA_21_HOME=C:\Program Files\Java\jdk-21.0.7
) else if exist "C:\Program Files (x86)\Java\jdk-21" (
    set JAVA_21_HOME=C:\Program Files (x86)\Java\jdk-21
) else if exist "%ProgramFiles%\Java\jdk-21" (
    set JAVA_21_HOME=%ProgramFiles%\Java\jdk-21
) else if exist "%ProgramFiles(x86)%\Java\jdk-21" (
    set JAVA_21_HOME=%ProgramFiles(x86)%\Java\jdk-21
)

if "%JAVA_21_HOME%"=="" (
    echo ERROR: No se encontró Java 21. Por favor, instala Java 21 o edita este archivo para especificar la ruta correcta.
    echo.
    echo Las ubicaciones buscadas fueron:
    echo - C:\Program Files\Java\jdk-21
    echo - C:\Program Files\Java\jdk-21.0.7
    echo - C:\Program Files (x86)\Java\jdk-21
    echo.
    echo Edita este archivo y configura JAVA_21_HOME manualmente con la ruta correcta de tu instalación de Java 21
    pause
    exit /b 1
)

echo Configurando JAVA_HOME a: %JAVA_21_HOME%
set JAVA_HOME=%JAVA_21_HOME%
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Verificando versión de Java:
java -version

echo.
echo JAVA_HOME configurado correctamente. Ahora puedes ejecutar Maven.
echo Ejemplo: mvn clean test
echo.

