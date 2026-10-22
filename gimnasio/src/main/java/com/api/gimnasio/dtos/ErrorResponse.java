package com.api.gimnasio.dtos;

/** Cuerpo JSON con el que se informan todos los errores. */
public record ErrorResponse(String codigo, String mensaje) {
}
