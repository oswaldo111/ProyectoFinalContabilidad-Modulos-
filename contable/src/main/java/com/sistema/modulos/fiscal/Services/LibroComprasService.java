package com.sistema.modulos.fiscal.Services;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.fiscal.DAO.LibroComprasDAO;
import com.sistema.modulos.fiscal.Models.RegistroLibroCompras;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibroComprasService {

    private final LibroComprasDAO libroComprasDAO;

    public LibroComprasService() {
        this.libroComprasDAO = new LibroComprasDAO();
    }

    /**
     * Genera el Libro de Compras IVA para el período indicado.
     */
    public List<RegistroLibroCompras> generarLibroCompras(int mes, int anio) throws Exception {
        validarSesionActiva();
        validarPeriodo(mes, anio);

        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();

        try {
            return libroComprasDAO.obtenerLibroCompras(idEmpresa, mes, anio);
        } catch (Exception e) {
            throw new Exception("Error al generar Libro de Compras: " + e.getMessage());
        }
    }

    /**
     * Obtiene los totales consolidados de compras del período.
     * Retorna un mapa con las claves: "gravadas", "exentas", "creditoFiscal", "totalGeneral"
     */
    public Map<String, BigDecimal> obtenerTotalesCompras(int mes, int anio) throws Exception {
        validarSesionActiva();
        validarPeriodo(mes, anio);

        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();

        try {
            Map<String, BigDecimal> totales = new HashMap<>();
            totales.put("gravadas", libroComprasDAO.obtenerTotalComprasGravadas(idEmpresa, mes, anio));
            totales.put("exentas", libroComprasDAO.obtenerTotalComprasExentas(idEmpresa, mes, anio));
            totales.put("creditoFiscal", libroComprasDAO.obtenerTotalCreditoFiscal(idEmpresa, mes, anio));

            BigDecimal totalGeneral = totales.get("gravadas")
                    .add(totales.get("exentas"))
                    .add(totales.get("creditoFiscal"));
            totales.put("totalGeneral", totalGeneral);

            return totales;
        } catch (Exception e) {
            throw new Exception("Error al obtener totales de compras: " + e.getMessage());
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
