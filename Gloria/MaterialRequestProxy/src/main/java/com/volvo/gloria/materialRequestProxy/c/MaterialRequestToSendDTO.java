package com.volvo.gloria.materialRequestProxy.c;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MaterialRequestToSendDTO implements Serializable {

    private static final long serialVersionUID = 8270803523026204257L;
    
    private String materialRequestId;

    private String changeTechId;

    private String mtrlRequestVersion;
    
    private String requestType;

    private List<MaterialRequestTransformerDTO> materialRequestTransformerDtos;
    
    private boolean isCancelRequest;
    
    private List<String> changeTechIds = new ArrayList<String>();

    public String getChangeTechId() {
        return changeTechId;
    }

    public void setChangeTechId(String changeTechId) {
        this.changeTechId = changeTechId;
    }

    public List<MaterialRequestTransformerDTO> getMaterialRequestTransformerDtos() {
        return materialRequestTransformerDtos;
    }

    public void setMaterialRequestTransformerDtos(List<MaterialRequestTransformerDTO> materialRequestTransformerDtos) {
        this.materialRequestTransformerDtos = materialRequestTransformerDtos;
    }

    public String getMtrlRequestVersion() {
        return mtrlRequestVersion;
    }

    public void setMtrlRequestVersion(String mtrlRequestVersion) {
        this.mtrlRequestVersion = mtrlRequestVersion;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getMaterialRequestId() {
        return materialRequestId;
    }

    public void setMaterialRequestId(String materialRequestId) {
        this.materialRequestId = materialRequestId;
    }

    public boolean isCancelRequest() {
        return isCancelRequest;
    }

    public void setCancelRequest(boolean isCancelRequest) {
        this.isCancelRequest = isCancelRequest;
    }

    public List<String> getChangeTechIds() {
        return changeTechIds;
    }

    public void setChangeTechIds(List<String> changeTechIds) {
        this.changeTechIds = changeTechIds;
    }

}
