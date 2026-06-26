package com.sistema.modulos.contabilidad.Models;

/**
 * Modelo simple para representar una Empresa.
 * Módulo de Contabilidad
 */
public class Empresa {

    private int idEmpresa;
    private String nombreEmpresa;

    public Empresa() {}

    public Empresa(int idEmpresa, String nombreEmpresa) {
        this.idEmpresa = idEmpresa;
        this.nombreEmpresa = nombreEmpresa;
    }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    @Override
    public String toString() {
        return nombreEmpresa;
    }
}
