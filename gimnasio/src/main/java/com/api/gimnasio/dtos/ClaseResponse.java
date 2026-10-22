package com.api.gimnasio.dtos;

import com.api.gimnasio.models.Disciplina;
import com.api.gimnasio.models.Sucursal;

import java.time.LocalDateTime;

/** Elemento de la lista que devuelve consultarClases. */
public record ClaseResponse(
        Long id,
        String nombre,
        Disciplina disciplina,
        Sucursal sucursal,
        LocalDateTime fechaHora,
        int cupoDisponible) {
}
