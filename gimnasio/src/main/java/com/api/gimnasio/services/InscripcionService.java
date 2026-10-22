package com.api.gimnasio.services;

import com.api.gimnasio.dtos.InscripcionResponse;
import com.api.gimnasio.models.CodigoError;
import com.api.gimnasio.models.Clase;
import com.api.gimnasio.models.Inscripcion;
import com.api.gimnasio.repositories.InscripcionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de entidad Inscripción (dominio Clases grupales).
 * Capacidad: registrarInscripcion (interna) y consultas de inscripciones.
 */
@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;

    public InscripcionService(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    /** Falla con INSCRIPCION_DUPLICADA si el socio ya está inscrito en la clase. */
    @Transactional(readOnly = true)
    public void validarInscripcionNueva(String numeroSocio, Long idClase) {
        if (inscripcionRepository.existsByNumeroSocioAndClaseId(numeroSocio, idClase)) {
            throw duplicada();
        }
    }

    /**
     * registrarInscripcion: guarda la inscripción y genera el folio.
     * Se invoca solo desde InscribirClase, que ya validó membresía y cupo.
     */
    @Transactional
    public Inscripcion registrarInscripcion(String numeroSocio, Clase clase) {
        try {
            return inscripcionRepository.saveAndFlush(new Inscripcion(numeroSocio, clase));
        } catch (DataIntegrityViolationException e) {
            // Dos peticiones simultáneas del mismo socio: la restricción única lo detecta.
            throw duplicada();
        }
    }

    @Transactional(readOnly = true)
    public InscripcionResponse consultarInscripcion(Long folio) {
        Inscripcion i = inscripcionRepository.findById(folio)
                .orElseThrow(() -> new ApiException(CodigoError.INSCRIPCION_NO_ENCONTRADA,
                        "No existe la inscripción con folio " + folio + "."));
        return aResponse(i);
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> consultarInscripcionesDeSocio(String numeroSocio) {
        if (numeroSocio == null || numeroSocio.trim().length() != 8) {
            throw new ApiException(CodigoError.DATOS_INVALIDOS, "numeroSocio debe tener 8 caracteres.");
        }
        return inscripcionRepository.findByNumeroSocioOrderByFechaRegistroDesc(numeroSocio.trim()).stream()
                .map(this::aResponse)
                .toList();
    }

    public InscripcionResponse aResponse(Inscripcion i) {
        Clase c = i.getClase();
        return new InscripcionResponse(i.getId(), i.getEstado().name(), c.getNombre(), c.getSucursal().name(), c.getFechaHora());
    }

    private ApiException duplicada() {
        return new ApiException(CodigoError.INSCRIPCION_DUPLICADA, "El socio ya está inscrito en esta clase.");
    }
}
