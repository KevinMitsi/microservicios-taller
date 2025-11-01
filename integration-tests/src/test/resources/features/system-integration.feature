# language: es
Característica: Sistema de Microservicios - Flujo completo de integración
  Como sistema integrado de microservicios
  Quiero verificar que todos los componentes funcionen correctamente juntos
  Para garantizar la calidad del sistema completo

  Escenario: Verificación de disponibilidad de todos los servicios
    Dado que todos los microservicios están desplegados
    Cuando verifico el estado de salud de cada servicio
    Entonces el servicio "msvc-security" debe estar disponible
    Y el servicio "msvc-saludo" debe estar disponible
    Y el servicio "msvc-consumer" debe estar disponible
    Y el servicio "msvc-orchestrator" debe estar disponible

  Escenario: Registro y autenticación de usuario
    Dado que el servicio de seguridad está disponible
    Cuando registro un nuevo usuario con nombre "integrationuser"
    Entonces el usuario debe ser creado exitosamente
    Y cuando inicio sesión con las credenciales correctas
    Entonces debo recibir un token JWT válido

  Escenario: Flujo de saludo autenticado
    Dado que tengo un token de autenticación válido
    Cuando solicito un saludo personalizado con el nombre "TesterIntegración"
    Entonces debo recibir un saludo que contenga mi nombre

  Escenario: Flujo completo de consumer
    Dado que todos los servicios están operativos
    Cuando invoco el endpoint de consumer con nombre "ConsumerTest"
    Entonces el consumer debe obtener un token automáticamente
    Y debe recibir un saludo del servicio de saludos
    Y la respuesta debe contener el nombre proporcionado

  Escenario: Manejo de errores de autenticación
    Dado que el servicio de seguridad está disponible
    Cuando intento iniciar sesión con credenciales inválidas
    Entonces debo recibir un error de autenticación
    Y el código de estado debe ser 401 o 400

  Escenario: Resiliencia del sistema
    Dado que el sistema está operativo
    Cuando realizo múltiples peticiones concurrentes
    Entonces todas las peticiones deben completarse exitosamente
    Y el sistema debe mantener su estabilidad

  Escenario: Consulta de canales de notificación
    Dado que tengo un token de autenticación válido
    Y el servicio orchestrator está disponible
    Cuando consulto los canales de notificación disponibles
    Entonces debo recibir una lista de canales

