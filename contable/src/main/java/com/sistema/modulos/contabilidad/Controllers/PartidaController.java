/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sistema.modulos.contabilidad.Controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.sistema.modulos.contabilidad.Models.Partida;
import com.sistema.modulos.contabilidad.Services.PartidaService;

public class PartidaController {
    
    private final PartidaService partidaService;
    
    public PartidaController() {
        this.partidaService = new PartidaService();
    }
    
    /**
     * Crea y guarda una nueva partida en la base de datos
     * 
     * @param partida Objeto Partida con los datos a guardar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean crearPartida(Partida partida) {
        try {
            partidaService.guardarPartida(partida);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación al crear partida: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error al crear partida: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene todas las partidas disponibles
     * 
     * @return Lista de todas las partidas
     */
    public List<Partida> listarPartidas() {
        try {
            return partidaService.obtenerPartidas();
        } catch (Exception e) {
            System.err.println("Error al listar partidas: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene una partida específica por su ID
     * 
     * @param idPartida Identificador de la partida
     * @return Objeto Partida si existe, null en caso contrario
     */
    public Partida obtenerPartida(int idPartida) {
        try {
            if (idPartida <= 0) {
                throw new IllegalArgumentException("El ID de la partida debe ser válido");
            }
            return partidaService.obtenerPartidaPorId(idPartida);
        } catch (Exception e) {
            System.err.println("Error al obtener partida: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Actualiza una partida existente
     * 
     * @param partida Objeto Partida con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean modificarPartida(Partida partida) {
        try {
            partidaService.actualizarPartida(partida);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación al actualizar partida: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error al actualizar partida: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina una partida por su ID
     * 
     * @param idPartida Identificador de la partida a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean eliminarPartida(int idPartida) {
        try {
            if (idPartida <= 0) {
                throw new IllegalArgumentException("El ID de la partida debe ser válido");
            }
            partidaService.eliminarPartida(idPartida);
            return true;
        } catch (Exception e) {
            System.err.println("Error al eliminar partida: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Marca una partida como mayorizada
     * 
     * @param partida Objeto Partida a mayorizar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean mayorizarPartida(Partida partida) {
        try {
            partidaService.marcarComoMayorizada(partida);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación al mayorizar partida: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error al mayorizar partida: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Calcula el total del debe de una partida
     * 
     * @param partida Objeto Partida
     * @return BigDecimal con el total del debe
     */
    public BigDecimal calcularTotalDebe(Partida partida) {
        try {
            if (partida == null) {
                return BigDecimal.ZERO;
            }
            return partidaService.calcularTotalDebe(partida);
        } catch (Exception e) {
            System.err.println("Error al calcular total debe: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcula el total del haber de una partida
     * 
     * @param partida Objeto Partida
     * @return BigDecimal con el total del haber
     */
    public BigDecimal calcularTotalHaber(Partida partida) {
        try {
            if (partida == null) {
                return BigDecimal.ZERO;
            }
            return partidaService.calcularTotalHaber(partida);
        } catch (Exception e) {
            System.err.println("Error al calcular total haber: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcula la diferencia entre debe y haber
     * 
     * @param partida Objeto Partida
     * @return BigDecimal con la diferencia
     */
    public BigDecimal calcularDiferencia(Partida partida) {
        try {
            if (partida == null) {
                return BigDecimal.ZERO;
            }
            return partidaService.calcularDiferencia(partida);
        } catch (Exception e) {
            System.err.println("Error al calcular diferencia: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Verifica si una partida está balanceada
     * 
     * @param partida Objeto Partida
     * @return true si la partida está balanceada, false en caso contrario
     */
    public boolean estaBalanceada(Partida partida) {
        try {
            if (partida == null) {
                return false;
            }
            return partidaService.estaBalanceada(partida);
        } catch (Exception e) {
            System.err.println("Error al verificar balance de partida: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cierra la conexión con la base de datos
     */
    public void cerrarConexion() {
        try {
            partidaService.cerrarConexion();
        } catch (Exception e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}

