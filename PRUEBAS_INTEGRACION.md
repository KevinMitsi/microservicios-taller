# Pruebas de Integración del Sistema de Microservicios

## 📋 Resumen

Este proyecto incluye un conjunto completo de **pruebas automatizadas de integración** que verifican el correcto funcionamiento del sistema de microservicios como un conjunto único.

## 🎯 Objetivo

Validar que todos los microservicios funcionan correctamente de forma integrada, probando:
- ✅ Comunicación entre servicios
- ✅ Flujos end-to-end completos
- ✅ Autenticación y autorización
- ✅ Manejo de errores y resiliencia
- ✅ Performance bajo carga
- ✅ Disponibilidad de servicios

## 📁 Ubicación

```
microservicios-taller/
└── integration-tests/          ← Módulo de pruebas de integración
    ├── README.md               ← Documentación completa
    ├── pom.xml                 ← Configuración Maven
    ├── run-tests.cmd           ← Script principal de ejecución
    ├── verify-services.cmd     ← Verificación rápida
    └── src/
        ├── main/java/          ← Clientes y configuración
        └── test/java/          ← Casos de prueba
```

## 🚀 Inicio Rápido

### 1. Levantar los servicios

```cmd
docker-compose up -d
```

Espera 1-2 minutos hasta que todos los servicios estén listos.

### 2. Ejecutar las pruebas

**Opción A: Script automatizado (Recomendado)**
```cmd
cd integration-tests
run-tests.cmd
```

**Opción B: Maven directo**
```cmd
cd integration-tests
mvn clean test
```

**Opción C: Verificación rápida (Smoke Test)**
```cmd
cd integration-tests
verify-services.cmd
```

## 📊 Tipos de Pruebas Incluidas

### 1. Pruebas JUnit (SystemIntegrationTest.java)
- ✅ **10 casos de prueba completos** que verifican:
  - Health checks de todos los servicios
  - Registro y autenticación de usuarios
  - Servicio de saludos personalizados
  - Flujo completo de consumer
  - Gestión de usuarios
  - Orchestrator y notificaciones
  - Resiliencia y manejo de errores
  - Monitoreo del sistema
  - Performance con peticiones concurrentes
  - Flujo end-to-end completo

### 2. Pruebas BDD con Cucumber (system-integration.feature)
- ✅ **7 escenarios en español** escritos en lenguaje Gherkin:
  - Verificación de disponibilidad
  - Autenticación de usuarios
  - Flujos de negocio
  - Manejo de errores
  - Resiliencia
  - Consulta de recursos

### 3. Smoke Tests (SmokeTest.java)
- ✅ **Verificación rápida** de disponibilidad de servicios
- Útil para CI/CD pipelines

## 🧪 Servicios Verificados

| Servicio | Puerto | Descripción | Estado |
|----------|--------|-------------|--------|
| msvc-security | 8080 | Autenticación y usuarios | ✓ Crítico |
| msvc-saludo | 80 | Saludos personalizados | ✓ Crítico |
| msvc-consumer | 8081 | Orquestador principal | ✓ Crítico |
| msvc-orchestrator | 8083 | Gestión de notificaciones | ✓ Crítico |
| msvc-delivery | 8082 | Envío de notificaciones | ⚠ Opcional |
| msvc-monitoring | 8000 | Monitoreo del sistema | ⚠ Opcional |

## 📈 Reportes Generados

Después de ejecutar las pruebas, encontrarás:

1. **Reporte Surefire (Maven)**
   - `integration-tests/target/surefire-reports/`
   - Formato XML y TXT

2. **Reporte Cucumber HTML**
   - `integration-tests/target/cucumber-reports.html`
   - Abre en navegador web

3. **Logs detallados**
   - Consola con output colorido
   - Información de cada paso ejecutado

## 📝 Ejemplo de Salida

```
=== SMOKE TEST: Verificando disponibilidad de servicios ===

✓ msvc-security [DISPONIBLE] - Puerto 8080
✓ msvc-saludo [DISPONIBLE] - Puerto 80
✓ msvc-consumer [DISPONIBLE] - Puerto 8081
✓ msvc-orchestrator [DISPONIBLE] - Puerto 8083
⚠ msvc-monitoring [NO DISPONIBLE] - Servicio opcional

============================================================
RESULTADO: ✓ TODOS LOS SERVICIOS CRÍTICOS ESTÁN DISPONIBLES
============================================================
```

## 🔧 Configuración

Las URLs de los servicios se configuran en:
`integration-tests/src/test/resources/application.properties`

```properties
integration.test.security-base-url=http://localhost:8080
integration.test.saludo-base-url=http://localhost:80
integration.test.consumer-base-url=http://localhost:8081
# ... más configuraciones
```

## 🐛 Solución de Problemas

### Servicios no disponibles
```cmd
# Verificar estado
docker-compose ps

# Ver logs
docker-compose logs msvc-security

# Reiniciar servicios
docker-compose restart
```

### Pruebas fallan
```cmd
# Ejecutar solo verificación rápida
cd integration-tests
verify-services.cmd

# Ver logs detallados
mvn test -X
```

### Problemas de conexión
- Verificar que Docker esté corriendo
- Verificar que los puertos no estén ocupados
- Esperar más tiempo para que los servicios inicien

## 📚 Documentación Adicional

Para información detallada, consulta:
- **[integration-tests/README.md](integration-tests/README.md)** - Documentación completa
- Código fuente de las pruebas en `integration-tests/src/test/java/`
- Escenarios BDD en `integration-tests/src/test/resources/features/`

## 🔄 Integración CI/CD

Las pruebas están preparadas para integrarse con:
- ✅ Jenkins
- ✅ GitHub Actions
- ✅ GitLab CI
- ✅ Azure DevOps

Ejemplo para Jenkins:
```groovy
stage('Integration Tests') {
    steps {
        dir('integration-tests') {
            bat 'mvn clean test'
        }
    }
}
```

## 📊 Métricas y Cobertura

Las pruebas de integración verifican:
- **6 microservicios** diferentes
- **10+ flujos de negocio** completos
- **20+ endpoints** HTTP
- **Autenticación JWT** y seguridad
- **Comunicación entre servicios** (RabbitMQ)
- **Persistencia** (MongoDB)
- **Resiliencia** ante errores

## 🎓 Tecnologías Utilizadas

- **Java 21** - Lenguaje base
- **Spring Boot 3.2** - Framework
- **REST Assured 5.4** - Cliente HTTP para pruebas
- **JUnit 5** - Framework de pruebas
- **Cucumber 7.15** - BDD (Behavior Driven Development)
- **Awaitility 4.2** - Pruebas asíncronas
- **Maven 3.8+** - Gestión de dependencias

## ✅ Criterios de Éxito

Las pruebas se consideran exitosas cuando:
1. ✓ Todos los servicios críticos están disponibles
2. ✓ El flujo de autenticación funciona correctamente
3. ✓ La comunicación entre servicios es exitosa
4. ✓ Los endpoints responden en tiempo razonable
5. ✓ El sistema maneja errores apropiadamente
6. ✓ Las pruebas concurrentes no generan fallos

## 🤝 Contribuir

Para añadir nuevas pruebas:
1. Crear nuevo test en `SystemIntegrationTest.java`
2. O añadir escenario en `system-integration.feature`
3. Implementar los pasos necesarios
4. Ejecutar y verificar que pasa
5. Documentar el nuevo caso de prueba

## 📞 Soporte

Si encuentras problemas:
1. Revisa los logs en `target/surefire-reports/`
2. Consulta la documentación completa en `integration-tests/README.md`
3. Verifica que todos los servicios estén corriendo
4. Ejecuta el smoke test para diagnóstico rápido

---

**Estado del Proyecto**: ✅ Completado y funcional  
**Última actualización**: Octubre 2025  
**Versión**: 1.0.0

