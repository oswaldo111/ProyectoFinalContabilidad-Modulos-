package com.sistema.modulos.contabilidad.Models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_partida")
public class DetallePartida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    // Relación con el encabezado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partida", nullable = false)
    private Partida partida;

    // Relación con la cuenta contable
    @ManyToOne(fetch = FetchType.EAGER) // Traer la cuenta siempre para ver su nombre
    @JoinColumn(name = "id_cuenta", nullable = false)
    private Cuenta cuenta;

    @Column(name = "debe", precision = 15, scale = 2)
    private BigDecimal debe = BigDecimal.ZERO;

    @Column(name = "haber", precision = 15, scale = 2)
    private BigDecimal haber = BigDecimal.ZERO;

    

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public Partida getIdPartida() { return partida; }
    public void setIdPartida(Partida idPartida) { this.partida = idPartida; }

    public Cuenta getIdCuenta() { return cuenta; }
    public void setIdCuenta(Cuenta idCuenta) { this.cuenta = idCuenta; }

    public BigDecimal getDebe() { return debe; }
    public void setDebe(BigDecimal debe) { this.debe = debe; }

    public BigDecimal getHaber() { return haber; }
    public void setHaber(BigDecimal haber) { this.haber = haber; }
}
