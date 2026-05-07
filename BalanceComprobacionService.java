package Services;

import DAO.ReporteDAO;
import Models.ReporteCuenta;
import java.util.ArrayList;

public class BalanceComprobacionService {

    private ReporteDAO dao;

    public BalanceComprobacionService(ReporteDAO dao) {
        this.dao = dao;
    }

    public void mostrarBalance() {

        ArrayList<ReporteCuenta> lista = dao.obtenerBalance();

        double totalDebe = 0;
        double totalHaber = 0;

        System.out.println("===== BALANCE DE COMPROBACION =====");

        for(ReporteCuenta r : lista) {

            System.out.println(
                r.getCodigo() + " - " +
                r.getNombre() + " | Debe: $" +
                r.getDebe() + " | Haber: $" +
                r.getHaber()
            );

            totalDebe += r.getDebe();
            totalHaber += r.getHaber();
        }

        System.out.println("--------------------------------");
        System.out.println("TOTAL DEBE: $" + totalDebe);
        System.out.println("TOTAL HABER: $" + totalHaber);
    }
}