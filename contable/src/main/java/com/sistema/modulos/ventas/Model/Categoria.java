package com.sistema.modulos.ventas.Model;

public class Categoria {

    private int idCategoria;
    private int idEmpresa;
    private String categoria;

    public Categoria() {
    }

    public Categoria(int idCategoria, int idEmpresa, String categoria) {
        this.idCategoria = idCategoria;
        this.idEmpresa = idEmpresa;
        this.categoria = categoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return categoria;
    }
}