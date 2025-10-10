Feature: Gestión de usuarios

  Scenario: Registrar un nuevo usuario exitosamente
    Given el servicio de usuarios está disponible
    When envío una solicitud POST a /users/register con los datos:
      | username   | "juanperez"        |
      | email      | "juan@example.com" |
      | password   | "supersegura1"     |
      | firstName  | "Juan"             |
      | lastName   | "Pérez"            |
      | mobileNumber | "3140000000"     |
    Then recibo una respuesta 201 y el usuario es creado con el username "juanperez"

  Scenario: Login exitoso con credenciales válidas
    Given existe un usuario con username "juanperez" y password "supersegura1"
    When envío una solicitud POST a /users/login con:
      | username | "juanperez"    |
      | password | "supersegura1" |
    Then recibo un token JWT válido en la respuesta y código 200

  Scenario: Recuperación de contraseña para usuario existente
    Given existe un usuario con email "juan@example.com"
    When envío una solicitud POST a /users/password-recovery con:
      | email | "juan@example.com" |
    Then recibo una respuesta 200 y se envía el evento de recuperación de contraseña

  Scenario: Actualizar información de usuario existente
    Given existe un usuario con id "123" y username "juanperez"
    When envío una solicitud PUT a /users/123 con:
      | firstName | "Juanito" |
    Then recibo una respuesta 200 y el nombre del usuario se actualiza a "Juanito"