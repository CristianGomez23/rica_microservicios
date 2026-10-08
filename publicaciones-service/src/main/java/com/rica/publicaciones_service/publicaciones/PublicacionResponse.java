package com.rica.publicaciones_service.publicaciones;

import java.util.Map;

public class PublicacionResponse {
    private String id;
    private String titulo;
    private String tipo;
    private Integer anio;
    private Map<String, String> detalles;


    public PublicacionResponse(String id, String titulo, String tipo, Integer anio,
                               Map<String, String> detalles, String investigadorCorreo, boolean huerfana) {
        this.investigadorCorreo = investigadorCorreo;
        this.huerfana = huerfana;
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.anio = anio;
        this.detalles = detalles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Map<String, String> getDetalles() {
        return detalles;
    }

    public void setDetalles(Map<String, String> detalles, String investigadorCorreo, boolean huerfana) {
        this.investigadorCorreo = investigadorCorreo;
        this.huerfana = huerfana;
        this.detalles = detalles;
    }

    private String investigadorCorreo;
    private boolean huerfana;
    public String getInvestigadorCorreo() { return investigadorCorreo; }
    public boolean isHuerfana() { return huerfana; }

}
