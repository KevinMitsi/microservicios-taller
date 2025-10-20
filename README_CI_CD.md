# Guía Rápida - Jenkins y SonarQube

## 🚀 Inicio Rápido

### 1. Levantar los Servicios

```bash
# Levantar Jenkins y SonarQube
docker-compose up -d jenkins sonarqube sonar-db

# Verificar que están corriendo
docker-compose ps
```

### 2. Acceder a Jenkins

**URL:** http://localhost:9090

**Obtener contraseña inicial:**
```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

**Pasos:**
1. Copiar la contraseña
2. Pegar en el navegador
3. Seleccionar "Install suggested plugins"
4. Crear usuario administrador
5. ✅ Jenkins listo

### 3. Acceder a SonarQube

**URL:** http://localhost:9000

**Credenciales iniciales:**
- Usuario: `admin`
- Contraseña: `admin`

**Pasos:**
1. Login con admin/admin
2. Cambiar contraseña cuando se solicite
3. Ir a: My Account > Security > Generate Token
4. Nombre del token: `msvc-security-token`
5. Guardar el token generado
6. ✅ SonarQube listo

### 4. Ejecutar Análisis de SonarQube

```bash
cd msvc-security

# Ejecutar tests con cobertura
mvn clean test

# Ejecutar análisis de SonarQube
mvn sonar:sonar -Dsonar.login=TU_TOKEN_AQUI

# O todo junto
mvn clean verify sonar:sonar -Dsonar.login=TU_TOKEN_AQUI
```

### 5. Ver Reportes

**Cucumber Reports:**
```bash
# Generar reportes
mvn verify

# Abrir reporte
start target/cucumber-report-html/cucumber-html-reports/overview-features.html
```

**JaCoCo Coverage:**
```bash
# Después de ejecutar los tests
start target/site/jacoco/index.html
```

**SonarQube Dashboard:**
- Abrir: http://localhost:9000
- Ver proyecto: `msvc-security`

## 📊 Resumen de lo Implementado

### ✅ Jenkins
- Motor de CI/CD configurado
- Puerto 8084
- Acceso a Docker para construir imágenes
- Jenkinsfile incluido en msvc-security

### ✅ SonarQube
- Análisis de calidad de código
- Puerto 9000
- Base de datos PostgreSQL
- Configuración en pom.xml

### ✅ JavaFaker
- Generación de datos aleatorios para tests
- Clase utilitaria: `TestDataGenerator`
- Tests de ejemplo en `random-users.feature`

### ✅ Reportes Mejorados
- Cucumber HTML Reports (Masterthought)
- JaCoCo Code Coverage
- Integración con SonarQube

## 🔧 Comandos Útiles

```bash
# Ver logs de Jenkins
docker logs -f jenkins

# Ver logs de SonarQube
docker logs -f sonarqube

# Reiniciar servicios
docker-compose restart jenkins sonarqube

# Detener servicios
docker-compose stop jenkins sonarqube

# Ver estado
docker-compose ps
```

## 📁 Estructura de Archivos Creados

```
microservicios/
├── docker-compose.yml (actualizado)
├── JENKINS_SONARQUBE.md (documentación completa)
├── TESTING_TOOLS.md (documentación de testing)
└── msvc-security/
    ├── Jenkinsfile (pipeline CI/CD)
    ├── pom.xml (actualizado con JaCoCo y SonarQube)
    ├── src/test/
    │   ├── java/com/taller/msvc_security/
    │   │   ├── utils/TestDataGenerator.java
    │   │   └── stepdefinitions/UserSteps.java (con JavaFaker)
    │   └── resources/features/
    │       ├── users.feature
    │       └── random-users.feature
    └── target/ (después de ejecutar tests)
        ├── cucumber-report-html/
        ├── site/jacoco/
        └── cucumber-reports/
```

## 🎯 Próximos Pasos

1. **Configurar Webhook en GitHub:**
   - Settings > Webhooks
   - URL: `http://TU_IP:9090/github-webhook/`
   - Events: Push, Pull Request

2. **Crear Job en Jenkins:**
   - New Item > Pipeline
   - Pipeline from SCM
   - Git: tu repositorio
   - Script Path: `msvc-security/Jenkinsfile`

3. **Configurar Quality Gate en SonarQube:**
   - Quality Gates > Create
   - Definir umbrales de calidad
   - Asignar al proyecto

4. **Automatizar Despliegue:**
   - Configurar credenciales de Docker Hub
   - Configurar credenciales de servidores
   - Habilitar deploy automático

## 📝 Notas Importantes

- Los datos se persisten en volúmenes Docker
- Primera ejecución de SonarQube puede tardar 2-3 minutos
- Jenkins requiere configuración adicional para ejecutar Docker
- Cambiar contraseñas por defecto en producción

Para más detalles, ver:
- `JENKINS_SONARQUBE.md` - Documentación completa
- `TESTING_TOOLS.md` - Herramientas de testing

