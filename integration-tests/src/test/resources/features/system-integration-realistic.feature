Feature: Sistema de Microservicios - Pruebas de Integración Realistas
  Como desarrollador
  Quiero verificar que el sistema de microservicios funcione correctamente
  Para garantizar la calidad del sistema en un entorno con seguridad habilitada

  Escenario: Verificación de disponibilidad de servicios
    Dado que todos los microservicios están desplegados
    Cuando verifico el estado de salud de cada servicio
    Entonces el servicio "msvc-security" debe estar disponible
    Y el servicio "msvc-saludo" debe estar disponible
    Y el servicio "msvc-consumer" debe estar disponible
    Y el servicio "msvc-orchestrator" debe estar disponible

  Escenario: Verificación de respuesta de servicios protegidos
    Dado que el servicio de seguridad está disponible
    Cuando registro un nuevo usuario con nombre "testuser"
    Entonces el usuario debe ser creado exitosamente
    Y cuando inicio sesión con las credenciales correctas
    Entonces debo recibir un token JWT válido

  Escenario: Flujo de consumer (el que realmente funciona)
    Dado que todos los servicios están operativos
    Cuando invoco el endpoint de consumer con nombre "ConsumerTest"
    Entonces el consumer debe obtener un token automáticamente
    Y debe recibir un saludo del servicio de saludos
    Y la respuesta debe contener el nombre proporcionado

  Escenario: Verificación de manejo de autenticación
    Dado que el servicio de seguridad está disponible
    Cuando intento iniciar sesión con credenciales inválidas
    Entonces debo recibir un error de autenticación
    Y el código de estado debe ser 401 o 400

  Escenario: Estabilidad básica del sistema
    Dado que el sistema está operativo
    Cuando realizo múltiples peticiones concurrentes
    Entonces todas las peticiones deben completarse exitosamente
    Y el sistema debe mantener su estabilidad
