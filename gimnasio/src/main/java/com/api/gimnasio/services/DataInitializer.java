package com.api.gimnasio.services;

import com.api.gimnasio.models.Clase;
import com.api.gimnasio.models.Disciplina;
import com.api.gimnasio.models.Membresia;
import com.api.gimnasio.models.Sucursal;
import com.api.gimnasio.repositories.ClaseRepository;
import com.api.gimnasio.repositories.MembresiaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Datos de prueba que se cargan solo si las tablas están vacías.
 * Fechas relativas a hoy para que las clases siempre sean futuras.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ClaseRepository claseRepository;
    private final MembresiaRepository membresiaRepository;

    public DataInitializer(ClaseRepository claseRepository, MembresiaRepository membresiaRepository) {
        this.claseRepository = claseRepository;
        this.membresiaRepository = membresiaRepository;
    }

    @Override
    public void run(String... args) {
        if (claseRepository.count() == 0) {
            LocalDate manana = LocalDate.now().plusDays(1);
            Clase llena = new Clase("Spinning express", Disciplina.SPINNING, Sucursal.CENTRO, manana.atTime(20, 0));
            llena.setLugaresOcupados(Clase.CAPACIDAD_MAXIMA);

            claseRepository.saveAll(List.of(
                    new Clase("Yoga matutino", Disciplina.YOGA, Sucursal.CENTRO, manana.atTime(7, 0)),
                    new Clase("Spinning vespertino", Disciplina.SPINNING, Sucursal.NORTE, manana.atTime(18, 0)),
                    new Clase("Funcional intenso", Disciplina.FUNCIONAL, Sucursal.SUR, manana.plusDays(1).atTime(19, 0)),
                    llena));
        }
        if (membresiaRepository.count() == 0) {
            LocalDate hoy = LocalDate.now();
            membresiaRepository.saveAll(List.of(
                    new Membresia("FZ000001", "Ana Lopez", "ana@example.com", hoy.plusDays(30)),
                    new Membresia("FZ000002", "Carlos Ruiz", "carlos@example.com", hoy.minusDays(5)),
                    new Membresia("FZ000003", "Marta Soto", "marta@example.com", hoy.plusDays(90))));
        }
    }
}
