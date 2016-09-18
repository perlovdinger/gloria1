package com.volvo.gloria.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.volvo.jvs.runtime.platform.ApplicationException;

/**
 * Gloria application exception, uses rollback=true transaction declaration.
 */
@javax.ejb.ApplicationException(rollback = true)
public class GloriaApplicationException extends ApplicationException {
    private static final long serialVersionUID = 1281324350687493524L;
    private String dtoName;
    private String[] errorData;
    private Map<Long, String> validationData = new HashMap<Long, String>();
    private String errorMessage;

    public GloriaApplicationException(String errorCode, String errorMessage) {
        super(errorMessage);
        setValidations(errorCode, errorMessage);
    }

    public GloriaApplicationException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
        setValidations(errorCode, errorMessage);
    }

    public GloriaApplicationException(Long id, String attribute, String errorCode, String errorMessage, String dtoName) {
        super(errorMessage);
        setValidations(id, attribute, errorCode, errorMessage, dtoName, null);
    }

    public GloriaApplicationException(String attribute, String errorCode, String errorMessage, Map<String, Object> msgParam) {
        super(errorMessage);
        setValidations(null, attribute, errorCode, errorMessage, "", msgParam);
    }
    
    public final void setValidations(Long id, String attribute, String errorCode, String errorMessage, String dtoName, Map<String, Object> msgParamMap) {
        String validation = null;
        String msgParam = getParamMap(msgParamMap);
        this.errorMessage = errorMessage;
        if (validationData.containsKey(id)) {
            validation = validationData.get(id) + ",{\"id\" :\"" + id + "\",\"attribute\" : \"" + attribute + "\",\"errorCode\" : \"" + errorCode
                    + "\",\"errorCodeParam\" : " +  msgParam + ",\"errorMessage\" : \"" + errorMessage + "\"}";
        } else {
            setDtoName(dtoName);
            validation = "{\"id\" :\"" + id + "\",\"attribute\" : \"" + attribute + "\",\"errorCode\" : \"" + errorCode +  "\",\"errorCodeParam\" : " +  
                    msgParam + ",\"errorMessage\" : \"" + errorMessage + "\"}";
        }
        validationData.put(id, validation);
    }

    private String getParamMap(Map<String, Object> msgParamMap) {
        if (msgParamMap == null || msgParamMap.isEmpty()) {
            return null;
        }
        Set<String> keySet = msgParamMap.keySet();
        String msgParam = "{";
        for (String param : keySet) {
            msgParam += "\"" + param + "\":\"" + msgParamMap.get(param) + "\", ";
        }
        
        msgParam = msgParam.substring(0, msgParam.length() - 2) + "}";
        return msgParam;
    }

    public void setValidations(String errorCode, String errorMessage) {
        setValidations(null, "", errorCode, errorMessage, "", null);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String[] getArgs() {
        return errorData;
    }

    public void setArgs(String[] data) {
        if (data != null) {
            this.errorData = Arrays.copyOf(data, data.length);
        }
    }

    public Map<Long, String> getValidationData() {
        return validationData;
    }

    public void setValidationData(Map<Long, String> validationData) {
        this.validationData = validationData;
    }

    public void addValidationData(Map<Long, String> validationDataToAdd) {
        validationData.putAll(validationDataToAdd);
    }

    public String getDtoName() {
        return dtoName;
    }

    public void setDtoName(String dtoName) {
        this.dtoName = dtoName;
    }

    @Override
    public String toString() {
        Map<Long, String> validation = getValidationData();
        Iterator<Long> itv = validation.keySet().iterator();
        String tempMsg = "";
        String finalMsg = null;
        while (itv.hasNext()) {
            Long key = itv.next();
            String msg = (String) validation.get(key);
            tempMsg = tempMsg + "{\"dtoName\" : \"" + getDtoName() + "\",\"dtoId\" : " + key + ",\"errors\" : [" + msg + "]},";
        }
        if (!tempMsg.isEmpty()) {
            finalMsg = "{\"validationErrors\" : [" + tempMsg.substring(0, tempMsg.length() - 1) + "]}";
        }
        return finalMsg;
    }

    public boolean hasViolations() {
        if (!getValidationData().isEmpty()) {
            return true;
        }
        return false;
    }
}
