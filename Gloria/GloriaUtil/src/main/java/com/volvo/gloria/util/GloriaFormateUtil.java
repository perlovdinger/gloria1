package com.volvo.gloria.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
/**
 * Utility methods.
 */
public abstract class GloriaFormateUtil {
    
    private static final int ASCII_32 = 32;
    private static final int ASCII_64 = 64;
    private static final int ASCII_91 = 91;
    private static final int ASCII_96 = 96;
    private static final int ASCII_123 = 123;

    private GloriaFormateUtil() {
        
    }
    /**
     * Utility to convert a comma separated string value as List<Long>.
     * 
     * @param valuesAsStr
     * @return
     */
    public static List<Long> getValuesAsLong(String valuesAsStr) {
        List<Long> longValues = new ArrayList<Long>();
        if (valuesAsStr != null && valuesAsStr.length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(valuesAsStr, ",");
            while (tokenizer.hasMoreTokens()) {
                longValues.add(Long.parseLong(tokenizer.nextToken()));
            }
        }
        return longValues;
    }

    /**
     * Utility to convert a comma separated string value as List<Long>.
     * 
     * @param valuesAsStr
     * @return
     */
    public static List<String> getValuesAsString(String valuesAsStr) {
        List<String> stringValues = new ArrayList<String>();
        if (valuesAsStr != null && valuesAsStr.length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(valuesAsStr, ",");
            while (tokenizer.hasMoreTokens()) {
                stringValues.add(tokenizer.nextToken());
            }
        }
        return stringValues;
    }

    /**
     * Utility to convert a comma separated string value as List<enum>.
     * 
     * @param valuesAsStr
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List getValuesAsEnums(String valuesAsStr, Class enumClass) {
        List enums = new ArrayList();
        if (valuesAsStr != null && valuesAsStr.length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(valuesAsStr, ",");
            while (tokenizer.hasMoreTokens()) {
                enums.add(Enum.valueOf(enumClass, tokenizer.nextToken().trim()));
            }
        }
        return enums;
    }
    
    /**
     * Utility to convert a comma separated string value as List<Double>.
     * 
     * @param valuesAsStr
     * @return
     */
    public static Object getValuesAsDouble(String valuesAsStr) {
        List<Double> doubleValues = new ArrayList<Double>();
        if (valuesAsStr != null && valuesAsStr.length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(valuesAsStr, ",");
            while (tokenizer.hasMoreTokens()) {
                doubleValues.add(Double.parseDouble(tokenizer.nextToken()));
            }
        }
        return doubleValues;
    }
    
    /**
     * truncate strings.
     * 
     * @param valueStr
     * @param length
     * @return
     */
    public static String truncate(String valueStr, int length) {
        if (!StringUtils.isEmpty(valueStr)) {
            if (valueStr.length() > length) {
                return valueStr.substring(0, length - 1);
            }
            return valueStr;
        }
        return "";
    }
    
    /**
     * Compare the items by matching the predicate.
     * 
     * @param inputCollection
     * @param predicate
     * @return
     */
    public static <E> boolean hasSameItems(Collection<E> inputCollection, Predicate predicate) {
        if (inputCollection != null && predicate != null) {            
            for (Iterator<E> iter = inputCollection.iterator(); iter.hasNext();) {
                E item = iter.next();                
                if (!predicate.evaluate(item)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Convert non-ascii characters to Hexa-Decimal value.
     * 
     * @param value
     * @return
     * @throws GloriaApplicationException
     */
    public static String hexaDecimalValue(String value) {
        if (!StringUtils.isEmpty(value)) {
            StringBuilder hexaDecimalValues = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                char charAtIndex = value.charAt(i);
                if (isUniCode(charAtIndex)) {
                    hexaDecimalValues.append("_".concat(String.format("%02x", (int) charAtIndex)));
                    continue;
                }
                hexaDecimalValues.append(charAtIndex);
            }
            return hexaDecimalValues.toString();
        }
        return value;
    }

    private static boolean isUniCode(char c) {
        return !((c > ASCII_64 && c < ASCII_91) || (c > ASCII_96 && c < ASCII_123) || c == ASCII_32);
    } 
}
