# language: es
Característica: Matrícula del alumno
  Como alumno de CIBERTEC
  Quiero cotizar, matricularme y pagar mis cursos
  Para asegurar mi matrícula en el periodo académico

  Antecedentes:
    Dado que el alumno "i202610002" inicia sesión con la contraseña "Cibertec2026"

  Escenario: Cotizar el curso de tu fila
    Cuando cotiza el curso "BD5410"
    Entonces el codigo de respuesta es 200
    Y el monto total cotizado es 432.0

  Escenario: Matricularse y pagar el curso de tu fila
    Cuando se matricula en el curso "BD5410"
    Entonces el codigo de respuesta es 201
    Y el estado de la matrícula es "REGISTRADA"
    Y el monto de la matrícula es 432.0
    Y la respuesta cumple el esquema "matricula.json"
    Cuando paga la matrícula con el método "YAPE"
    Entonces el codigo de respuesta es 200
    Y el estado de la matrícula es "PAGADA"
    Y la fecha de pago está presente
    Y en la lista de pagos existe el pago de la matrícula con método "YAPE" y formato de operación válido

  Escenario: Regla de negocio que debe fallar
    Cuando intenta consultar la constancia del alumno "i202610003"
    Entonces el codigo de respuesta es 403
    Y el codigo de error es "ACCESO_DENEGADO"
    Y la respuesta cumple el esquema "error.json"
