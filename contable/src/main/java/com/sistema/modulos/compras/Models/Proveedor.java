package com.sistema.modulos.compras.Models;

public class Proveedor {
    private Long idEntidad;
    private Long idEmpresa;
    private String tipoEntidad;
    private String nombre;
    private String nit;
    private String nrc;
    private boolean esContribuyente;

    public Proveedor() {}

    public Proveedor(Long idEmpresa, String nombre, String nit, String nrc, boolean esContribuyente) {
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
        this.nit = nit;
        this.nrc = nrc;
        this.esContribuyente = esContribuyente;
        this.tipoEntidad = "PROVEEDOR";
    }

    public Long getIdEntidad() { return idEntidad; }
    public void setIdEntidad(Long idEntidad) { this.idEntidad = idEntidad; }

    public Long getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getTipoEntidad() { return tipoEntidad; }
    public void setTipoEntidad(String tipoEntidad) { this.tipoEntidad = tipoEntidad; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }

    public String getNrc() { return nrc; }
    public void setNrc(String nrc) { this.nrc = nrc; }

    public boolean isEsContribuyente() { return esContribuyente; }
    public void setEsContribuyente(boolean esContribuyente) { this.esContribuyente = esContribuyente; }

    @Override
    public String toString() {
        return nombre + " (NIT: " + nit + ")";
    }
}