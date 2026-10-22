package com.api.gimnasio.dtos;

import java.time.LocalDateTime;

/** Salida de inscribirEnClase. */
public record InscripcionResponse(
        Long folioInscripcion,
        String estado,
        String nombreClase,
        String sucursal,
        LocalDateTime fechaHoraClase) {
}
