package com.api.gimnasio.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * Inscripción de un socio a una clase. El id es el folio de la inscripción.
 * La restricción única evita inscripciones duplicadas aun con peticiones simultáneas.
 */
@Entity
@Table(name = "inscripciones",
        uniqueConstraints = @UniqueConstraint(name = "uk_inscripcion_socio_clase",
                columnNames = {"numero_socio", "clase_id"}))
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_socio", nullable = false, length = 8)
    private String numeroSocio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clase_id", nullable = false)
    private Clase clase;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInscripcion estado;

    protected Inscripcion() {
    }

    public Inscripcion(String numeroSocio, Clase clase) {
        this.numeroSocio = numeroSocio;
        this.clase = clase;
        this.fechaRegistro = LocalDateTime.now();
        this.estado = EstadoInscripcion.CONFIRMADA;
    }

    public Long getId() { return id; }
    public String getNumeroSocio() { return numeroSocio; }
    public Clase getClase() { return clase; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public EstadoInscripcion getEstado() { return estado; }
}
