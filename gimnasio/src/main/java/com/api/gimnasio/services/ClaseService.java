package com.api.gimnasio.services;

import com.api.gimnasio.dtos.ClaseResponse;
import com.api.gimnasio.dtos.CupoResponse;
import com.api.gimnasio.dtos.OperacionCupo;
import com.api.gimnasio.models.CodigoError;
import com.api.gimnasio.models.Clase;
import com.api.gimnasio.models.Disciplina;
import com.api.gimnasio.repositories.ClaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de entidad Clase (dominio Clases grupales).
 * Capacidades: consultarClases, consultarCupo y actualizarCupo.
 */
@Service
public class ClaseService {

    private final ClaseRepository claseRepository;

    public ClaseService(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    /** consultarClases: clases que aún no inician, con filtros opcionales. */
    @Transactional(readOnly = true)
    public List<ClaseResponse> consultarClases(String sucursal, Disciplina disciplina, LocalDate fecha) {
        return claseRepository.findByFechaHoraAfterOrderByFechaHoraAsc(LocalDateTime.now()).stream()
                .filter(c -> sucursal == null || sucursal.isBlank() || c.getSucursal().equalsIgnoreCase(sucursal.trim()))
                .filter(c -> disciplina == null || c.getDisciplina() == disciplina)
                .filter(c -> fecha == null || c.getFechaHora().toLocalDate().equals(fecha))
                .map(c -> new ClaseResponse(c.getId(), c.getNombre(), c.getDisciplina(),
                        c.getSucursal(), c.getFechaHora(), c.getCupoDisponible()))
                .toList();
    }

    /** consultarCupo: solo lectura, no reserva ningún lugar. */
    @Transactional(readOnly = true)
    public CupoResponse consultarCupo(Long idClase) {
        return aCupoResponse(obtenerClase(idClase));
    }

    /**
     * actualizarCupo: reserva o libera un lugar de forma atómica.
     * Si la clase ya tiene 20 inscritos, la reserva se rechaza con CLASE_SIN_CUPO.
     */
    @Transactional
    public CupoResponse actualizarCupo(Long idClase, OperacionCupo operacion) {
        validarId(idClase);
        if (operacion == null) {
            throw new ApiException(CodigoError.DATOS_INVALIDOS, "La operación debe ser RESERVAR o LIBERAR.");
        }
        int filas = (operacion == OperacionCupo.RESERVAR)
                ? claseRepository.reservarLugar(idClase)
                : claseRepository.liberarLugar(idClase);

        Clase clase = obtenerClase(idClase); // lanza CLASE_NO_ENCONTRADA si no existe
        if (filas == 0 && operacion == OperacionCupo.RESERVAR) {
            throw new ApiException(CodigoError.CLASE_SIN_CUPO,
                    "La clase ya tiene " + clase.getCapacidadMaxima() + " inscritos.");
        }
        return aCupoResponse(clase);
    }

    /** Uso interno de los demás servicios (no es un endpoint). */
    @Transactional(readOnly = true)
    public Clase obtenerClase(Long idClase) {
        validarId(idClase);
        return claseRepository.findById(idClase)
                .orElseThrow(() -> new ApiException(CodigoError.CLASE_NO_ENCONTRADA,
                        "No existe la clase con id " + idClase + "."));
    }

    private void validarId(Long idClase) {
        if (idClase == null || idClase <= 0) {
            throw new ApiException(CodigoError.DATOS_INVALIDOS, "idClase debe ser un número entero positivo.");
        }
    }

    private CupoResponse aCupoResponse(Clase c) {
        int disponible = Math.max(0, c.getCupoDisponible());
        return new CupoResponse(c.getId(), c.getCapacidadMaxima(), c.getLugaresOcupados(), disponible, disponible > 0);
    }
}
