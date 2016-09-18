package com.volvo.gloria.procurerequest.c.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for RequestGateway.
 */
public class RequestGatewayDTO implements Serializable {

    private static final long serialVersionUID = -8455139033744817796L;

    private boolean isCancelRequest;
    private String senderLogicalId;
    private ChangeIdTransformerDTO changeIdTransformerDto = new ChangeIdTransformerDTO();
    private List<RequestHeaderTransformerDTO> requestHeaderTransformerDtos;

    public ChangeIdTransformerDTO getChangeIdTransformerDto() {
        return changeIdTransformerDto;
    }

    public void setChangeIdTransformerDto(ChangeIdTransformerDTO changeIdTransformerDto) {
        this.changeIdTransformerDto = changeIdTransformerDto;
    }

    public List<RequestHeaderTransformerDTO> getRequestHeaderTransformerDtos() {
        return requestHeaderTransformerDtos;
    }
    
    public void setRequestHeaderTransformerDtos(List<RequestHeaderTransformerDTO> requestHeaderTransformerDtos) {
        this.requestHeaderTransformerDtos = requestHeaderTransformerDtos;
    }

    public boolean isCancelRequest() {
        return isCancelRequest;
    }

    public void setCancelRequest(boolean isCancelRequest) {
        this.isCancelRequest = isCancelRequest;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        if (this.changeIdTransformerDto.getChangeTechId().equalsIgnoreCase(((RequestGatewayDTO) obj).getChangeIdTransformerDto().getChangeTechId())) {
            return true;
        }
        return false;
    }

    // Base hashCode on changeTechId
    @Override
    public int hashCode() {
        return this.changeIdTransformerDto.getChangeTechId().hashCode();
    }

    public String getSenderLogicalId() {
        return senderLogicalId;
    }

    public void setSenderLogicalId(String senderLogicalId) {
        this.senderLogicalId = senderLogicalId;
    }

}
