package com.api.gimnasio.services;

import com.api.gimnasio.dtos.InscripcionRequest;
import com.api.gimnasio.dtos.InscripcionResponse;
import com.api.gimnasio.dtos.CupoResponse;
import com.api.gimnasio.dtos.OperacionCupo;
import com.api.gimnasio.models.CodigoError;
import com.api.gimnasio.models.Clase;
import com.api.gimnasio.models.Inscripcion;
import com.api.gimnasio.models.Membresia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio de tarea InscribirClase (dominio Clases grupales).
 * Capacidad: inscribirEnClase. Coordina a Clase, Membresía, Inscripción y Notificación.
 *
 * Todo el proceso corre en una sola transacción: si cualquier paso falla, el lugar
 * reservado en la clase se libera (rollback) y no se programa ninguna notificación.
 */
@Service
public class InscribirClaseService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final ClaseService claseService;
    private final MembresiaService membresiaService;
    private final InscripcionService inscripcionService;
    private final NotificacionService notificacionService;
    private final long horasAntesRecordatorio;

    public InscribirClaseService(ClaseService claseService,
                                 MembresiaService membresiaService,
                                 InscripcionService inscripcionService,
                                 NotificacionService notificacionService,
                                 @Value("${fitzone.recordatorio.horas-antes:2}") long horasAntesRecordatorio) {
        this.claseService = claseService;
        this.membresiaService = membresiaService;
        this.inscripcionService = inscripcionService;
        this.notificacionService = notificacionService;
        this.horasAntesRecordatorio = horasAntesRecordatorio;
    }

    @Transactional
    public InscripcionResponse inscribirEnClase(InscripcionRequest request) {
        validar(request);
        String numeroSocio = request.numeroSocio().trim();
        Long idClase = request.idClase();

        // 1. La clase debe existir y no haber iniciado.
        Clase clase = claseService.obtenerClase(idClase);
        if (!clase.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new ApiException(CodigoError.CLASE_YA_INICIADA, "La clase ya inició o ya terminó.");
        }

        // 2. Membresía vigente el día de la clase (consultarMembresia).
        Membresia membresia = membresiaService.obtenerMembresia(numeroSocio);
        if (!membresiaService.estaVigenteEn(membresia, clase.getFechaHora().toLocalDate())) {
            throw new ApiException(CodigoError.MEMBRESIA_VENCIDA,
                    "La membresía vence el " + membresia.getFechaVencimiento().format(FECHA)
                            + ", antes de la fecha de la clase.");
        }

        // 3. El socio no debe estar ya inscrito.
        inscripcionService.validarInscripcionNueva(numeroSocio, idClase);

        // 4. Debe haber cupo (consultarCupo) y el lugar se reserva de forma atómica (actualizarCupo).
        CupoResponse cupo = claseService.consultarCupo(idClase);
        if (!cupo.hayCupo()) {
            throw new ApiException(CodigoError.CLASE_SIN_CUPO,
                    "La clase ya tiene " + cupo.capacidadMaxima() + " inscritos.");
        }
        claseService.actualizarCupo(idClase, OperacionCupo.RESERVAR);

        // 5. Registrar la inscripción (registrarInscripcion).
        Inscripcion inscripcion = inscripcionService.registrarInscripcion(numeroSocio, clase);

        // 6. Mensajes asíncronos y unidireccionales (enviarConfirmacion, programarRecordatorio).
        String fecha = clase.getFechaHora().format(FECHA);
        String hora = clase.getFechaHora().format(HORA);
        notificacionService.enviarConfirmacion(numeroSocio, idClase,
                "Hola " + membresia.getNombreSocio() + ", quedaste inscrito en " + clase.getNombre()
                        + " (sucursal " + clase.getSucursal() + ") el " + fecha + " a las " + hora
                        + ". Folio: " + inscripcion.getId() + ".");

        LocalDateTime momentoRecordatorio = clase.getFechaHora().minusHours(horasAntesRecordatorio);
        if (momentoRecordatorio.isBefore(LocalDateTime.now())) {
            momentoRecordatorio = LocalDateTime.now(); // la clase empieza en menos de 2 horas
        }
        notificacionService.programarRecordatorio(numeroSocio, idClase, momentoRecordatorio,
                "Recordatorio: tu clase " + clase.getNombre() + " en la sucursal " + clase.getSucursal()
                        + " comienza hoy a las " + hora + ".");

        return inscripcionService.aResponse(inscripcion);
    }

    private void validar(InscripcionRequest request) {
        if (request == null || request.numeroSocio() == null || request.numeroSocio().trim().length() != 8) {
            throw new ApiException(CodigoError.DATOS_INVALIDOS, "numeroSocio debe tener 8 caracteres.");
        }
        if (request.idClase() == null || request.idClase() <= 0) {
            throw new ApiException(CodigoError.DATOS_INVALIDOS, "idClase debe ser un número entero positivo.");
        }
    }
}
