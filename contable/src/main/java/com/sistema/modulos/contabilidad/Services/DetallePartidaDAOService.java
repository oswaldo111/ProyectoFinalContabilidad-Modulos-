package com.sistema.modulos.contabilidad.Services;

import java.util.List;

import com.sistema.modulos.contabilidad.DAO.DetallePartidaDAO;
import com.sistema.modulos.contabilidad.Models.DetallePartida;

public class DetallePartidaService {

    private DetallePartidaDAO detallePartidaDAO;

    // Constructor
    public DetallePartidaService() {
        this.detallePartidaDAO = new DetallePartidaDAO();
    }

    // =========================
    // CREAR OBJETO
    // =========================
    public DetallePartida crearDetallePartida() {
        return detallePartidaDAO.crear();
    }

    // =========================
    // INSERTAR
    // =========================
    public void guardarDetallePartida(DetallePartida detallePartida) {
        detallePartidaDAO.insertar(detallePartida);
    }

    // =========================
    // LISTAR
    // =========================
    public List<DetallePartida> obtenerDetallesPartida() {
        return detallePartidaDAO.mostrar();
    }

    // =========================
    // ACTUALIZAR
    // =========================
    public void actualizarDetallePartida(DetallePartida detallePartida) {
        detallePartidaDAO.actualizar(detallePartida);
    }
}