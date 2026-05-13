package com.sistema.modulos.compras.Models;

import java.math.BigDecimal;

public class Producto {
    private int idProducto;
    private String nombreProducto;
    private int existencias;
    private BigDecimal precioVenta;
    private BigDecimal costoUnitario;
    private String nombreCategoria;
    
    public Producto() {}
    
    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public int getExistencias() { return existencias; }
    public void setExistencias(int existencias) { this.existencias = existencias; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }
    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
    
    @Override
    public String toString() {
        return nombreProducto + " - $" + costoUnitario;
    }
}