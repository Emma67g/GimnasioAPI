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
 * Mensaje pendiente o enviado. Funciona como cola de mensajes asíncronos:
 * el servicio de tarea solo la registra y un proceso programado la despacha.
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_socio", nullable = false, length = 8)
    private String numeroSocio;

    @Column(name = "id_clase", nullable = false)
    private Long idClase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoNotificacion tipo;

    @Column(name = "enviar_en", nullable = false)
    private LocalDateTime enviarEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(nullable = false)
    private int intentos = 0;

    @Column(nullable = false, length = 300)
    private String mensaje;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    protected Notificacion() {
    }

    public Notificacion(String numeroSocio, Long idClase, TipoNotificacion tipo, LocalDateTime enviarEn, String mensaje) {
        this.numeroSocio = numeroSocio;
        this.idClase = idClase;
        this.tipo = tipo;
        this.enviarEn = enviarEn;
        this.mensaje = mensaje;
    }

    public void marcarEnviada() {
        this.estado = EstadoNotificacion.ENVIADA;
        this.fechaEnvio = LocalDateTime.now();
    }

    public void registrarFallo(int maxIntentos) {
        this.intentos++;
        if (this.intentos >= maxIntentos) {
            this.estado = EstadoNotificacion.FALLIDA;
        }
    }

    public Long getId() { return id; }
    public String getNumeroSocio() { return numeroSocio; }
    public Long getIdClase() { return idClase; }
    public TipoNotificacion getTipo() { return tipo; }
    public LocalDateTime getEnviarEn() { return enviarEn; }
    public EstadoNotificacion getEstado() { return estado; }
    public int getIntentos() { return intentos; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
}
