Feature: Smoke Test de Servicios
  Como desarrollador
  Quiero verificar que todos los servicios estén disponibles
  Para asegurarme de que el sistema puede ser probado

  Escenario: Verificación básica de disponibilidad
    Dado que todos los microservicios están desplegados
    Cuando verifico el estado de salud de cada servicio
    Entonces el servicio "msvc-security" debe estar disponible
    Y el servicio "msvc-saludo" debe estar disponible
    Y el servicio "msvc-consumer" debe estar disponible
    Y el servicio "msvc-orchestrator" debe estar disponible
