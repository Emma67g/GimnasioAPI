package com.api.gimnasio.dtos;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        String numeroSocio,
        Long idClase,
        String tipo,
        LocalDateTime enviarEn,
        String estado,
        String mensaje) {
}
