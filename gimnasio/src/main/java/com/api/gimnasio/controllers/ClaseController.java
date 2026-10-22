package com.api.gimnasio.controllers;

import com.api.gimnasio.dtos.ClaseResponse;
import com.api.gimnasio.dtos.CupoResponse;
import com.api.gimnasio.dtos.CupoUpdateRequest;
import com.api.gimnasio.models.Disciplina;
import com.api.gimnasio.services.ClaseService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }

    /** consultarClases: GET /api/clases?sucursal=Centro&disciplina=YOGA&fecha=2026-10-20 */
    @GetMapping
    public List<ClaseResponse> consultarClases(
            @RequestParam(required = false) String sucursal,
            @RequestParam(required = false) Disciplina disciplina,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return claseService.consultarClases(sucursal, disciplina, fecha);
    }

    /** consultarCupo: GET /api/clases/{idClase}/cupo */
    @GetMapping("/{idClase}/cupo")
    public CupoResponse consultarCupo(@PathVariable Long idClase) {
        return claseService.consultarCupo(idClase);
    }

    /** actualizarCupo: PATCH /api/clases/{idClase}/cupo con {"operacion": "RESERVAR" | "LIBERAR"} */
    @PatchMapping("/{idClase}/cupo")
    public CupoResponse actualizarCupo(@PathVariable Long idClase, @RequestBody CupoUpdateRequest request) {
        return claseService.actualizarCupo(idClase, request.operacion());
    }
}
