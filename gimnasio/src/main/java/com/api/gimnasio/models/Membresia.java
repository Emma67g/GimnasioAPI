package com.api.gimnasio.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "membresias")
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_socio", nullable = false, unique = true, length = 8)
    private String numeroSocio;

    @Column(name = "nombre_socio", nullable = false, length = 100)
    private String nombreSocio;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    protected Membresia() {
    }

    public Membresia(String numeroSocio, String nombreSocio, String correo, LocalDate fechaVencimiento) {
        this.numeroSocio = numeroSocio;
        this.nombreSocio = nombreSocio;
        this.correo = correo;
        this.fechaVencimiento = fechaVencimiento;
    }

    public Long getId() { return id; }
    public String getNumeroSocio() { return numeroSocio; }
    public String getNombreSocio() { return nombreSocio; }
    public String getCorreo() { return correo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
}
