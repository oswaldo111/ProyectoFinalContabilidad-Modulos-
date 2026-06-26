package com.sistema.modulos.ventas.Model;

public class Productos {

    private int idProducto;
    private int idEmpresa;
    private int idCategoria;
    private String nombreProducto;
    private int stockMinimo;
    private int existencias;
    private double precioVenta;   // Mapea a 'precio_venta'
    private String metodoCosteo;  // Mapea a 'metodo_costeo'
    private double costoUnitario; // Mapea a 'costo_unitario'

    // Constructor vacío (Obligatorio para el DAO)
    public Productos() {
    }

    // Constructor completo
    public Productos(int idProducto, int idEmpresa, int idCategoria, String nombreProducto, int stockMinimo, int existencias, double precioVenta, String metodoCosteo, double costoUnitario) {
        this.idProducto = idProducto;
        this.idEmpresa = idEmpresa;
        this.idCategoria = idCategoria;
        this.nombreProducto = nombreProducto;
        this.stockMinimo = stockMinimo;
        this.existencias = existencias;
        this.precioVenta = precioVenta;
        this.metodoCosteo = metodoCosteo;
        this.costoUnitario = costoUnitario;
    }

    // --- GETTERS Y SETTERS ---

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getExistencias() {
        return existencias;
    }

    public void setExistencias(int existencias) {
        this.existencias = existencias;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getMetodoCosteo() {
        return metodoCosteo;
    }

    public void setMetodoCosteo(String metodoCosteo) {
        this.metodoCosteo = metodoCosteo;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    // Método toString para que el combo muestre el nombre de manera limpia
    @Override
    public String toString() {
        return nombreProducto;
    }
}