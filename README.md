# microservicios-taller

## 📋 Sistema de Microservicios con Pruebas de Integración Completas

Este proyecto implementa un sistema completo de microservicios con arquitectura distribuida, comunicación asíncrona y un conjunto robusto de pruebas automatizadas de integración.

## 🏗️ Arquitectura del Sistema

El sistema está compuesto por los siguientes microservicios:

- **msvc-security** (Puerto 8080): Gestión de usuarios y autenticación JWT
- **msvc-saludo** (Puerto 80): Servicio de saludos personalizados
- **msvc-consumer** (Puerto 8081): Orquestador que integra security y saludo
- **msvc-orchestrator** (Puerto 8083): Gestión de notificaciones multi-canal
- **msvc-delivery** (Puerto 8082): Envío de notificaciones (email/SMS)
- **msvc-monitoring** (Puerto 8000): Monitoreo del estado de servicios

### Infraestructura
- **RabbitMQ**: Mensajería asíncrona entre servicios
- **MongoDB**: Persistencia de datos (2 instancias)
- **Grafana + Loki + Promtail**: Observabilidad y logs
- **Jenkins + SonarQube**: CI/CD y calidad de código

## 🧪 Pruebas de Integración

### ✅ Cobertura de Pruebas

El proyecto incluye un módulo completo de **pruebas de integración automatizadas** que verifican:

- ✓ Disponibilidad de todos los servicios
- ✓ Flujos end-to-end completos
- ✓ Autenticación y autorización JWT
- ✓ Comunicación entre microservicios
- ✓ Resiliencia y manejo de errores
- ✓ Performance bajo carga concurrente
- ✓ Integración con RabbitMQ y MongoDB

### 📊 Estadísticas de Pruebas

- **10+ casos de prueba JUnit** completos
- **7 escenarios BDD** con Cucumber (en español)
- **3 smoke tests** para verificación rápida
- **6 servicios** verificados
- **20+ endpoints** HTTP probados

### 🚀 Ejecutar Pruebas de Integración

**Inicio rápido:**

```cmd
# 1. Levantar servicios
docker-compose up -d

# 2. Ejecutar pruebas
run-integration-tests.cmd
```

**Opciones avanzadas:**

```cmd
# Verificación rápida (smoke test)
cd integration-tests
verify-services.cmd

# Solo pruebas JUnit
cd integration-tests
run-junit-tests.cmd

# Solo pruebas Cucumber BDD
cd integration-tests
run-cucumber-tests.cmd

# Con Maven
cd integration-tests
mvn clean test
```

### 📚 Documentación de Pruebas

Para información detallada sobre las pruebas de integración:
- **[PRUEBAS_INTEGRACION.md](PRUEBAS_INTEGRACION.md)** - Resumen ejecutivo
- **[integration-tests/README.md](integration-tests/README.md)** - Documentación completa

## 🚀 Inicio Rápido

### Requisitos Previos

- Java 21
- Maven 3.8+
- Docker y Docker Compose
- Git

### Instalación y Ejecución

```cmd
# Clonar el repositorio
git clone <repository-url>
cd microservicios-taller

# Levantar todos los servicios
docker-compose up -d

# Esperar ~2 minutos para que los servicios inicien

# Verificar estado
docker-compose ps

# Ejecutar pruebas de integración
run-integration-tests.cmd
```

## 📁 Estructura del Proyecto

```
microservicios-taller/
├── msvc-security/              # Microservicio de seguridad
├── msvc-saludo/                # Microservicio de saludos
├── msvc-consumer/              # Microservicio orquestador
├── msvc-orchestrator/          # Microservicio de notificaciones
├── msvc-delivery/              # Microservicio de envío
├── msvc-monitoring/            # Microservicio de monitoreo (Python)
├── integration-tests/          # ⭐ Pruebas de integración completas
│   ├── README.md              # Documentación detallada
│   ├── pom.xml                # Configuración Maven
│   ├── run-tests.cmd          # Script de ejecución
│   └── src/
│       ├── main/java/         # Clientes REST
│       └── test/java/         # Casos de prueba
├── docker-compose.yml         # Orquestación de servicios
├── PRUEBAS_INTEGRACION.md    # Documentación de pruebas
└── README.md                  # Este archivo
```

## 🔧 Tecnologías Utilizadas

### Backend
- **Java 21** - Lenguaje principal
- **Spring Boot 3.2** - Framework de microservicios
- **MongoDB** - Base de datos NoSQL
- **RabbitMQ** - Mensajería asíncrona
- **JWT** - Autenticación

### Testing
- **JUnit 5** - Framework de pruebas
- **REST Assured** - Cliente HTTP para pruebas
- **Cucumber** - BDD (Behavior Driven Development)
- **Awaitility** - Pruebas asíncronas

### DevOps
- **Docker** - Contenedorización
- **Jenkins** - CI/CD
- **SonarQube** - Calidad de código
- **Grafana/Loki** - Observabilidad

## 📈 Reportes y Monitoreo

### Interfaces Web Disponibles

- **RabbitMQ Management**: http://localhost:15672 (admin/admin)
- **Grafana**: http://localhost:3000
- **Jenkins**: http://localhost:9090
- **SonarQube**: http://localhost:9000

### Reportes de Pruebas

Después de ejecutar las pruebas, encontrarás:
- **Maven Surefire**: `integration-tests/target/surefire-reports/`
- **Cucumber HTML**: `integration-tests/target/cucumber-reports.html`

## 🛠️ Comandos Útiles

```cmd
# Ver logs de un servicio
docker-compose logs -f msvc-security

# Reiniciar un servicio
docker-compose restart msvc-security

# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Ver estado de servicios
docker-compose ps

# Ejecutar solo smoke tests
cd integration-tests && mvn test -Dtest=SmokeTest
```

## 🤝 Contribuir

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request
6. Asegúrate de que las pruebas de integración pasen

## 📞 Soporte

Si encuentras problemas:
1. Revisa la documentación en `PRUEBAS_INTEGRACION.md`
2. Ejecuta el smoke test: `cd integration-tests && verify-services.cmd`
3. Revisa los logs: `docker-compose logs <service-name>`
4. Consulta los reportes de pruebas

## 📄 Licencia

Este proyecto es parte de un taller académico de microservicios.

## ✨ Características Destacadas

- ✅ **Arquitectura de Microservicios**: Diseño modular y escalable
- ✅ **Comunicación Asíncrona**: RabbitMQ para mensajería
- ✅ **Seguridad JWT**: Autenticación y autorización robusta
- ✅ **Pruebas Automatizadas**: Cobertura completa de integración
- ✅ **CI/CD Ready**: Integración con Jenkins y GitHub Actions
- ✅ **Observabilidad**: Grafana, Loki y Promtail
- ✅ **Calidad de Código**: SonarQube integrado
- ✅ **Documentación Completa**: README y guías detalladas

---

**Estado**: ✅ Sistema operativo con pruebas de integración completas  
**Versión**: 1.0.0  
**Última actualización**: Octubre 2025


