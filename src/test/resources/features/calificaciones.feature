# language: es
Característica: Calificación de matrículas

  Esquema del escenario: el docente registra la nota y el historial se actualiza
    Dado que la alumna "i202610005" obtiene su matrícula del curso "PI5620"
    Y el docente "D0001" inicia sesión con la clave "Cibertec2026"
    Cuando el docente registra la nota <nota> en la matrícula
    Entonces la nota es registrada con código 200
    Cuando la alumna consulta su historial académico
    Entonces la condición del curso "PI5620" es "<condicion>"
    Y los créditos aprobados acumulados son <creditosAprobados>
    Y el promedio ponderado calculado es <promedioPonderado>
    Y la respuesta cumple con el esquema de historial

    Ejemplos:
      | nota | condicion   | creditosAprobados | promedioPonderado |
      | 18   | APROBADO    | 13                | 16.62             |
      | 12,5 | DESAPROBADO | 7                 | 14.08             |

  Escenario: Un docente que no dicta el curso intenta registrar la nota
    Dado que la alumna "i202610005" obtiene su matrícula del curso "PI5620"
    Y el docente "D0002" inicia sesión con la clave "Cibertec2026"
    Cuando el docente intenta registrar la nota 15 en la matrícula
    Entonces la calificación es rechazada con código 403 y código de error "ACCESO_DENEGADO"
    Y la respuesta cumple con el esquema de error

  Escenario: No se puede calificar una matrícula que no está pagada (PS5511 de la misma alumna)
    Dado que la alumna "i202610005" obtiene su matrícula del curso "PS5511"
    Y el docente "D0001" inicia sesión con la clave "Cibertec2026"
    Cuando el docente intenta registrar la nota 15 en la matrícula
    Entonces la calificación es rechazada con código 409 y código de error "MATRICULA_NO_PAGADA"
    Y la respuesta cumple con el esquema de error
