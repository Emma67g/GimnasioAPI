package com.api.gimnasio.dtos;

/** Salida de consultarCupo y de actualizarCupo. */
public record CupoResponse(
        Long idClase,
        int capacidadMaxima,
        int lugaresOcupados,
        int cupoDisponible,
        boolean hayCupo) {
}
