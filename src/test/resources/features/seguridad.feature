# language: es
Característica: Seguridad de la sesión

  Escenario: Rotación del refresh token
    Dado que el alumno "i202610002" inicia sesión con la contraseña "Cibertec2026" para verificar seguridad
    Cuando renueva sus tokens con el refresh token
    Entonces la renovación inicial responde 200 con tokens nuevos
    Cuando intenta usar otra vez el refresh token original
    Entonces el reuso es rechazado con 401 y código de error "REFRESH_REUTILIZADO"
    Y la respuesta cumple con el esquema de error
    Cuando intenta consultar su perfil con el nuevo access token
    Entonces la consulta de perfil es rechazada con 401 y código de error "TOKEN_INVALIDO"
    Y la respuesta cumple con el esquema de error

  Escenario: Cierre de sesión
    Dado que el alumno "i202610002" inicia sesión con la contraseña "Cibertec2026" para verificar seguridad
    Cuando cierra su sesión
    Entonces el logout responde con código 204
    Cuando intenta consultar su perfil con el mismo access token
    Entonces la consulta de perfil es rechazada con 401 y código de error "TOKEN_INVALIDO"
    Y la respuesta cumple con el esquema de error

  Escenario: Bloqueo de cuenta por intentos fallidos
    Cuando el alumno "i202610004" ingresa credenciales fallidas 5 veces:
      | intento | clave    | status | codigo                 |
      | 1       | Clave001 | 401    | CREDENCIALES_INVALIDAS |
      | 2       | Clave002 | 401    | CREDENCIALES_INVALIDAS |
      | 3       | Clave003 | 401    | CREDENCIALES_INVALIDAS |
      | 4       | Clave004 | 401    | CREDENCIALES_INVALIDAS |
      | 5       | Clave005 | 423    | CUENTA_BLOQUEADA       |
    Cuando intenta ingresar con la clave correcta "Cibertec2026"
    Entonces el login es rechazado con 423 y código de error "CUENTA_BLOQUEADA"
    Y la respuesta cumple con el esquema de error

  Escenario: Alumno sin permiso de administrador
    Dado que el alumno "i202610002" inicia sesión con la contraseña "Cibertec2026" para verificar seguridad
    Cuando intenta listar todos los alumnos con GET "/api/alumnos"
    Entonces la consulta es rechazada con 403 y código de error "ACCESO_DENEGADO"
    Y la respuesta cumple con el esquema de error
