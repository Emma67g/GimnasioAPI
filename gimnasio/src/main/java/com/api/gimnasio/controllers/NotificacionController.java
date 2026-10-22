package com.api.gimnasio.controllers;

import com.api.gimnasio.dtos.NotificacionResponse;
import com.api.gimnasio.services.NotificacionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Solo consulta el estado de los mensajes; los mensajes los genera InscribirClase. */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping("/socio/{numeroSocio}")
    public List<NotificacionResponse> consultarNotificaciones(@PathVariable String numeroSocio) {
        return notificacionService.consultarNotificaciones(numeroSocio);
    }
}
