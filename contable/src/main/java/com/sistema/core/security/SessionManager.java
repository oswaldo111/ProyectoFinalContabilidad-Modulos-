package com.sistema.core.security;

/**
 * Singleton para manejar la sesión de usuario y empresa seleccionada
 * REQUISITO: Todos los DAOs/Services deben usar id_empresa de esta clase
 */
public class SessionManager {
    
    private static int idEmpresa = 1;
    private static String nombreEmpresa = "";
    private static int idUsuario = 1;
    private static String nombreUsuario = "";

    private SessionManager() {}

    public static synchronized SessionManager getInstancia() {
        return new SessionManager();
    }

    public static void iniciarSesion(int idEmpresa, String nombreEmpresa, int idUsuario, String nombreUsuario) {
        SessionManager.idEmpresa = idEmpresa;
        SessionManager.nombreEmpresa = nombreEmpresa;
        SessionManager.idUsuario = idUsuario;
        SessionManager.nombreUsuario = nombreUsuario;
    }

    public static int getIdEmpresa() {
        return idEmpresa;
    }

    public static String getNombreEmpresa() { return nombreEmpresa; }
    public static int getIdUsuario() { return idUsuario; }
    public static String getNombreUsuario() { return nombreUsuario; }

    public static boolean haySesionActiva() {
        return idEmpresa > 0 && idUsuario > 0;
    }
    public static void cerrarSesion() {
        idEmpresa = 1;
        nombreEmpresa = "";
        idUsuario = 1;
        nombreUsuario = "";
    }
}