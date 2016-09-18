package com.volvo.gloria.common.c;

/**
 * Different actions for cost center sync.
 */
public enum CostCenterActionType {
    CREATE("Create"), UPDATE("Update"), DELETE("Delete"), LOAD("Load");

    private final String value;

    CostCenterActionType(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    public static CostCenterActionType fromValue(String value) {
        if (value != null) {
            for (CostCenterActionType type : CostCenterActionType.values()) {
              if (value.equalsIgnoreCase(type.value)) {
                return type;
              }
            }
          }
          return null;
    }
}
