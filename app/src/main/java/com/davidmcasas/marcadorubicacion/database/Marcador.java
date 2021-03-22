package com.davidmcasas.marcadorubicacion.database;

public class Marcador {

    private Integer id;
    private String nombre;
    private String icono;
    private String color;
    private String coordenadas;
    private String descripcion;

    public Marcador(Integer id, String nombre, String icono, String color, String coordenadas, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.icono = icono;
        this.color = color;
        this.coordenadas = coordenadas;
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Marcador{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", icono='" + icono + '\'' +
                ", color='" + color + '\'' +
                ", coordenadas='" + coordenadas + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIcono() {
        return icono;
    }

    public String getColor() {
        return color;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }
}
