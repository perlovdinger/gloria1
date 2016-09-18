package com.volvo.gloria.common.util;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.ConversionUnit;

/**
 * unit conversion utility class.
 * 
 */
public class UnitConverter {

    protected UnitConverter() {

    }

    public static String convert(String applicationFrom, String applicationTo, String codeToConvert, CommonServices commonServices) {
        ConversionUnit conversionUnit = commonServices.findConversionUnit(applicationFrom, applicationTo, codeToConvert);
        if (conversionUnit != null) {
            return conversionUnit.getCodeTo();
        }
        return null;
    }

    public static long convert(String applicationFrom, String applicationTo, String codeToConvert, long quantity, CommonServices commonServices) {
        ConversionUnit conversionUnit = commonServices.findConversionUnit(applicationFrom, applicationTo, codeToConvert);
        if (conversionUnit != null) {
            return quantity / conversionUnit.getDividedBy();
        }
        return 0;
    }
}
