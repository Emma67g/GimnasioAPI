package com.api.gimnasio.controllers;

import com.api.gimnasio.dtos.InscripcionRequest;
import com.api.gimnasio.dtos.InscripcionResponse;
import com.api.gimnasio.services.InscribirClaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscribir-clase")
public class InscribirClaseController {

    private final InscribirClaseService inscribirClaseService;

    public InscribirClaseController(InscribirClaseService inscribirClaseService) {
        this.inscribirClaseService = inscribirClaseService;
    }

    /** inscribirEnClase */
    @PostMapping
    public ResponseEntity<InscripcionResponse> inscribirEnClase(@RequestBody InscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inscribirClaseService.inscribirEnClase(request));
    }
}
