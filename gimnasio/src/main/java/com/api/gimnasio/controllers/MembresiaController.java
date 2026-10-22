package com.api.gimnasio.controllers;

import com.api.gimnasio.dtos.MembresiaResponse;
import com.api.gimnasio.services.MembresiaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/membresias")
public class MembresiaController {

    private final MembresiaService membresiaService;

    public MembresiaController(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
    }

    /** consultarMembresia: GET /api/membresias/{numeroSocio} */
    @GetMapping("/{numeroSocio}")
    public MembresiaResponse consultarMembresia(@PathVariable String numeroSocio) {
        return membresiaService.consultarMembresia(numeroSocio);
    }
}
