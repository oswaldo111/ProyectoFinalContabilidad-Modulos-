package com.sistema.modulos.compras.Services;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.compras.DAO.FacturaCompraDAO;
import com.sistema.modulos.compras.Models.FacturaCompra;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class FacturaCompraService {

    private final FacturaCompraDAO facturaDAO;
    private static final BigDecimal TASA_IVA = new BigDecimal("0.13");

    public FacturaCompraService() {
        this.facturaDAO = new FacturaCompraDAO();
    }

    public Long registrarFacturaCompra(FacturaCompra factura) throws Exception {
        validarSesionActiva();
        validarFacturaCompra(factura);

        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();

        if (facturaDAO.existeNumeroDocumento(factura.getNumeroDocumento(), idEmpresa)) {
            throw new Exception("Ya existe una factura con el número: " + factura.getNumeroDocumento());
        }

        calcularMontosFactura(factura);

        factura.setIdEmpresa(idEmpresa);

        try {
            Long idFactura = facturaDAO.guardar(factura);

            if (idFactura == null) {
                throw new Exception("No se pudo registrar la factura");
            }

            return idFactura;

        } catch (Exception e) {
            throw new Exception("Error de base de datos: " + e.getMessage());
        }
    }

    public boolean procesarPago(Long idFactura, BigDecimal montoPago) throws Exception {
        validarSesionActiva();

        if (montoPago == null || montoPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El monto de pago debe ser mayor a cero");
        }

        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();
        FacturaCompra factura = facturaDAO.obtenerPorId(idFactura, idEmpresa);

        if (factura == null) {
            throw new Exception("Factura no encontrada");
        }

        if ("PAGADO".equals(factura.getEstadoPago())) {
            throw new Exception("La factura ya está completamente pagada");
        }

        if (montoPago.compareTo(factura.getSaldoPendiente()) > 0) {
            throw new Exception("El monto excede el saldo pendiente: " + factura.getSaldoPendiente());
        }

        try {
            boolean actualizado = facturaDAO.actualizarEstadoPago(idFactura, idEmpresa, montoPago);
            if (!actualizado)
                throw new Exception("No se pudo procesar el pago");
            return true;

        } catch (Exception e) {
            throw new Exception("Error de base de datos: " + e.getMessage());
        }
    }

    public List<FacturaCompra> listarFacturasPendientes() throws Exception {
        validarSesionActiva();
        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();
        return facturaDAO.listarPendientesPorEmpresa(idEmpresa);
    }

    public FacturaCompra obtenerFactura(Long idFactura) throws Exception {
        validarSesionActiva();
        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();
        return facturaDAO.obtenerPorId(idFactura, idEmpresa);
    }

    private void calcularMontosFactura(FacturaCompra factura) {
        BigDecimal montoGravado = factura.getMontoGravado();
        BigDecimal montoIva = BigDecimal.ZERO;
        BigDecimal montoTotal = BigDecimal.ZERO;

        if ("EXP".equals(factura.getTipoDocumento())) {
            montoGravado = factura.getMontoTotal();
            montoIva = BigDecimal.ZERO;
            montoTotal = montoGravado;

        } else if ("FAC".equals(factura.getTipoDocumento())) {
            if (factura.getMontoTotal() != null) {
                BigDecimal divisor = BigDecimal.ONE.add(TASA_IVA);
                montoGravado = factura.getMontoTotal().divide(divisor, 2, RoundingMode.HALF_UP);
                montoIva = factura.getMontoTotal().subtract(montoGravado);
                montoTotal = factura.getMontoTotal();
            } else {
                montoIva = montoGravado.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
                montoTotal = montoGravado.add(montoIva);
            }

        } else {
            montoIva = montoGravado.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
            montoTotal = montoGravado.add(montoIva);
        }

        factura.setMontoGravado(montoGravado);
        factura.setMontoIva(montoIva);
        factura.setMontoTotal(montoTotal);
        factura.setSaldoPendiente(montoTotal);
    }

    private void validarFacturaCompra(FacturaCompra factura) throws Exception {
        if (factura.getIdEntidad() == null)
            throw new Exception("El proveedor es obligatorio");
        if (factura.getNumeroDocumento() == null || factura.getNumeroDocumento().trim().isEmpty())
            throw new Exception("El número de documento es obligatorio");
        if (factura.getTipoDocumento() == null)
            throw new Exception("El tipo de documento es obligatorio");

        if (!"CCF".equals(factura.getTipoDocumento()) &&
                !"FAC".equals(factura.getTipoDocumento()) &&
                !"EXP".equals(factura.getTipoDocumento())) {
            throw new Exception("Tipo de documento no válido (CCF, FAC, EXP)");
        }

        if (factura.getFechaEmision() == null)
            throw new Exception("La fecha de emisión es obligatoria");
        if (factura.getFechaEmision().isAfter(LocalDate.now()))
            throw new Exception("La fecha no puede ser futura");
    }

    public List<FacturaCompra> listarFacturasPendientesConNombre() throws Exception {
        validarSesionActiva();
        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();
        return facturaDAO.listarPendientesConNombre(idEmpresa);
    }

    private void validarSesionActiva() throws Exception {
        if (!SessionManager.getInstancia().haySesionActiva()) {
            throw new Exception("No hay una sesión activa. Por favor, inicie sesión primero.");
        }
    }
}