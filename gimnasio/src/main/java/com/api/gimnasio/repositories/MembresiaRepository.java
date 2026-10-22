package com.api.gimnasio.repositories;

import com.api.gimnasio.models.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    Optional<Membresia> findByNumeroSocio(String numeroSocio);
}
