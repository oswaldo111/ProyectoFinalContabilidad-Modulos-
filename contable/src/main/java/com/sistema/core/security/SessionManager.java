package com.sistema.core.security;

/**
 * Singleton para manejar la sesión de usuario y empresa seleccionada
 * REQUISITO: Todos los DAOs/Services deben usar id_empresa de esta clase
 */
public class SessionManager {
    
    private static SessionManager instancia;
    private Long idEmpresa;
    private String nombreEmpresa;
    private Long idUsuario;
    private String nombreUsuario;

    private SessionManager() {}

    public static synchronized SessionManager getInstancia() {
        if (instancia == null) {
            instancia = new SessionManager();
        }
        return instancia;
    }

    public void iniciarSesion(Long idEmpresa, String nombreEmpresa, Long idUsuario, String nombreUsuario) {
        this.idEmpresa = idEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
    }

    public Long getIdEmpresa() {
        if (idEmpresa == null) {
            throw new IllegalStateException("❌ No hay empresa seleccionada. Inicie sesión primero.");
        }
        return idEmpresa;
    }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public Long getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }

    /**
     * Verifica si hay una sesión activa
     */
    public boolean haySesionActiva() {
        return idEmpresa != null && idUsuario != null;
    }

    public void cerrarSesion() {
        this.idEmpresa = null;
        this.nombreEmpresa = null;
        this.idUsuario = null;
        this.nombreUsuario = null;
    }
}