package com.sistema.modulos.fiscal.Services;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.fiscal.Models.LiquidacionIva;

import java.math.BigDecimal;
import java.util.Map;

public class LiquidacionService {

    private final LibroVentasService libroVentasService;
    private final LibroComprasService libroComprasService;

    public LiquidacionService() {
        this.libroVentasService = new LibroVentasService();
        this.libroComprasService = new LibroComprasService();
    }

    /**
     * Calcula la liquidación del período fiscal.
     * Cruza el Débito Fiscal (ventas) contra el Crédito Fiscal (compras)
     * y determina si hay Impuesto a Pagar o Remanente a Favor.
     *
     * @param mes                  Mes del período (1-12)
     * @param anio                 Año del período
     * @param remanenteMesAnterior Remanente de crédito fiscal del mes anterior (puede ser 0)
     * @return Objeto LiquidacionIva con todos los valores calculados
     */
    public LiquidacionIva calcularLiquidacion(int mes, int anio, BigDecimal remanenteMesAnterior) throws Exception {
        validarSesionActiva();

        if (remanenteMesAnterior == null) {
            remanenteMesAnterior = BigDecimal.ZERO;
        }

        if (remanenteMesAnterior.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El remanente del mes anterior no puede ser negativo.");
        }

        try {
            // Obtener totales de ventas
            Map<String, BigDecimal> totalesVentas = libroVentasService.obtenerTotalesVentas(mes, anio);

            // Obtener totales de compras
            Map<String, BigDecimal> totalesCompras = libroComprasService.obtenerTotalesCompras(mes, anio);

            // Construir el objeto de liquidación
            LiquidacionIva liquidacion = new LiquidacionIva();
            liquidacion.setMes(mes);
            liquidacion.setAnio(anio);

            // Datos de ventas
            liquidacion.setTotalVentasGravadas(totalesVentas.get("gravadas"));
            liquidacion.setTotalVentasExentas(totalesVentas.get("exentas"));
            liquidacion.setTotalDebitoFiscal(totalesVentas.get("debitoFiscal"));

            // Datos de compras
            liquidacion.setTotalComprasGravadas(totalesCompras.get("gravadas"));
            liquidacion.setTotalComprasExentas(totalesCompras.get("exentas"));
            liquidacion.setTotalCreditoFiscal(totalesCompras.get("creditoFiscal"));

            // Remanente del mes anterior
            liquidacion.setRemanenteMesAnterior(remanenteMesAnterior);

            // Calcular resultado (impuesto o remanente)
            liquidacion.calcular();

            return liquidacion;

        } catch (Exception e) {
            throw new Exception("Error al calcular liquidación de IVA: " + e.getMessage());
        }
    }

    private void validarSesionActiva() throws Exception {
        if (!SessionManager.getInstancia().haySesionActiva()) {
            throw new Exception("No hay una sesión activa. Por favor, inicie sesión primero.");
        }
    }
}
