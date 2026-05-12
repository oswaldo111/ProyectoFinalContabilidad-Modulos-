package com.sistema.modulos.fiscal.Services;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.fiscal.DAO.LibroVentasDAO;
import com.sistema.modulos.fiscal.Models.RegistroLibroVentas;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibroVentasService {

    private final LibroVentasDAO libroVentasDAO;

    public LibroVentasService() {
        this.libroVentasDAO = new LibroVentasDAO();
    }

    /**
     * Genera el Libro de Ventas IVA para el período indicado.
     */
    public List<RegistroLibroVentas> generarLibroVentas(int mes, int anio) throws Exception {
        validarSesionActiva();
        validarPeriodo(mes, anio);

        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();

        try {
            return libroVentasDAO.obtenerLibroVentas(idEmpresa, mes, anio);
        } catch (Exception e) {
            throw new Exception("Error al generar Libro de Ventas: " + e.getMessage());
        }
    }

    /**
     * Obtiene los totales consolidados de ventas del período.
     * Retorna un mapa con las claves: "gravadas", "exentas", "debitoFiscal", "totalGeneral"
     */
    public Map<String, BigDecimal> obtenerTotalesVentas(int mes, int anio) throws Exception {
        validarSesionActiva();
        validarPeriodo(mes, anio);

        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();

        try {
            Map<String, BigDecimal> totales = new HashMap<>();
            totales.put("gravadas", libroVentasDAO.obtenerTotalVentasGravadas(idEmpresa, mes, anio));
            totales.put("exentas", libroVentasDAO.obtenerTotalVentasExentas(idEmpresa, mes, anio));
            totales.put("debitoFiscal", libroVentasDAO.obtenerTotalDebitoFiscal(idEmpresa, mes, anio));

            BigDecimal totalGeneral = totales.get("gravadas")
                    .add(totales.get("exentas"))
                    .add(totales.get("debitoFiscal"));
            totales.put("totalGeneral", totalGeneral);

            return totales;
        } catch (Exception e) {
            throw new Exception("Error al obtener totales de ventas: " + e.getMessage());
        }
    }

    private void validarPeriodo(int mes, int anio) throws Exception {
        if (mes < 1 || mes > 12) {
            throw new Exception("El mes debe estar entre 1 y 12.");
        }
        if (anio < 2000 || anio > 2100) {
            throw new Exception("El año no es válido.");
        }
    }

    private void validarSesionActiva() throws Exception {
        if (!SessionManager.getInstancia().haySesionActiva()) {
            throw new Exception("No hay una sesión activa. Por favor, inicie sesión primero.");
        }
    }
}
