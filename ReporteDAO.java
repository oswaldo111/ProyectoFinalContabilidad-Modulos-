package DAO;

import java.sql.*;
import java.util.ArrayList;
import Models.ReporteCuenta;

public class ReporteDAO {

    Connection con;

    public ReporteDAO(Connection con) {
        this.con = con;
    }

    public ArrayList<ReporteCuenta> obtenerBalance() {

        ArrayList<ReporteCuenta> lista = new ArrayList<>();

        String sql = """
            SELECT
                c.codigo,
                c.nombre,
                SUM(dp.debe) AS total_debe,
                SUM(dp.haber) AS total_haber,
                SUM(dp.debe - dp.haber) AS saldo
            FROM cuentas c
            INNER JOIN detalle_partida dp
            ON c.id_cuenta = dp.id_cuenta
            GROUP BY c.codigo, c.nombre
            ORDER BY c.codigo
        """;

        try {

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                ReporteCuenta r = new ReporteCuenta();

                r.setCodigo(rs.getString("codigo"));
                r.setNombre(rs.getString("nombre"));
                r.setDebe(rs.getDouble("total_debe"));
                r.setHaber(rs.getDouble("total_haber"));
                r.setSaldo(rs.getDouble("saldo"));

                lista.add(r);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}

