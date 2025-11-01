# Pruebas de Integración del Sistema de Microservicios

## Descripción

Este módulo contiene un conjunto completo de pruebas automatizadas de integración que verifican el correcto funcionamiento del sistema de microservicios como un conjunto único.

## Estructura del Proyecto

```
integration-tests/
├── pom.xml                          # Configuración Maven con dependencias
├── src/
│   ├── main/java/
│   │   └── com/taller/integration/
│   │       ├── IntegrationTestApplication.java    # Aplicación Spring Boot
│   │       ├── config/
│   │       │   └── IntegrationTestConfig.java     # Configuración de URLs y parámetros
│   │       ├── client/                             # Clientes REST para cada servicio
│   │       │   ├── SecurityServiceClient.java
│   │       │   ├── SaludoServiceClient.java
│   │       │   ├── ConsumerServiceClient.java
│   │       │   ├── OrchestratorServiceClient.java
│   │       │   └── MonitoringServiceClient.java
│   │       └── dto/                                # DTOs para requests/responses
│   │           ├── UserRegistrationRequest.java
│   │           ├── LoginRequest.java
│   │           └── NotificationRequest.java
│   └── test/
│       ├── java/com/taller/integration/
│       │   ├── tests/
│       │   │   └── SystemIntegrationTest.java     # Pruebas JUnit completas
│       │   └── bdd/
│       │       ├── SystemIntegrationSteps.java    # Steps de Cucumber
│       │       └── CucumberTestRunner.java        # Runner de Cucumber
│       └── resources/
│           ├── application.properties              # Configuración de pruebas
│           └── features/
│               └── system-integration.feature     # Escenarios BDD en español
```

## Servicios Verificados

Las pruebas cubren los siguientes microservicios:

1. **msvc-security** (Puerto 8080)
   - Registro de usuarios
   - Autenticación JWT
   - Gestión de usuarios

2. **msvc-saludo** (Puerto 80)
   - Servicio de saludos personalizados

3. **msvc-consumer** (Puerto 8081)
   - Orquestación de llamadas entre servicios
   - Integración de security y saludo

4. **msvc-orchestrator** (Puerto 8083)
   - Gestión de notificaciones
   - Canales y templates

5. **msvc-delivery** (Puerto 8082)
   - Envío de notificaciones por email/SMS

6. **msvc-monitoring** (Puerto 8000)
   - Monitoreo del estado de los servicios

## Casos de Prueba

### Pruebas JUnit (SystemIntegrationTest.java)

1. **Test 1: Health Checks** - Verifica que todos los servicios estén operativos
2. **Test 2: Autenticación** - Registro y login de usuarios
3. **Test 3: Servicio de Saludo** - Obtención de saludos personalizados
4. **Test 4: Consumer Service** - Flujo completo de integración
5. **Test 5: Gestión de Usuarios** - Listar y obtener detalles de usuarios
6. **Test 6: Orchestrator** - Canales y templates de notificación
7. **Test 7: Resiliencia** - Manejo de errores y reintentos
8. **Test 8: Monitoreo** - Estado del sistema
9. **Test 9: Performance** - Peticiones concurrentes
10. **Test 10: Flujo E2E** - Flujo completo desde registro hasta notificación

### Pruebas BDD con Cucumber (system-integration.feature)

- Verificación de disponibilidad de servicios
- Registro y autenticación de usuarios
- Flujo de saludo autenticado
- Flujo completo de consumer
- Manejo de errores de autenticación
- Resiliencia del sistema
- Consulta de canales de notificación

## Requisitos Previos

1. **Java 21** instalado
2. **Maven 3.8+** instalado
3. **Docker y Docker Compose** (para levantar los servicios)
4. Todos los microservicios deben estar corriendo

## Configuración

### 1. Iniciar todos los servicios

Desde el directorio raíz del proyecto:

```cmd
docker-compose up -d
```

Esperar a que todos los servicios estén listos (aproximadamente 1-2 minutos).

### 2. Configurar variables de entorno (opcional)

Si los servicios están en diferentes puertos o hosts, editar:
`src/test/resources/application.properties`

## Ejecución de las Pruebas

### Opción 1: Ejecutar todas las pruebas

```cmd
cd integration-tests
mvn clean test
```

### Opción 2: Ejecutar solo pruebas JUnit

```cmd
mvn test -Dtest=SystemIntegrationTest
```

### Opción 3: Ejecutar solo pruebas Cucumber

```cmd
mvn test -Dtest=CucumberTestRunner
```

### Opción 4: Ejecutar con Maven desde el directorio raíz

```cmd
mvn -pl integration-tests clean test
```

## Reportes

Después de ejecutar las pruebas, se generan varios reportes:

1. **Reporte Maven Surefire**
   - Ubicación: `target/surefire-reports/`
   - Formato: XML y TXT

2. **Reporte Cucumber HTML**
   - Ubicación: `target/cucumber-reports.html`
   - Abre en navegador para ver resultados detallados

3. **Reporte Cucumber JSON**
   - Ubicación: `target/cucumber.json`
   - Para integración con CI/CD

## Logs y Debugging

### Ver logs detallados

```cmd
mvn test -X
```

### Ver logs de REST Assured

Los logs de REST Assured se configuran en `application.properties`:
```properties
logging.level.io.restassured=DEBUG
```

## Solución de Problemas

### Error: Connection refused

**Causa**: Los servicios no están corriendo o no están listos.

**Solución**:
```cmd
# Verificar que los servicios estén corriendo
docker-compose ps

# Ver logs de un servicio específico
docker-compose logs msvc-security

# Reiniciar servicios
docker-compose restart
```

### Error: Timeout waiting for services

**Causa**: Los servicios tardan en iniciar.

**Solución**: Aumentar el timeout en `IntegrationTestConfig.java`:
```java
private int asyncTimeoutSeconds = 60; // Aumentar a 60 segundos
```

### Error: 401 Unauthorized

**Causa**: Token JWT expirado o inválido.

**Solución**: Las pruebas generan tokens frescos automáticamente. Si persiste:
1. Verificar que msvc-security esté operativo
2. Revisar configuración JWT en el servicio

### Tests fallan intermitentemente

**Causa**: Condiciones de carrera o servicios sobrecargados.

**Solución**:
1. Aumentar delays entre peticiones
2. Verificar recursos del sistema (CPU, memoria)
3. Ejecutar pruebas de forma secuencial con `-Dsurefire.forkCount=1`

## Integración con CI/CD

### Jenkins

```groovy
stage('Integration Tests') {
    steps {
        dir('integration-tests') {
            sh 'mvn clean test'
        }
    }
    post {
        always {
            junit 'integration-tests/target/surefire-reports/*.xml'
            publishHTML([
                reportDir: 'integration-tests/target',
                reportFiles: 'cucumber-reports.html',
                reportName: 'Cucumber Report'
            ])
        }
    }
}
```

### GitHub Actions

```yaml
- name: Run Integration Tests
  run: |
    cd integration-tests
    mvn clean test
  
- name: Publish Test Results
  uses: dorny/test-reporter@v1
  if: always()
  with:
    name: Integration Test Results
    path: integration-tests/target/surefire-reports/*.xml
    reporter: java-junit
```

## Mejores Prácticas

1. **Ejecutar pruebas después de cada cambio significativo**
2. **Mantener los servicios actualizados** antes de ejecutar pruebas
3. **Revisar logs** si las pruebas fallan
4. **Ejecutar en entorno limpio** para resultados consistentes
5. **Documentar nuevos casos de prueba** al añadir funcionalidad

## Extensión de las Pruebas

### Añadir nueva prueba JUnit

1. Crear método en `SystemIntegrationTest.java`:
```java
@Test
@Order(11)
@DisplayName("Test 11: Nueva funcionalidad")
public void test11_nuevaFuncionalidad() {
    // Tu código aquí
}
```

### Añadir nuevo escenario Cucumber

1. Editar `system-integration.feature`:
```gherkin
Escenario: Nueva funcionalidad
  Dado que el sistema está disponible
  Cuando realizo acción X
  Entonces debo obtener resultado Y
```

2. Implementar steps en `SystemIntegrationSteps.java`:
```java
@Cuando("realizo acción X")
public void realizoAccionX() {
    // Implementación
}
```

## Contacto y Soporte

Para problemas o sugerencias sobre las pruebas de integración:
- Revisar logs en `target/surefire-reports/`
- Consultar documentación de servicios individuales
- Verificar configuración en `application.properties`

## Versiones

- **Java**: 21
- **Spring Boot**: 3.2.0
- **REST Assured**: 5.4.0
- **Cucumber**: 7.15.0
- **JUnit**: 5.x
- **Awaitility**: 4.2.0

---

**Última actualización**: Octubre 2025
integration.test.consumer-base-url=http://localhost:8081
integration.test.security-base-url=http://localhost:8080
integration.test.saludo-base-url=http://localhost:80
integration.test.orchestrator-base-url=http://localhost:8083
integration.test.delivery-base-url=http://localhost:8082
integration.test.monitoring-base-url=http://localhost:8000

integration.test.max-retries=3
integration.test.retry-delay-ms=1000
integration.test.async-timeout-seconds=30

logging.level.com.taller.integration=INFO
logging.level.io.restassured=DEBUG

