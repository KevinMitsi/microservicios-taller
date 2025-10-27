# 🚀 INICIO RÁPIDO - CI/CD para MSVC-Security

## ⚡ Configuración en 5 Minutos

### Paso 1: Iniciar Servicios (2 minutos)

Abre una terminal CMD en la carpeta del proyecto y ejecuta:

```cmd
start-ci-cd.cmd
```

O manualmente:
```cmd
docker-compose up -d jenkins sonarqube sonar-db mongo-security
```

**Espera 2-3 minutos** para que los servicios inicien completamente.

---

### Paso 2: Configurar Jenkins (2 minutos)

1. **Obtén la contraseña inicial:**
   ```cmd
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```
   Copia la contraseña que aparece.

2. **Accede a Jenkins:**
   - Abre: http://localhost:9090
   - Pega la contraseña
   - Click en "Install suggested plugins"
   - Espera a que termine la instalación

3. **Instala plugins adicionales:**
   - Ve a: **Manage Jenkins** → **Manage Plugins** → **Available**
   - Busca e instala:
     - `Cucumber Reports`
     - `HTML Publisher`
     - `JaCoCo`
     - `SonarQube Scanner`
   - Click en "Install without restart"

4. **Configura herramientas:**
   - Ve a: **Manage Jenkins** → **Global Tool Configuration**
   - **JDK**: Nombre = `JDK-21`, selecciona "Install automatically"
   - **Maven**: Nombre = `Maven`, selecciona "Install automatically"
   - **SonarQube Scanner**: Selecciona "Install automatically"
   - Click en "Save"

---

### Paso 3: Configurar SonarQube (1 minuto)

1. **Accede a SonarQube:**
   - Abre: http://localhost:9000
   - Login: `admin` / `admin`
   - Cambia la contraseña cuando te lo pida

2. **Genera un token:**
   - Click en tu avatar (arriba derecha) → **My Account**
   - Tab **Security**
   - Token name: `jenkins-integration`
   - Type: `Global Analysis Token`
   - Click en **Generate**
   - **¡IMPORTANTE!** Copia el token (no se mostrará de nuevo)

3. **Agrega el token a Jenkins:**
   - Ve a Jenkins: **Manage Jenkins** → **Manage Credentials**
   - Click en **(global)** → **Add Credentials**
   - Kind: `Secret text`
   - Secret: [pega el token de SonarQube]
   - ID: `sonarqube-token`
   - Description: `SonarQube Token`
   - Click en "Create"

4. **Configura el servidor SonarQube en Jenkins:**
   - Ve a: **Manage Jenkins** → **Configure System**
   - Busca la sección **SonarQube servers**
   - Click en **Add SonarQube**
   - Name: `SonarQube`
   - Server URL: `http://sonarqube:9000`
   - Server authentication token: Selecciona `sonarqube-token`
   - Click en "Save"

---

### Paso 4: Crear el Pipeline Job

1. En Jenkins, click en **New Item**
2. Item name: `msvc-security-pipeline`
3. Selecciona: **Pipeline**
4. Click en **OK**

5. En la configuración:
   - **General**: Descripción = "Pipeline CI/CD para msvc-security"
   - **Pipeline**:
     - Definition: `Pipeline script from SCM`
     - SCM: `Git`
     - Repository URL: `https://github.com/tu-usuario/microservicios-taller.git`
     - Branch Specifier: `*/main` (o `*/develop`)
     - Script Path: `msvc-security/Jenkinsfile`
   - Click en **Save**

---

### Paso 5: Ejecutar el Pipeline ▶️

1. En el job `msvc-security-pipeline`, click en **Build Now**
2. Observa la ejecución en tiempo real (click en el número del build)
3. Click en **Console Output** para ver los logs

---

## 📊 Ver los Reportes

Una vez que el build termine exitosamente:

### En Jenkins:
- **Test Result** → Resultados de pruebas unitarias
- **Coverage Report** → Cobertura de código (JaCoCo)
- **Cucumber reports** → Pruebas BDD
- **Cucumber HTML Report** → Reporte visual mejorado

### En SonarQube:
- Ve a: http://localhost:9000
- Click en el proyecto **msvc-security**
- Explora: Issues, Coverage, Duplications

---

## ✅ Verificación Exitosa

Si todo funcionó correctamente, deberías ver:

✅ Build status: **SUCCESS** (verde)
✅ Tests: **Todos pasando**
✅ Coverage: **> 60%** (idealmente > 80%)
✅ Quality Gate: **PASSED**
✅ Reportes de Cucumber generados

---

## 🆘 Problemas Comunes

### Jenkins no inicia
```cmd
docker logs jenkins
docker-compose restart jenkins
```

### SonarQube no inicia
```cmd
docker logs sonarqube
# Espera 3 minutos más, SonarQube es lento al iniciar
```

### Pipeline falla en Quality Gate
- Ve a SonarQube y revisa qué métrica falló
- Es normal en el primer análisis si hay código legacy
- Puedes desactivar temporalmente el Quality Gate o ajustar los umbrales

### Tests fallan
```cmd
# Asegúrate de que MongoDB y RabbitMQ estén corriendo
docker-compose up -d mongo-security rabbitmq

# Ejecuta tests localmente para verificar
cd msvc-security
mvn clean test
```

---

## 📚 Documentación Completa

Para más detalles, consulta:
- **CONFIGURACION_JENKINS_SONAR.md** - Guía detallada paso a paso
- **RESUMEN_CONFIGURACION.md** - Resumen ejecutivo
- **COMO_ABRIR_REPORTES.md** - Guía de reportes

---

## 🎯 Próximos Pasos

Una vez que el pipeline funcione:

1. **Configura Webhooks** en GitHub para ejecución automática
2. **Agrega credenciales de Docker Hub** para publicar imágenes
3. **Configura notificaciones** (email, Slack)
4. **Ajusta Quality Gate** según tus estándares
5. **Replica la configuración** para otros microservicios

---

**¡Listo para CI/CD! 🚀**

