package com.volvo.gloria.financeProxy.c;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoodsReceiptHeaderTransformerDTO implements Serializable {

    private static final long serialVersionUID = -1209915910239760147L;

    private String companyCode;
    private Date postingDateTime;
    private Date documentDate;
    private String referenceDocument;
    private String headerText;
    private String assignCodeGM;

    private List<GoodsReceiptLineTransformerDTO> goodsReceiptLineTransformerDTOs = new ArrayList<GoodsReceiptLineTransformerDTO>();

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public Date getPostingDateTime() {
        return postingDateTime;
    }

    public void setPostingDateTime(Date postingDateTime) {
        this.postingDateTime = postingDateTime;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public String getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(String referenceDocument) {
        this.referenceDocument = referenceDocument;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getAssignCodeGM() {
        return assignCodeGM;
    }

    public void setAssignCodeGM(String assignCodeGM) {
        this.assignCodeGM = assignCodeGM;
    }

    public List<GoodsReceiptLineTransformerDTO> getGoodsReceiptLineTransformerDTOs() {
        return goodsReceiptLineTransformerDTOs;
    }
    
    public void setGoodsReceiptLineTransformerDTOs(List<GoodsReceiptLineTransformerDTO> goodsReceiptLineTransformerDTOs) {
        this.goodsReceiptLineTransformerDTOs = goodsReceiptLineTransformerDTOs;
    }
}
