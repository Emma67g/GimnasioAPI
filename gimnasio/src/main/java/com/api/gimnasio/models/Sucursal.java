package com.api.gimnasio.models;

/** Las tres sucursales de FitZone. */
public enum Sucursal {
    NORTE("Norte"),
    CENTRO("Centro"),
    SUR("Sur");

    private final String etiqueta;

    Sucursal(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Nombre legible, usado en los textos de las notificaciones. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
