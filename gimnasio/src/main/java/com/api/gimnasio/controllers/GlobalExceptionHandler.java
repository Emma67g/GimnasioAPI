package com.api.gimnasio.controllers;

import com.api.gimnasio.dtos.ErrorResponse;
import com.api.gimnasio.models.CodigoError;
import com.api.gimnasio.services.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> manejarApiException(ApiException ex) {
        return ResponseEntity
                .status(ex.getCodigo().getStatus())
                .body(new ErrorResponse(ex.getCodigo().name(), ex.getMessage()));
    }

    /** JSON mal formado, enum desconocido o parámetro/ruta incorrecto. */
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> manejarDatosInvalidos(Exception ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(CodigoError.DATOS_INVALIDOS.name(),
                        "La petición tiene datos faltantes o con formato inválido."));
    }
}
