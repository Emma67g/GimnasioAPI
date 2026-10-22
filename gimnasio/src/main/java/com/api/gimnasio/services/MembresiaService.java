package com.api.gimnasio.services;

import com.api.gimnasio.dtos.MembresiaResponse;
import com.api.gimnasio.models.CodigoError;
import com.api.gimnasio.models.Membresia;
import com.api.gimnasio.repositories.MembresiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Servicio de entidad Membresía (dominio Membresías).
 * Capacidad: consultarMembresia.
 */
@Service
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;

    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    @Transactional(readOnly = true)
    public MembresiaResponse consultarMembresia(String numeroSocio) {
        Membresia m = obtenerMembresia(numeroSocio);
        return new MembresiaResponse(m.getNumeroSocio(), m.getNombreSocio(), m.getFechaVencimiento(),
                estaVigenteEn(m, LocalDate.now()));
    }

    /** Uso interno de los demás servicios (no es un endpoint). */
    @Transactional(readOnly = true)
    public Membresia obtenerMembresia(String numeroSocio) {
        if (numeroSocio == null || numeroSocio.trim().length() != 8) {
            throw new ApiException(CodigoError.DATOS_INVALIDOS, "numeroSocio debe tener 8 caracteres.");
        }
        return membresiaRepository.findByNumeroSocio(numeroSocio.trim())
                .orElseThrow(() -> new ApiException(CodigoError.MEMBRESIA_NO_ENCONTRADA,
                        "No existe una membresía para el socio " + numeroSocio.trim() + "."));
    }

    /** La membresía es vigente en una fecha si no vence antes de ese día. */
    public boolean estaVigenteEn(Membresia membresia, LocalDate fecha) {
        return !membresia.getFechaVencimiento().isBefore(fecha);
    }
}
