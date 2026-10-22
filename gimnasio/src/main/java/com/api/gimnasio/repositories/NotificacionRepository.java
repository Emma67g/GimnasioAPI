package com.api.gimnasio.repositories;

import com.api.gimnasio.models.EstadoNotificacion;
import com.api.gimnasio.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByEstadoAndEnviarEnLessThanEqual(EstadoNotificacion estado, LocalDateTime limite);

    List<Notificacion> findByNumeroSocioOrderByEnviarEnAsc(String numeroSocio);
}
