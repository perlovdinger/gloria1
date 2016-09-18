package com.volvo.gloria.common.c;

/**
 * Different actions for cost center sync.
 */
public enum CarryOverActionType {
    ADD("Add"), CHANGE("Change"), DELETE("Delete"), LOAD("Load");

    private final String value;

    CarryOverActionType(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    public static CarryOverActionType fromValue(String value) {
        if (value != null) {
            for (CarryOverActionType type : CarryOverActionType.values()) {
              if (value.equalsIgnoreCase(type.value)) {
                return type;
              }
            }
          }
          return null;
    }
}
