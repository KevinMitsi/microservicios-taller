@echo off
echo ================================================
echo   Verificacion de Servicios CI/CD
echo ================================================
echo.

echo [Estado de contenedores]
docker-compose ps

echo.
echo ================================================
echo [Verificando Jenkins...]
echo ================================================
curl -s -o nul -w "HTTP Status: %%{http_code}\n" http://localhost:9090

echo.
echo ================================================
echo [Verificando SonarQube...]
echo ================================================
curl -s -o nul -w "HTTP Status: %%{http_code}\n" http://localhost:9000

echo.
echo ================================================
echo [Logs recientes de Jenkins]
echo ================================================
docker logs --tail 20 jenkins

echo.
echo ================================================
echo [Logs recientes de SonarQube]
echo ================================================
docker logs --tail 20 sonarqube

echo.
echo ================================================
echo Para obtener la contrasena de Jenkins:
echo ================================================
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

echo.
pause

