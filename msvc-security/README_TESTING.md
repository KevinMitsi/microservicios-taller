# Pruebas Automatizadas con Cucumber y Rest Assured

## Descripción

Este proyecto incluye pruebas automatizadas para el microservicio de seguridad utilizando:
- **Cucumber**: Framework BDD (Behavior Driven Development) para escribir pruebas en lenguaje natural
- **Rest Assured**: Librería para testing de APIs REST
- **JSON Schema Validator**: Validación de la estructura de las respuestas JSON

## Estructura del Proyecto de Pruebas

```
msvc-security/
├── src/
│   ├── test/
│   │   ├── java/com/taller/msvc_security/
│   │   │   ├── runners/
│   │   │   │   └── TestRunner.java          # Ejecutor principal de Cucumber
│   │   │   └── stepdefinitions/
│   │   │       ├── UserSteps.java           # Step definitions para usuarios
│   │   │       └── NotificationSteps.java   # Step definitions para notificaciones
│   │   └── resources/
│   │       ├── features/
│   │       │   ├── users.feature            # Casos de prueba de usuarios
│   │       │   └── notification.feature     # Casos de prueba de notificaciones
│   │       └── schema/
│   │           ├── user_schema.json         # Schema JSON para validar usuarios
│   │           ├── auth_response_schema.json # Schema JSON para validar autenticación
│   │           └── error_response_schema.json # Schema JSON para validar errores
```

## Casos de Prueba Implementados

### Gestión de Usuarios (users.feature)

1. **Registrar un nuevo usuario exitosamente**
   - Verifica que se puede crear un usuario nuevo
   - Valida la estructura de la respuesta con JSON Schema
   - Comprueba que el código de respuesta sea 201

2. **Login exitoso con credenciales válidas**
   - Verifica que un usuario puede autenticarse
   - Valida que se recibe un token JWT válido
   - Valida la estructura del token con JSON Schema

3. **Recuperación de contraseña para usuario existente**
   - Verifica el envío de solicitud de recuperación
   - Comprueba que se envía el evento correspondiente

4. **Actualizar información de usuario existente**
   - Verifica que un usuario puede actualizar su información
   - Valida la estructura de la respuesta con JSON Schema
   - Requiere autenticación JWT

### Notificaciones (notification.feature)

1. **Enviar una notificación con destino válido**
   - Verifica el envío correcto de notificaciones

2. **No enviar notificación si el destino está vacío**
   - Verifica validación de campos obligatorios
   - Comprueba código de error 400

## Validación con JSON Schema

Las pruebas incluyen validación de la estructura de las respuestas utilizando JSON Schema:

### user_schema.json
Valida que las respuestas de usuario contengan:
- `id` (obligatorio)
- `username` (obligatorio)
- `email` (obligatorio)
- `firstName` (obligatorio)
- `lastName` (obligatorio)
- `mobileNumber` (obligatorio)
- `authorities` (opcional, array)

### auth_response_schema.json
Valida que las respuestas de autenticación contengan:
- `token` (obligatorio, formato JWT)
- `expiresIn` (opcional)

## Cómo Ejecutar las Pruebas

### Prerequisitos

1. Asegúrate de que el microservicio esté corriendo en `http://localhost:8080`
2. Tener Java 21 o superior instalado
3. Maven instalado

### Ejecutar todas las pruebas

Desde el directorio `msvc-security`, ejecuta:

```cmd
mvn test
```

### Ejecutar solo las pruebas de Cucumber

```cmd
mvn test -Dtest=TestRunner
```

### Ver el reporte HTML

Después de ejecutar las pruebas, se genera un reporte HTML en:
```
target/cucumber-report.html
```

También se genera un archivo JSON con los resultados en:
```
target/cucumber.json
```

## Configuración de Rest Assured

Las pruebas están configuradas para:
- **Base URI**: http://localhost
- **Puerto**: 8080
- **Base Path**: /api

Esto se configura en cada step definition con:
```java
RestAssured.baseURI = "http://localhost";
RestAssured.port = 8080;
RestAssured.basePath = "/api";
```

## Ejemplos de Validación

### Validación de Status Code
```java
assertEquals(statusCode.intValue(), response.getStatusCode());
```

### Validación con JSON Schema
```java
response.then().assertThat()
    .body(matchesJsonSchemaInClasspath("schema/user_schema.json"));
```

### Validación de Campos Específicos
```java
String actualUsername = response.jsonPath().getString("username");
assertEquals(expectedUsername, actualUsername);
```

### Validación de Token JWT
```java
String[] jwtParts = authToken.split("\\.");
assertEquals("El JWT debe tener 3 partes separadas por punto", 3, jwtParts.length);
```

## Características Implementadas

✅ **Cucumber BDD**: Casos de prueba escritos en lenguaje natural (Gherkin)
✅ **Rest Assured**: Testing completo de endpoints REST
✅ **JSON Schema Validation**: Validación automática de estructura de respuestas
✅ **Autenticación JWT**: Manejo de tokens de autenticación en las pruebas
✅ **Reportes**: Generación automática de reportes HTML y JSON
✅ **Validaciones Múltiples**: Status codes, estructura JSON, campos específicos

## Dependencias en pom.xml

```xml
<!-- Cucumber -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>

<!-- Rest Assured -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>

<!-- JSON Schema Validator -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>
```

## Notas Adicionales

- Las pruebas crean datos de prueba dinámicamente para evitar conflictos
- Los IDs de usuario se generan automáticamente y se almacenan para uso posterior
- Los tokens JWT se obtienen automáticamente y se usan en endpoints protegidos
- Los esquemas JSON validan tanto campos obligatorios como opcionales

