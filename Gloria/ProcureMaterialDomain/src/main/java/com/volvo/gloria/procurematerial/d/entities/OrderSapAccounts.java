package com.volvo.gloria.procurematerial.d.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entity Class for ORDER_SAP_ACCOUNTS.
 */
@Entity
@Table(name = "ORDER_SAP_ACCOUNTS")
public class OrderSapAccounts implements Serializable {

    private static final long serialVersionUID = 2262762836818792475L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_SAP_ACCOUNTS_OID")
    private long orderSapAccountsOID;
    
    @Version
    private long version;
    
    @ManyToOne
    @JoinColumn(name = "ORDER_SAP_LINE_OID")
    private OrderSapLine orderSapLine;
    
    private long sequence;
    private String generalLedgerAccount;
    private String costCenter;
    private String wbsElement;
    
    public long getOrderSapAccountsOID() {
        return orderSapAccountsOID;
    }
    public void setOrderSapAccountsOID(long orderSapAccountsOID) {
        this.orderSapAccountsOID = orderSapAccountsOID;
    }
    public OrderSapLine getOrderSapLine() {
        return orderSapLine;
    }
    public void setOrderSapLine(OrderSapLine orderSapLine) {
        this.orderSapLine = orderSapLine;
    }
    public long getSequence() {
        return sequence;
    }
    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
    public String getGeneralLedgerAccount() {
        return generalLedgerAccount;
    }
    public void setGeneralLedgerAccount(String generalLedgerAccount) {
        this.generalLedgerAccount = generalLedgerAccount;
    }
    public String getCostCenter() {
        return costCenter;
    }
    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }
    public String getWbsElement() {
        return wbsElement;
    }
    public void setWbsElement(String wbsElement) {
        this.wbsElement = wbsElement;
    }
    public long getVersion() {
        return version;
    }
    
}
