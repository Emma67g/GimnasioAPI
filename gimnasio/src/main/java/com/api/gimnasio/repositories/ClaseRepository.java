package com.api.gimnasio.repositories;

import com.api.gimnasio.models.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Long> {

    List<Clase> findByFechaHoraAfterOrderByFechaHoraAsc(LocalDateTime desde);

    /**
     * Reserva un lugar de forma atómica: la condición se evalúa en la propia
     * sentencia UPDATE, por lo que dos peticiones simultáneas nunca superan la capacidad.
     * Devuelve 1 si se reservó y 0 si la clase no existe o ya está llena.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Clase c SET c.lugaresOcupados = c.lugaresOcupados + 1 "
            + "WHERE c.id = :id AND c.lugaresOcupados < c.capacidadMaxima")
    int reservarLugar(@Param("id") Long id);

    /** Libera un lugar. Devuelve 0 si la clase no existe o no tiene lugares ocupados. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Clase c SET c.lugaresOcupados = c.lugaresOcupados - 1 "
            + "WHERE c.id = :id AND c.lugaresOcupados > 0")
    int liberarLugar(@Param("id") Long id);
}
