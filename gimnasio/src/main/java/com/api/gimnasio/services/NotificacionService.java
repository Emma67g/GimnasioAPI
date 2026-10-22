package com.api.gimnasio.services;

import com.api.gimnasio.dtos.NotificacionResponse;
import com.api.gimnasio.models.CodigoError;
import com.api.gimnasio.models.EstadoNotificacion;
import com.api.gimnasio.models.Notificacion;
import com.api.gimnasio.models.TipoNotificacion;
import com.api.gimnasio.repositories.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de Notificación.
 * Capacidades: enviarConfirmacion y programarRecordatorio.
 *
 * Es un servicio genérico (no contiene reglas del gimnasio): solo recibe destinatario,
 * momento de envío y texto. Ambas capacidades son unidireccionales: registran el mensaje
 * como PENDIENTE y regresan sin esperar el envío; un proceso programado lo despacha.
 */
@Service
@EnableScheduling // activa el despachador de notificaciones pendientes
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);
    private static final int MAX_INTENTOS = 3;

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    /** enviarConfirmacion: mensaje para enviarse de inmediato (en el siguiente ciclo del despachador). */
    @Transactional
    public void enviarConfirmacion(String numeroSocio, Long idClase, String mensaje) {
        guardar(numeroSocio, idClase, TipoNotificacion.CONFIRMACION, LocalDateTime.now(), mensaje);
    }

    /** programarRecordatorio: mensaje para enviarse en la fecha y hora indicadas. */
    @Transactional
    public void programarRecordatorio(String numeroSocio, Long idClase, LocalDateTime enviarEn, String mensaje) {
        guardar(numeroSocio, idClase, TipoNotificacion.RECORDATORIO, enviarEn, mensaje);
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> consultarNotificaciones(String numeroSocio) {
        if (numeroSocio == null || numeroSocio.trim().length() != 8) {
            throw new ApiException(CodigoError.DATOS_INVALIDOS, "numeroSocio debe tener 8 caracteres.");
        }
        return notificacionRepository.findByNumeroSocioOrderByEnviarEnAsc(numeroSocio.trim()).stream()
                .map(n -> new NotificacionResponse(n.getId(), n.getNumeroSocio(), n.getIdClase(),
                        n.getTipo().name(), n.getEnviarEn(), n.getEstado().name(), n.getMensaje()))
                .toList();
    }

    /** Despachador: revisa periódicamente los mensajes cuya hora de envío ya llegó. */
    @Scheduled(fixedDelayString = "${fitzone.notificaciones.intervalo-ms:60000}")
    @Transactional
    public void despacharPendientes() {
        List<Notificacion> pendientes = notificacionRepository
                .findByEstadoAndEnviarEnLessThanEqual(EstadoNotificacion.PENDIENTE, LocalDateTime.now());
        for (Notificacion n : pendientes) {
            try {
                // Aquí se integraría el proveedor real de correo/SMS. Por ahora se simula con el log.
                log.info("[ENVIO SIMULADO] {} para el socio {}: {}", n.getTipo(), n.getNumeroSocio(), n.getMensaje());
                n.marcarEnviada();
            } catch (RuntimeException e) {
                log.warn("Fallo al enviar la notificación {}: {}", n.getId(), e.getMessage());
                n.registrarFallo(MAX_INTENTOS);
            }
        }
    }

    private void guardar(String numeroSocio, Long idClase, TipoNotificacion tipo, LocalDateTime enviarEn, String mensaje) {
        notificacionRepository.save(new Notificacion(numeroSocio, idClase, tipo, enviarEn, mensaje));
    }
}
