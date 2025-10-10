Feature: Envío de notificaciones

  Scenario: Enviar una notificación con destino válido
    Given el servicio de notificaciones está disponible
    When envío una solicitud POST a /notifications/send con:
      | channel     | "email"                |
      | destination | "destino@example.com"  |
      | templateType | "welcome"             |
      | data        | "{...}"                |
    Then recibo una respuesta 200 y la notificación es enviada correctamente

  Scenario: No enviar notificación si el destino está vacío
    Given el servicio de notificaciones está disponible
    When envío una solicitud POST a /notifications/send con:
      | channel     | "sms"                |
      | destination | ""                   |
      | templateType | "alert"             |
      | data        | "{...}"              |
    Then recibo una respuesta 400 y la notificación **no** es enviada