package com.sistema.modulos.contabilidad.Models;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "partidas")
public class Partida {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_partida")
    private Integer idPartida;

    @Column(name = "id_empresa", nullable = false)
    private Integer idEmpresa;

    @Column(name = "numero_partida", nullable = false)
    private Integer numeroPartida;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha")
    private Date fecha;

    @Column(name = "descripcion_general", columnDefinition = "TEXT")
    private String descripcionGeneral;

    @Column(name = "estado", length = 15)
    private String estado; // 'BORRADOR' o 'MAYORIZADA'

    // MAESTRO-DETALLE: Una partida contiene muchos detalles
    // orphanRemoval = true asegura que si borras un detalle de la lista, se borre de la BD
    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePartida> detalles = new ArrayList<>();

    public Partida() {}

    public Partida(int idPartida, int idEmpresa, int numeroPartida, Date fecha, String descripcionGeneral, String estado) {
        this.idPartida = idPartida;
        this.idEmpresa = idEmpresa;
        this.numeroPartida = numeroPartida;
        this.fecha = fecha;
        this.descripcionGeneral = descripcionGeneral;
        this.estado = estado;
        this.detalles = new ArrayList<>();
    }

    // Getters y Setters
    public int getIdPartida() { return idPartida; }
    public void setIdPartida(int idPartida) { this.idPartida = idPartida; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public int getNumeroPartida() { return numeroPartida; }
    public void setNumeroPartida(int numeroPartida) { this.numeroPartida = numeroPartida; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getDescripcionGeneral() { return descripcionGeneral; }
    public void setDescripcionGeneral(String descripcionGeneral) { this.descripcionGeneral = descripcionGeneral; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Manejo de la lista de detalles
    public List<DetallePartida> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePartida> detalles) { this.detalles = detalles; }

    public void addDetalle(DetallePartida detalle) {
        detalles.add(detalle);
        detalle.setIdPartida(this);
    }
}