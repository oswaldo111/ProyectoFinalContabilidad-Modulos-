package com.sistema.modulos.contabilidad.Controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.sistema.modulos.contabilidad.Models.DetallePartida;
import com.sistema.modulos.contabilidad.Services.DetallePartidaDAOService;

public class DetallePartidaController {
    
    private final DetallePartidaDAOService detallePartidaService;
    
    public DetallePartidaController() {
        this.detallePartidaService = new DetallePartidaDAOService();
    }
    
    /**
     * Crea un nuevo objeto DetallePartida vacío
     * 
     * @return Objeto DetallePartida inicializado
     */
    public DetallePartida crearNuevoDetalle() {
        try {
            return detallePartidaService.crearDetallePartida();
        } catch (Exception e) {
            System.err.println("Error al crear nuevo detalle: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Guarda un detalle de partida en la base de datos
     * 
     * @param detallePartida Objeto DetallePartida a guardar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean guardarDetallePartida(DetallePartida detallePartida) {
        try {
            validarDetallePartida(detallePartida);
            detallePartidaService.guardarDetallePartida(detallePartida);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación al guardar detalle: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error al guardar detalle de partida: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene todos los detalles de partida registrados
     * 
     * @return Lista de DetallePartida, vacía si no hay registros
     */
    public List<DetallePartida> listarDetallesPartida() {
        try {
            List<DetallePartida> detalles = detallePartidaService.obtenerDetallesPartida();
            return detalles != null ? detalles : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error al listar detalles de partida: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Actualiza un detalle de partida existente
     * 
     * @param detallePartida Objeto DetallePartida con datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public boolean actualizarDetallePartida(DetallePartida detallePartida) {
        try {
            validarDetallePartida(detallePartida);
            
            if (detallePartida.getIdDetalle() <= 0) {
                throw new IllegalArgumentException(
                    "El detalle debe tener un identificador válido para actualizarse"
                );
            }
            
            detallePartidaService.actualizarDetallePartida(detallePartida);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación al actualizar detalle: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error al actualizar detalle de partida: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Valida que un detalle de partida cumpla con los requisitos mínimos
     * 
     * @param detallePartida Objeto a validar
     * @throws IllegalArgumentException si la validación falla
     */
    private void validarDetallePartida(DetallePartida detallePartida) {
        if (detallePartida == null) {
            throw new IllegalArgumentException("El detalle de partida no puede ser nulo");
        }
        
        if (detallePartida.getIdCuenta() == null || 
            detallePartida.getIdCuenta().getIdCuenta() <= 0) {
            throw new IllegalArgumentException(
                "El detalle debe estar asociado a una cuenta contable válida"
            );
        }
        
        BigDecimal debe = detallePartida.getDebe();
        BigDecimal haber = detallePartida.getHaber();
        
        if (debe == null && haber == null) {
            throw new IllegalArgumentException(
                "El detalle debe tener al menos un valor en debe o haber"
            );
        }
        
        if (debe != null && debe.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El debe no puede ser negativo");
        }
        
        if (haber != null && haber.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El haber no puede ser negativo");
        }
        
        boolean tieneDebe = debe != null && debe.compareTo(BigDecimal.ZERO) > 0;
        boolean tieneHaber = haber != null && haber.compareTo(BigDecimal.ZERO) > 0;
        
        if (tieneDebe && tieneHaber) {
            throw new IllegalArgumentException(
                "Un detalle no puede tener valores tanto en debe como en haber"
            );
        }
        
        if (!tieneDebe && !tieneHaber) {
            throw new IllegalArgumentException(
                "Un detalle debe registrar al menos un valor en debe o haber"
            );
        }
    }
    
    /**
     * Verifica si un detalle tiene un monto válido en debe
     * 
     * @param detallePartida Objeto a verificar
     * @return true si tiene un monto válido en debe
     */
    public boolean tieneDebe(DetallePartida detallePartida) {
        try {
            if (detallePartida == null || detallePartida.getDebe() == null) {
                return false;
            }
            return detallePartida.getDebe().compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            System.err.println("Error al verificar debe del detalle: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un detalle tiene un monto válido en haber
     * 
     * @param detallePartida Objeto a verificar
     * @return true si tiene un monto válido en haber
     */
    public boolean tieneHaber(DetallePartida detallePartida) {
        try {
            if (detallePartida == null || detallePartida.getHaber() == null) {
                return false;
            }
            return detallePartida.getHaber().compareTo(BigDecimal.ZERO) > 0;
        } catch (Exception e) {
            System.err.println("Error al verificar haber del detalle: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene el monto total del detalle (ya sea debe o haber)
     * 
     * @param detallePartida Objeto del cual obtener el monto
     * @return BigDecimal con el monto total
     */
    public BigDecimal obtenerMonto(DetallePartida detallePartida) {
        try {
            if (detallePartida == null) {
                return BigDecimal.ZERO;
            }
            
            if (tieneDebe(detallePartida)) {
                return detallePartida.getDebe();
            } else if (tieneHaber(detallePartida)) {
                return detallePartida.getHaber();
            }
            
            return BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Error al obtener monto del detalle: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
