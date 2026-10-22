package com.api.gimnasio.repositories;

import com.api.gimnasio.models.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    boolean existsByNumeroSocioAndClaseId(String numeroSocio, Long claseId);

    List<Inscripcion> findByNumeroSocioOrderByFechaRegistroDesc(String numeroSocio);
}
