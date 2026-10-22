package com.api.gimnasio.models;

import org.springframework.http.HttpStatus;

/** Códigos de error de la especificación y el estado HTTP con el que se informan. */
public enum CodigoError {
    MEMBRESIA_NO_ENCONTRADA(HttpStatus.NOT_FOUND),
    MEMBRESIA_VENCIDA(HttpStatus.FORBIDDEN),
    CLASE_NO_ENCONTRADA(HttpStatus.NOT_FOUND),
    CLASE_YA_INICIADA(HttpStatus.CONFLICT),
    CLASE_SIN_CUPO(HttpStatus.CONFLICT),
    INSCRIPCION_DUPLICADA(HttpStatus.CONFLICT),
    INSCRIPCION_NO_ENCONTRADA(HttpStatus.NOT_FOUND),
    DATOS_INVALIDOS(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    CodigoError(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
