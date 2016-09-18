package com.volvo.gloria.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * build json string.
 * 
 */
public class ObjectJSON {

    private Map<String, String> items = new HashMap<String, String>();

    public ObjectJSON() {

    }

    public void addItem(String key, String value) {
        this.items.put(key, value);
    }

    public String jsonValue() {
        return build(false);
    }

    public String jsonValueWithHexaDecimal() {
        return build(true);
    }

    private String build(boolean handleNonAsciiChars) {
        if (!this.items.isEmpty()) {
            Set<String> keys = this.items.keySet();
            String jsonStr = "";
            for (String key : keys) {
                if (this.items.get(key) != null) {
                    if (!StringUtils.isEmpty(jsonStr)) {
                        jsonStr += ",";
                    }
                    jsonStr += encloseWithDoubleQuote(key, handleNonAsciiChars).concat(":").concat(encloseWithDoubleQuote(this.items.get(key),
                                                                                                                          handleNonAsciiChars));
                }
            }
            return "{" + jsonStr + "}";
        }
        return "";
    }

    private String encloseWithDoubleQuote(String value, boolean handleNonAsciiChars) {
        if (handleNonAsciiChars) {
            return "\"".concat(GloriaFormateUtil.hexaDecimalValue(value)).concat("\"");
        }
        return "\"".concat(value).concat("\"");
    }
}
