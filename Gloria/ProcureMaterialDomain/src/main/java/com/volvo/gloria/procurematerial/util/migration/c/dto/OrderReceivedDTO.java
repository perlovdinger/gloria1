package com.volvo.gloria.procurematerial.util.migration.c.dto;

import java.util.Date;

public class OrderReceivedDTO {
    private static final String SEMICOLON = ";";
    private static final String COMMA = ",";
    private String itemId;
    private String lineType;
    private Long receivedQuantity;
    private String receivalDateOriginal;
    private Date receivalDate;
    private String orderLinePaid;
    private String receivedQuantityorginal;
    private String packingSlipNumber;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public Long getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(Long receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public String getReceivalDateOriginal() {
        return receivalDateOriginal;
    }

    public void setReceivalDateOriginal(String receivalDateOriginal) {
        this.receivalDateOriginal = receivalDateOriginal;
    }

    public Date getReceivalDate() {
        return receivalDate;
    }

    public void setReceivalDate(Date receivalDate) {
        this.receivalDate = receivalDate;
    }

    public String getOrderLinePaid() {
        return orderLinePaid;
    }

    public void setOrderLinePaid(String orderLinePaid) {
        this.orderLinePaid = orderLinePaid;
    }

    public String getReceivedQuantityorginal() {
        return receivedQuantityorginal;
    }

    public void setReceivedQuantityorginal(String receivedQuantityorginal) {
        this.receivedQuantityorginal = receivedQuantityorginal;
    }

    public String getPackingSlipNumber() {
        return packingSlipNumber;
    }

    public void setPackingSlipNumber(String packingSlipNumber) {
        this.packingSlipNumber = packingSlipNumber;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(returnEmptyStringForNull(itemId));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(lineType));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(receivedQuantity));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(receivalDateOriginal));
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append("");
        stringBuilder.append(SEMICOLON);
        stringBuilder.append(returnEmptyStringForNull(packingSlipNumber));

        return stringBuilder.toString();
    }

    private String returnEmptyStringForNull(Object obj) {
        return obj == null ? "" : obj.toString().replace(SEMICOLON, "SEMICOLON").replace(COMMA, "COMMA");
    }
}
