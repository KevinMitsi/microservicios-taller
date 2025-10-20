# language: es
Característica: Pruebas con datos aleatorios usando JavaFaker

  Escenario: Crear múltiples usuarios con datos aleatorios
    Dado el servicio de usuarios está disponible
    Cuando creo 5 usuarios con datos aleatorios
    Entonces todos los usuarios son creados exitosamente
    Y cada usuario tiene datos únicos

  Escenario: Validar login con usuarios generados aleatoriamente
    Dado existe un usuario aleatorio en el sistema
    Cuando intento hacer login con las credenciales correctas
    Entonces el login es exitoso con código 200
    Y recibo un token JWT válido

