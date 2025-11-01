# Flujo de Pruebas de Integración

## 📊 Diagrama de Flujo de Pruebas

```
┌─────────────────────────────────────────────────────────────────┐
│                    INICIO DE PRUEBAS                             │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 1: Verificación de Prerequisitos                          │
│  • Java 21 instalado                                             │
│  • Maven 3.8+ disponible                                         │
│  • Docker Compose corriendo                                      │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 2: Health Check de Servicios (Smoke Test)                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ msvc-security│  │ msvc-saludo  │  │ msvc-consumer│          │
│  │   :8080      │  │    :80       │  │   :8081      │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────┐  ┌──────────────┐                            │
│  │msvc-orch     │  │ RabbitMQ     │                            │
│  │  :8083       │  │   :5672      │                            │
│  └──────────────┘  └──────────────┘                            │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 3: Pruebas de Autenticación (Test 2)                      │
│                                                                  │
│  1. Registrar Usuario                                           │
│     POST /api/users                                             │
│     ├─> msvc-security                                           │
│     └─> MongoDB                                                 │
│                                                                  │
│  2. Login                                                       │
│     POST /api/auth/login                                        │
│     ├─> msvc-security                                           │
│     └─> Generar JWT Token                                       │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 4: Pruebas de Servicios Individuales (Test 3)             │
│                                                                  │
│  GET /saludo?nombre=Test                                        │
│  ├─> msvc-saludo                                                │
│  └─> Respuesta: "Hola Test, bienvenido..."                     │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 5: Pruebas de Integración (Test 4)                        │
│                                                                  │
│  GET /consumeApps/{nombre}                                      │
│  ├─> msvc-consumer                                              │
│  │    ├─> Generar credenciales aleatorias                       │
│  │    ├─> POST /api/auth/login → msvc-security                 │
│  │    │    └─> Obtener token JWT                                │
│  │    └─> GET /saludo?nombre={nombre} → msvc-saludo            │
│  │         └─> Con Authorization: Bearer {token}                │
│  └─> Respuesta final con saludo                                │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 6: Pruebas de Gestión de Usuarios (Test 5)                │
│                                                                  │
│  1. Listar Usuarios (GET /api/users)                            │
│     └─> Requiere autenticación JWT                             │
│                                                                  │
│  2. Obtener Usuario por ID (GET /api/users/{id})                │
│     └─> Validar datos del usuario                              │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 7: Pruebas de Orchestrator (Test 6)                       │
│                                                                  │
│  1. GET /api/notifications/channels                             │
│     └─> Obtener canales disponibles (email, sms, etc.)         │
│                                                                  │
│  2. GET /api/notifications/templates                            │
│     └─> Obtener templates de notificación                      │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 8: Pruebas de Resiliencia (Test 7)                        │
│                                                                  │
│  1. Credenciales Inválidas                                      │
│     POST /api/auth/login (usuario/pass incorrectos)             │
│     └─> Esperar código 401/400                                 │
│                                                                  │
│  2. Token Inválido                                              │
│     GET /api/users con token inválido                           │
│     └─> Esperar código 401/403                                 │
│                                                                  │
│  3. Verificar Sistema Sigue Operativo                           │
│     └─> Health checks después de errores                       │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 9: Pruebas de Performance (Test 9)                        │
│                                                                  │
│  Ejecutar 5 peticiones concurrentes                             │
│  ├─> Petición 1: GET /saludo?nombre=Test1                      │
│  ├─> Petición 2: GET /saludo?nombre=Test2                      │
│  ├─> Petición 3: GET /saludo?nombre=Test3                      │
│  ├─> Petición 4: GET /saludo?nombre=Test4                      │
│  └─> Petición 5: GET /saludo?nombre=Test5                      │
│                                                                  │
│  Medir tiempo total y promedio                                  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  PASO 10: Flujo End-to-End Completo (Test 10)                   │
│                                                                  │
│  1. Registrar nuevo usuario E2E                                 │
│  2. Autenticar usuario E2E                                      │
│  3. Obtener saludo con token                                    │
│  4. Usar consumer service                                       │
│  5. Verificar todos los servicios siguen UP                     │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│  GENERACIÓN DE REPORTES                                          │
│                                                                  │
│  • Maven Surefire Reports (XML/TXT)                             │
│    └─> target/surefire-reports/                                │
│                                                                  │
│  • Cucumber HTML Report                                         │
│    └─> target/cucumber-reports.html                            │
│                                                                  │
│  • Logs de ejecución en consola                                 │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  ✅ PRUEBAS COMPLETADAS                          │
│                                                                  │
│  Resultados:                                                     │
│  • Tests ejecutados: 10+                                        │
│  • Escenarios BDD: 7                                            │
│  • Servicios verificados: 6                                     │
│  • Endpoints probados: 20+                                      │
└─────────────────────────────────────────────────────────────────┘
```

## 🔄 Flujo de Datos entre Servicios

```
┌──────────────┐
│   Cliente    │
│  (Pruebas)   │
└──────┬───────┘
       │
       │ 1. GET /consumeApps/{nombre}
       ▼
┌──────────────────────┐
│   msvc-consumer      │
│     (Puerto 8081)    │
└──────┬───────────────┘
       │
       │ 2. POST /api/auth/login (credenciales aleatorias)
       ▼
┌──────────────────────┐         ┌──────────────┐
│   msvc-security      │◄────────┤   MongoDB    │
│     (Puerto 8080)    │ 3. Query│   :27017     │
└──────┬───────────────┘         └──────────────┘
       │
       │ 4. JWT Token
       ▼
┌──────────────────────┐
│   msvc-consumer      │
│  (Guarda el token)   │
└──────┬───────────────┘
       │
       │ 5. GET /saludo?nombre={nombre}
       │    Authorization: Bearer {token}
       ▼
┌──────────────────────┐
│   msvc-saludo        │
│     (Puerto 80)      │
└──────┬───────────────┘
       │
       │ 6. "Hola {nombre}, bienvenido..."
       ▼
┌──────────────────────┐
│   msvc-consumer      │
│  (Retorna respuesta) │
└──────┬───────────────┘
       │
       │ 7. Respuesta final
       ▼
┌──────────────┐
│   Cliente    │
│  (Valida)    │
└──────────────┘
```

## 🎯 Cobertura de Pruebas por Servicio

```
msvc-security (8080)
├─ ✓ Health check
├─ ✓ Registro de usuarios
├─ ✓ Autenticación JWT
├─ ✓ Listar usuarios (paginado)
├─ ✓ Obtener usuario por ID
├─ ✓ Manejo de errores (401/400)
└─ ✓ Validación de tokens

msvc-saludo (80)
├─ ✓ Health check
├─ ✓ Saludo personalizado
└─ ✓ Autenticación con Bearer token

msvc-consumer (8081)
├─ ✓ Health check
├─ ✓ Flujo completo de integración
├─ ✓ Generación de credenciales
└─ ✓ Orquestación de servicios

msvc-orchestrator (8083)
├─ ✓ Health check
├─ ✓ Listar canales
├─ ✓ Listar templates
├─ ✓ Crear notificación
└─ ✓ Buscar notificaciones

msvc-delivery (8082)
├─ ✓ Consumo de cola RabbitMQ
└─ ✓ Envío de notificaciones

msvc-monitoring (8000)
└─ ⚠ Verificación opcional
```

## 📋 Checklist de Validación

Cada ejecución de pruebas valida:

- [ ] Todos los servicios están UP (health checks)
- [ ] Se puede registrar un nuevo usuario
- [ ] Se puede autenticar y obtener token JWT
- [ ] El token es válido y funcional
- [ ] Se puede listar usuarios con autenticación
- [ ] Se puede obtener saludo personalizado
- [ ] El consumer integra correctamente los servicios
- [ ] El sistema rechaza credenciales inválidas
- [ ] El sistema protege endpoints con autenticación
- [ ] El sistema maneja peticiones concurrentes
- [ ] Los servicios siguen operativos después de errores
- [ ] El flujo end-to-end completo funciona

## 🚀 Ejecución en Pipeline CI/CD

```
┌────────────────┐
│  Git Push      │
└────────┬───────┘
         │
         ▼
┌────────────────────┐
│  Jenkins Trigger   │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  Build Stage       │
│  (Maven compile)   │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  Docker Compose    │
│  (Start Services)  │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  Smoke Tests       │
│  (Quick check)     │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  Integration Tests │
│  (Full suite)      │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  Generate Reports  │
│  • JUnit XML       │
│  • Cucumber HTML   │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  Publish Results   │
│  (Jenkins/GitHub)  │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  Cleanup           │
│  (docker-compose   │
│   down)            │
└────────────────────┘
```

---

**Última actualización**: Octubre 2025

