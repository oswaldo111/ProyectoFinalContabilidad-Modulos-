package com.sistema.modulos.fiscal.DAO;

import com.sistema.core.DBConnection;
import com.sistema.modulos.fiscal.Models.RegistroLibroVentas;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibroVentasDAO {

    /**
     * Obtiene todas las ventas del período para el Libro de Ventas IVA.
     */
    public List<RegistroLibroVentas> obtenerLibroVentas(Long idEmpresa, int mes, int anio) throws SQLException {
        Connection conn = null;
        List<RegistroLibroVentas> lista = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();

            String sql = """
                SELECT f.id_factura, f.fecha_emision, f.numero_documento, f.tipo_documento,
                       e.nombre, e.nrc,
                       COALESCE(f.monto_exento, 0) AS monto_exento,
                       f.monto_gravado, f.monto_iva, f.monto_total
                FROM facturacion f
                INNER JOIN entidades e ON f.id_entidad = e.id_entidad
                WHERE f.id_empresa = ? AND f.tipo_operacion = 'VENTA'
                  AND EXTRACT(MONTH FROM f.fecha_emision) = ?
                  AND EXTRACT(YEAR FROM f.fecha_emision) = ?
                ORDER BY f.fecha_emision, f.numero_documento
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEmpresa);
                ps.setInt(2, mes);
                ps.setInt(3, anio);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearRegistroVenta(rs));
                    }
                }
            }
            return lista;

        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Obtiene el total del Débito Fiscal (IVA cobrado) del período.
     */
    public BigDecimal obtenerTotalDebitoFiscal(Long idEmpresa, int mes, int anio) throws SQLException {
        return obtenerSuma(idEmpresa, mes, anio, "monto_iva");
    }

    /**
     * Obtiene el total de ventas gravadas del período.
     */
    public BigDecimal obtenerTotalVentasGravadas(Long idEmpresa, int mes, int anio) throws SQLException {
        return obtenerSuma(idEmpresa, mes, anio, "monto_gravado");
    }

    /**
     * Obtiene el total de ventas exentas del período.
     */
    public BigDecimal obtenerTotalVentasExentas(Long idEmpresa, int mes, int anio) throws SQLException {
        return obtenerSuma(idEmpresa, mes, anio, "COALESCE(monto_exento, 0)");
    }

    /**
     * Método auxiliar para obtener sumas de columnas específicas.
     */
    private BigDecimal obtenerSuma(Long idEmpresa, int mes, int anio, String columna) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            String sql = "SELECT COALESCE(SUM(" + columna + "), 0) AS total "
                       + "FROM facturacion "
                       + "WHERE id_empresa = ? AND tipo_operacion = 'VENTA' "
                       + "AND EXTRACT(MONTH FROM fecha_emision) = ? "
                       + "AND EXTRACT(YEAR FROM fecha_emision) = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEmpresa);
                ps.setInt(2, mes);
                ps.setInt(3, anio);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBigDecimal("total");
                    }
                }
            }
            return BigDecimal.ZERO;

        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Convierte un ResultSet en un objeto RegistroLibroVentas.
     */
    private RegistroLibroVentas mapearRegistroVenta(ResultSet rs) throws SQLException {
        RegistroLibroVentas r = new RegistroLibroVentas();
        r.setIdFactura(rs.getLong("id_factura"));
        r.setFechaEmision(rs.getObject("fecha_emision", LocalDate.class));
        r.setNumeroDocumento(rs.getString("numero_documento"));
        r.setTipoDocumento(rs.getString("tipo_documento"));
        r.setNombreCliente(rs.getString("nombre"));
        r.setNrc(rs.getString("nrc"));
        r.setVentasExentas(rs.getBigDecimal("monto_exento"));
        r.setVentasGravadas(rs.getBigDecimal("monto_gravado"));
        r.setDebitoFiscal(rs.getBigDecimal("monto_iva"));
        r.setVentaTotal(rs.getBigDecimal("monto_total"));
        return r;
    }
}
