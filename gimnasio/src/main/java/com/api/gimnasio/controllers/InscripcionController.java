package com.api.gimnasio.controllers;

import com.api.gimnasio.dtos.InscripcionResponse;
import com.api.gimnasio.services.InscripcionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Solo consultas. registrarInscripcion no se expone: registrar una inscripción sin pasar por
 * InscribirClase se saltaría las validaciones de membresía y cupo.
 */
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping("/{folio}")
    public InscripcionResponse consultarInscripcion(@PathVariable Long folio) {
        return inscripcionService.consultarInscripcion(folio);
    }

    @GetMapping("/socio/{numeroSocio}")
    public List<InscripcionResponse> consultarInscripcionesDeSocio(@PathVariable String numeroSocio) {
        return inscripcionService.consultarInscripcionesDeSocio(numeroSocio);
    }
}
