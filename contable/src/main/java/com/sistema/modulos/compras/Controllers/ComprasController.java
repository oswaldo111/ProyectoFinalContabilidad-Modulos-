package com.sistema.modulos.compras.Controllers;

import com.sistema.modulos.compras.Views.PanelCompras;
import com.sistema.modulos.compras.Views.PanelProveedores;
import com.sistema.modulos.compras.Views.PanelReporteCompras;

public class ComprasController {
    
    private final PanelProveedores proveedoresView;
    private final PanelCompras comprasView;
    private final PanelReporteCompras reporteView;
    
    public ComprasController(PanelProveedores proveedoresView, 
                           PanelCompras comprasView, 
                           PanelReporteCompras reporteView) {
        this.proveedoresView = proveedoresView;
        this.comprasView = comprasView;
        this.reporteView = reporteView;
        // Inicializar eventos comunes aquí si es necesario
    }
    
    // Métodos compartidos entre vistas (ej: guardar, consultar, exportar)
    public void guardarDatos(String tipo, Object datos) {
        // Lógica común de guardado
    }
}