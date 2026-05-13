package com.sistema.modulos.contabilidad.Models;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Integer idCuenta;

    @Column(name = "id_empresa", nullable = false)
    private Integer idEmpresa;

    @Column(name = "codigo_cuenta", nullable = false, length = 20)
    private String codigoCuenta;

    @Column(name = "nombre_cuenta", nullable = false, length = 100)
    private String nombreCuenta;

    @Column(name = "tipo_cuenta", nullable = false, length = 50)
    private String tipoCuenta;

    // RELACIÓN JERÁRQUICA: Una cuenta tiene un padre
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta_padre")
    private Cuenta cuentaPadre;

    // RELACIÓN INVERSA: Una cuenta padre tiene muchas sub-cuentas (opcional, útil
    // para árboles)
    @OneToMany(mappedBy = "cuentaPadre", cascade = CascadeType.ALL)
    private List<Cuenta> subCuentas;

    public Cuenta() {
    }

    // Getters y Setters
    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    public void setCodigoCuenta(String codigoCuenta) {
        this.codigoCuenta = codigoCuenta;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public Cuenta getCuentaPadre() {
        return cuentaPadre;
    }

    public void setCuentaPadre(Cuenta cuentaPadre) {
        this.cuentaPadre = cuentaPadre;
    }

    // Práctica Senior: Sobrescribir toString para facilitar el llenado de
    // ComboBoxes y JTrees en Swing
    @Override
    public String toString() {
        return this.codigoCuenta + " - " + this.nombreCuenta;
    }

    // Equals y HashCode para poder comparar objetos Cuenta fácilmente
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Cuenta cuenta = (Cuenta) o;
        return idCuenta == cuenta.idCuenta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCuenta);
    }
}