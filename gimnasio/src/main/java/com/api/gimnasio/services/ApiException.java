package com.api.gimnasio.services;

import com.api.gimnasio.models.CodigoError;

public class ApiException extends RuntimeException {

    private final CodigoError codigo;

    public ApiException(CodigoError codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public CodigoError getCodigo() {
        return codigo;
    }
}
