package com.api.gimnasio.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Clase grupal de una sucursal. El cupo (lugaresOcupados) es un dato de esta
 * entidad y solo se modifica mediante el servicio Clase.
 */
@Entity
@Table(name = "clases")
public class Clase {

    public static final int CAPACIDAD_MAXIMA = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Disciplina disciplina;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Sucursal sucursal;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "capacidad_maxima", nullable = false)
    private int capacidadMaxima = CAPACIDAD_MAXIMA;

    @Column(name = "lugares_ocupados", nullable = false)
    private int lugaresOcupados = 0;

    protected Clase() {
    }

    public Clase(String nombre, Disciplina disciplina, Sucursal sucursal, LocalDateTime fechaHora) {
        this.nombre = nombre;
        this.disciplina = disciplina;
        this.sucursal = sucursal;
        this.fechaHora = fechaHora;
    }

    public int getCupoDisponible() {
        return capacidadMaxima - lugaresOcupados;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public Disciplina getDisciplina() { return disciplina; }
    public Sucursal getSucursal() { return sucursal; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public int getLugaresOcupados() { return lugaresOcupados; }
    public void setLugaresOcupados(int lugaresOcupados) { this.lugaresOcupados = lugaresOcupados; }
}
