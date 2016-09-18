package com.volvo.gloria.warehouse.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PRINTER")
public class Printer implements Serializable {

    private static final long serialVersionUID = 6049053969442233494L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRINTER_OID")
    private long printerOid;

    @ManyToOne
    @JoinColumn(name = "WAREHOUSE_OID")
    private Warehouse warehouse;

    private String name;
    private String hostAddress;

    public long getPrinterOid() {
        return printerOid;
    }

    public void setPrinterOid(long printerOid) {
        this.printerOid = printerOid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

}
