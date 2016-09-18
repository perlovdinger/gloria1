package com.volvo.gloria.financeProxy.c;

import java.io.Serializable;

public class OrderSapAccountsDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4281626547762768994L;
    
    private String sequence;
    private String generalLedgerAccount;
    private String costCenter;
    private String wbsElement;
    
   
    public String getSequence() {
        return sequence;
    }
    public void setSequence(String sequence) {
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

}
