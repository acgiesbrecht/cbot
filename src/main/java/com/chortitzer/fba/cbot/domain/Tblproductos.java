/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chortitzer.fba.cbot.domain;

import java.io.Serializable;
import java.util.Collection;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author adriang
 */
@Entity
@Table(name = "tblproductos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tblproductos.findAll", query = "SELECT t FROM Tblproductos t")
    ,
    @NamedQuery(name = "Tblproductos.findById", query = "SELECT t FROM Tblproductos t WHERE t.id = :id")
    ,
    @NamedQuery(name = "Tblproductos.findByDescripcion", query = "SELECT t FROM Tblproductos t WHERE t.descripcion = :descripcion")
    ,
    @NamedQuery(name = "Tblproductos.findByMateriaprima", query = "SELECT t FROM Tblproductos t WHERE t.materiaprima = :materiaprima")})
public class Tblproductos implements Serializable {

    private static final long serialVersionUID = 1L;

    private final IntegerProperty id = new SimpleIntegerProperty();

    private final StringProperty descripcion = new SimpleStringProperty();

    private final IntegerProperty materiaprima = new SimpleIntegerProperty();

    private Collection<TblBasPrecios> tblBasPreciosCollection;

    public Tblproductos() {
    }

    /*
    public Tblproductos(Integer id) {
        this.id = id;
    }

    public Tblproductos(Integer id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    @Basic(optional = false)
    @Column(name = "descripcion")
    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    @Column(name = "materiaprima")
    public Integer getMateriaprima() {
        return materiaprima.get();
    }

    public void setMateriaprima(Integer materiaprima) {
        this.materiaprima.set(materiaprima == null ? 0 : materiaprima);
    }

    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idProducto")
    public Collection<TblBasPrecios> getTblBasPreciosCollection() {
        return tblBasPreciosCollection;
    }

    public void setTblBasPreciosCollection(Collection<TblBasPrecios> tblBasPreciosCollection) {
        this.tblBasPreciosCollection = tblBasPreciosCollection;
    }

    /*
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tblproductos)) {
            return false;
        }
        Tblproductos other = (Tblproductos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
     */
    @Override
    public String toString() {
        return descripcion.get();
    }

}
