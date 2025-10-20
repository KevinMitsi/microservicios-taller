# 🚀 Guía Completa de Configuración Jenkins y SonarQube para MSVC-Security

## 📋 Índice
1. [Requisitos Previos](#requisitos-previos)
2. [Configuración de Jenkins](#configuración-de-jenkins)
3. [Configuración de SonarQube](#configuración-de-sonarqube)
4. [Configuración del Pipeline](#configuración-del-pipeline)
5. [Ejecución y Verificación](#ejecución-y-verificación)
6. [Interpretación de Reportes](#interpretación-de-reportes)

---

## 📦 Requisitos Previos

### Software Necesario:
- Docker y Docker Compose instalados
- Git configurado
- Java 21 (JDK)
- Maven 3.8+

### Servicios Docker:
Asegúrate de que tu `docker-compose.yml` incluya:
- Jenkins (puerto 8084)
- SonarQube (puerto 9000)
- PostgreSQL (para SonarQube)

---

## 🔧 Configuración de Jenkins

### Paso 1: Iniciar Jenkins

```bash
docker-compose up -d jenkins
```

### Paso 2: Obtener Contraseña Inicial

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Paso 3: Acceder a Jenkins
1. Abrir navegador en `http://localhost:8084`
2. Pegar la contraseña inicial
3. Seleccionar "Install suggested plugins"

### Paso 4: Instalar Plugins Adicionales

Ve a **Manage Jenkins** → **Manage Plugins** → **Available** e instala:

#### Plugins Esenciales:
- ✅ **Maven Integration Plugin**
- ✅ **Docker Pipeline Plugin**
- ✅ **SonarQube Scanner Plugin**
- ✅ **JaCoCo Plugin**
- ✅ **Cucumber Reports Plugin**
- ✅ **HTML Publisher Plugin**
- ✅ **Git Plugin**
- ✅ **Pipeline Plugin**
- ✅ **Blue Ocean** (opcional, para UI moderna)

### Paso 5: Configurar Herramientas Globales

Ve a **Manage Jenkins** → **Global Tool Configuration**:

#### 1. Configurar JDK:
- Nombre: `JDK-21`
- JAVA_HOME: `/usr/lib/jvm/java-21-openjdk` (dentro del contenedor)
- O seleccionar "Install automatically"

#### 2. Configurar Maven:
- Nombre: `Maven`
- Versión: `3.9.0` o superior
- Seleccionar "Install automatically"

#### 3. Configurar SonarQube Scanner:
- Nombre: `SonarQube Scanner`
- Versión: Latest
- Seleccionar "Install automatically"

### Paso 6: Configurar Credenciales

Ve a **Manage Jenkins** → **Manage Credentials** → **(global)** → **Add Credentials**:

#### 1. Docker Hub Credentials:
- Tipo: `Username with password`
- ID: `dockerhub-credentials`
- Username: `tu-usuario-dockerhub`
- Password: `tu-token-dockerhub`
- Description: `Docker Hub Credentials`

#### 2. SonarQube Token:
- Tipo: `Secret text`
- Secret: `tu-token-sonarqube` (lo obtendrás después)
- ID: `sonarqube-token`
- Description: `SonarQube Authentication Token`

---

## 🔍 Configuración de SonarQube

### Paso 1: Iniciar SonarQube

```bash
docker-compose up -d sonarqube postgres
```

Espera aproximadamente 2-3 minutos para que SonarQube inicie completamente.

### Paso 2: Acceder a SonarQube
1. Abrir navegador en `http://localhost:9000`
2. Credenciales por defecto:
   - Usuario: `admin`
   - Password: `admin`
3. Cambiar contraseña al primer acceso

### Paso 3: Crear Token de Autenticación

1. Ir a **Account** → **Security** → **Tokens**
2. Nombre: `jenkins-integration`
3. Tipo: `Global Analysis Token`
4. Generar y **copiar el token** (no se mostrará nuevamente)
5. Agregar este token a las credenciales de Jenkins (Paso 6.2 anterior)

### Paso 4: Crear Proyecto en SonarQube

1. Clic en **Create Project** → **Manually**
2. Project key: `msvc-security`
3. Display name: `MSvc Security`
4. Main branch name: `main`
5. Clic en **Set Up**

### Paso 5: Configurar Quality Gate (Opcional)

1. Ir a **Quality Gates**
2. Seleccionar el Quality Gate por defecto o crear uno nuevo
3. Configurar umbrales según tus necesidades:
   - Coverage: > 80%
   - Bugs: 0
   - Vulnerabilities: 0
   - Code Smells: < 10

---

## 🔗 Configuración del Pipeline en Jenkins

### Paso 1: Configurar Servidor SonarQube en Jenkins

1. Ir a **Manage Jenkins** → **Configure System**
2. Buscar sección **SonarQube servers**
3. Clic en **Add SonarQube**:
   - Name: `SonarQube`
   - Server URL: `http://sonarqube:9000`
   - Server authentication token: Seleccionar `sonarqube-token`

### Paso 2: Crear Pipeline Job

1. En Jenkins, clic en **New Item**
2. Nombre: `msvc-security-pipeline`
3. Seleccionar: **Pipeline**
4. Clic en **OK**

### Paso 3: Configurar Pipeline

#### Opción A: Pipeline desde SCM (Recomendado)
1. En la sección **Pipeline**:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: `https://github.com/tu-usuario/microservicios-taller.git`
   - Branch: `*/main` o `*/develop`
   - Script Path: `msvc-security/Jenkinsfile`

#### Opción B: Pipeline Script Directo
Copiar el contenido del archivo `Jenkinsfile` directamente en el campo **Pipeline Script**

### Paso 4: Configurar Triggers (Opcional)

En la configuración del Job:
- ✅ **GitHub hook trigger for GITScm polling** (para integración con GitHub)
- ✅ **Poll SCM**: `H/5 * * * *` (verificar cada 5 minutos)

---

## ▶️ Ejecución y Verificación

### Paso 1: Ejecutar el Pipeline

1. En el Job, clic en **Build Now**
2. Observar la ejecución en tiempo real

### Paso 2: Verificar Etapas del Pipeline

El pipeline ejecutará las siguientes etapas:

#### ✅ Etapa 1: Checkout
- Clona el repositorio del proyecto
- Muestra información del último commit

#### ✅ Etapa 2: Build
- Compila el código fuente
- Verifica que no hay errores de compilación

#### ✅ Etapa 3: Unit & Integration Tests
- Ejecuta todas las pruebas unitarias
- Ejecuta pruebas de integración con Cucumber
- Genera reportes de:
  - JUnit (resultados de tests)
  - JaCoCo (cobertura de código)
  - Cucumber JSON y HTML (pruebas BDD)

#### ✅ Etapa 4: SonarQube Analysis
- Envía el código y resultados de tests a SonarQube
- Analiza:
  - Bugs
  - Vulnerabilidades
  - Code Smells
  - Duplicación
  - Cobertura de código

#### ✅ Etapa 5: Quality Gate
- Espera el resultado del análisis de SonarQube
- Verifica que se cumplan los umbrales definidos
- **Aborta el pipeline si no cumple los criterios**

#### ✅ Etapa 6: Package
- Empaqueta la aplicación en un JAR
- Archiva el artefacto

#### ✅ Etapa 7: Build Docker Image
- Construye la imagen Docker
- Etiqueta con número de build y 'latest'

#### ✅ Etapa 8: Push to Docker Hub (solo en rama main)
- Publica la imagen en Docker Hub
- Disponible para deployment

#### ✅ Etapa 9: Generate Test Report Summary
- Genera un resumen consolidado de todos los reportes

---

## 📊 Interpretación de Reportes

### 1. JUnit Test Results

**Ubicación:** En la página del build, sección **Test Result**

**Información disponible:**
- ✅ Total de tests ejecutados
- ✅ Tests exitosos
- ❌ Tests fallidos
- ⏭️ Tests omitidos
- 📈 Tendencia histórica
- 📝 Detalles de cada test

**¿Cómo interpretarlo?**
- **100% de tests pasando**: Excelente, el código cumple con los requisitos
- **Tests fallando**: Revisar logs de cada test fallido
- **Tests omitidos**: Verificar si es intencional

### 2. JaCoCo Coverage Report

**Ubicación:** En la página del build, sección **Coverage Report**

**Métricas clave:**
- **Instruction Coverage**: % de instrucciones ejecutadas
- **Branch Coverage**: % de ramas (if/else) ejecutadas
- **Line Coverage**: % de líneas ejecutadas
- **Method Coverage**: % de métodos ejecutados
- **Class Coverage**: % de clases cubiertas

**Rangos recomendados:**
- 🟢 **80-100%**: Excelente cobertura
- 🟡 **60-79%**: Cobertura aceptable
- 🔴 **<60%**: Cobertura baja, añadir más tests

### 3. Cucumber BDD Report

**Ubicación:** 
- En la página del build: **Cucumber reports**
- Reporte HTML: **Cucumber HTML Report**

**Información disponible:**
- ✅ Escenarios ejecutados
- ✅ Steps exitosos/fallidos
- 📊 Duración de cada escenario
- 📈 Tendencias
- 🔍 Detalles de fallos con stacktrace

**Estructura del reporte:**
```
Features (Funcionalidades)
  └── Scenarios (Escenarios)
       └── Steps (Pasos)
            ├── Given (Dado)
            ├── When (Cuando)
            └── Then (Entonces)
```

### 4. SonarQube Quality Report

**Ubicación:** `http://localhost:9000/dashboard?id=msvc-security`

**Métricas principales:**

#### A. Bugs 🐛
- **Definición**: Errores de código que pueden causar comportamiento incorrecto
- **Objetivo**: 0 bugs
- **Severidades**: Blocker, Critical, Major, Minor, Info

#### B. Vulnerabilities 🔒
- **Definición**: Problemas de seguridad
- **Objetivo**: 0 vulnerabilidades
- **Ejemplos**: SQL Injection, XSS, contraseñas hardcodeadas

#### C. Code Smells 👃
- **Definición**: Problemas de mantenibilidad del código
- **Objetivo**: Rating A (< 5% de deuda técnica)
- **Ejemplos**: Código duplicado, métodos muy largos, alta complejidad

#### D. Coverage 📊
- **Definición**: Porcentaje de código cubierto por tests
- **Objetivo**: > 80%

#### E. Duplications 📋
- **Definición**: Porcentaje de código duplicado
- **Objetivo**: < 3%

#### F. Technical Debt ⏱️
- **Definición**: Tiempo estimado para resolver todos los code smells
- **Rating**:
  - A: ≤ 5% del tiempo de desarrollo
  - B: 6-10%
  - C: 11-20%
  - D: 21-50%
  - E: > 50%

---

## 🎯 Checklist de Verificación Exitosa

Después de ejecutar el pipeline, verifica:

### ✅ En Jenkins:
- [ ] Build completado con status **SUCCESS** (verde)
- [ ] Todos los tests pasando (0 failures)
- [ ] Cobertura de código > 80%
- [ ] Reportes de Cucumber generados
- [ ] Artefactos archivados correctamente
- [ ] Imagen Docker construida

### ✅ En SonarQube:
- [ ] Quality Gate: **PASSED**
- [ ] Bugs: 0
- [ ] Vulnerabilities: 0
- [ ] Coverage: > 80%
- [ ] Duplications: < 3%
- [ ] Maintainability Rating: A o B

---

## 🔧 Solución de Problemas Comunes

### Problema 1: "No se puede conectar a SonarQube"
**Solución:**
```bash
# Verificar que SonarQube está corriendo
docker ps | grep sonarqube

# Verificar logs
docker logs sonarqube

# Reiniciar SonarQube si es necesario
docker-compose restart sonarqube
```

### Problema 2: "Tests fallan en Jenkins pero pasan localmente"
**Posibles causas:**
- Dependencias de servicios externos (MongoDB, RabbitMQ)
- Variables de entorno faltantes
- Perfiles de Spring incorrectos

**Solución:**
Agregar servicios necesarios en `docker-compose.yml` y configurar network para que Jenkins los acceda.

### Problema 3: "Quality Gate falla"
**Solución:**
1. Revisar el dashboard de SonarQube
2. Identificar las métricas que no cumplen
3. Resolver issues prioritarios (Bugs y Vulnerabilities primero)
4. Añadir más tests para mejorar coverage

### Problema 4: "No se generan reportes de Cucumber"
**Verificar:**
```bash
# Verificar que los tests de Cucumber se ejecutan
mvn test -Dtest=TestRunner

# Verificar que se genera el JSON
ls -la target/cucumber-reports/

# Verificar configuración en pom.xml
```

---

## 📈 Mejores Prácticas

### 1. Commits y Branches
- ✅ Commit frecuente con mensajes descriptivos
- ✅ Usar branches: `main`, `develop`, `feature/*`
- ✅ Pull Request antes de merge a `main`

### 2. Tests
- ✅ Escribir tests antes de código (TDD)
- ✅ Mantener cobertura > 80%
- ✅ Tests unitarios rápidos (< 1 segundo)
- ✅ Tests de integración para flujos completos

### 3. Calidad de Código
- ✅ Resolver Bugs y Vulnerabilities inmediatamente
- ✅ Refactorizar Code Smells regularmente
- ✅ Revisar Code Reviews en SonarQube
- ✅ Mantener Quality Gate en PASSED

### 4. Pipeline
- ✅ Pipeline rápido (< 10 minutos ideal)
- ✅ Fail fast (detectar problemas temprano)
- ✅ Archivar artefactos importantes
- ✅ Notificaciones de fallos

---

## 📞 Comandos Útiles

### Ver logs de Jenkins:
```bash
docker logs -f jenkins
```

### Ver logs de SonarQube:
```bash
docker logs -f sonarqube
```

### Ejecutar tests localmente:
```bash
cd msvc-security
mvn clean test
mvn verify
```

### Ver reportes localmente:
```bash
# Abrir reporte de cobertura
start target/site/jacoco/index.html

# Abrir reporte de Cucumber
start target/cucumber-report-html/overview-features.html
```

### Limpiar todo y empezar de nuevo:
```bash
docker-compose down -v
docker-compose up -d
```

---

## 🎓 Conclusión

Con esta configuración, has implementado un pipeline completo de CI/CD que:

1. ✅ **Clona** el proyecto automáticamente
2. ✅ **Compila** y verifica la sintaxis
3. ✅ **Ejecuta** pruebas unitarias y de integración (Cucumber)
4. ✅ **Analiza** la calidad del código con SonarQube
5. ✅ **Genera** reportes detallados (JUnit, JaCoCo, Cucumber)
6. ✅ **Verifica** Quality Gate
7. ✅ **Empaqueta** la aplicación
8. ✅ **Construye** imagen Docker
9. ✅ **Publica** en Docker Hub
10. ✅ **Reporta** resultados consolidados

Todos los reportes están integrados y son visibles directamente en Jenkins, permitiendo un seguimiento completo del estado del proyecto.

---

**¡Éxito en tu pipeline de CI/CD! 🚀**

