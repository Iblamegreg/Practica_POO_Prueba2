package com.example.poo_practica_prueba_00;

public class Estudiante {
    private int id;
    private String cedula;
    private String nombre;
    private String carrera;
    private boolean activo;

    public Estudiante(int id, String cedula, String nombre, String carrera, boolean activo) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.carrera = carrera;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
