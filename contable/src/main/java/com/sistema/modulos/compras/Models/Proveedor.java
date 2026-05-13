package com.sistema.modulos.compras.Models;

public class Proveedor {
    private int idEntidad;
    private String nombre;
    private String nrc;
    private String nit;
    private String dui;
    private String telefono;
    private String direccion;
    
    public Proveedor() {}
    
    public Proveedor(int idEntidad, String nombre, String nrc, String nit, String dui, String telefono, String direccion) {
        this.idEntidad = idEntidad;
        this.nombre = nombre;
        this.nrc = nrc;
        this.nit = nit;
        this.dui = dui;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    
    // Getters y Setters
    public int getIdEntidad() { return idEntidad; }
    public void setIdEntidad(int idEntidad) { this.idEntidad = idEntidad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNrc() { return nrc; }
    public void setNrc(String nrc) { this.nrc = nrc; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    @Override
    public String toString() {
        return nombre + " - NIT: " + nit;
    }
}