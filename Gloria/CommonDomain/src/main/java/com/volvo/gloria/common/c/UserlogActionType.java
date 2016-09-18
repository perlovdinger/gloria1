package com.volvo.gloria.common.c;

/**
 * possible actions for user log.
 * 
 */
public enum UserlogActionType {

    SCRAPPART("SCRAP PART"), RELEASEPART("RELEASE PART"), ADJUSTINVENTORYQUANTITY("Adjust inventory quantity");

    private String actionName;

    UserlogActionType(String actionName) {
        this.actionName = actionName;
    }

    public String actionName() {
        return actionName;
    }

    @Override
    public String toString() {
        return actionName;
    }
}
