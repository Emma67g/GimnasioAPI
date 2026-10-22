package com.api.gimnasio.dtos;

import java.time.LocalDate;

/** Salida de consultarMembresia. "vigente" se calcula con la fecha de hoy. */
public record MembresiaResponse(
        String numeroSocio,
        String nombreSocio,
        LocalDate fechaVencimiento,
        boolean vigente) {
}
