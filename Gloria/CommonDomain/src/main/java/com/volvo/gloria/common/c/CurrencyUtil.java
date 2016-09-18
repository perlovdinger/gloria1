package com.volvo.gloria.common.c;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.CurrencyRate;

/**
 * handling currency conversion.
 * 
 */
public final class CurrencyUtil {

    private static final String CURRENCY_SEK = "SEK";
    private static final String CURRENCY_EUR = "EUR";

    private CurrencyUtil() {
    }

    public static double convertUnitPriceToEUR(double unitPrice, String currencyCode, CommonServices commonServices) {
        if (!StringUtils.isEmpty(currencyCode)) {
            return convert(unitPrice, currencyCode, commonServices);
        }
        return 0;
    }

    private static double convert(double unitPrice, String currencyCode, CommonServices commonServices) {
        double currentunitPrice = unitPrice;
        if (isCurrencyToBeConvertedNotInEuro(currencyCode)) {
            if (isCurrencyToBeConvertedNotInSEK(currencyCode)) {
                currentunitPrice = convertToSEK(currentunitPrice, currencyCode, commonServices);
            }
            currentunitPrice = convertToEURO(currentunitPrice, currencyCode, commonServices);
        }
        return currentunitPrice;
    }

    private static double convertToSEK(double unitPrice, String currencyCode, CommonServices commonServices) {
        CurrencyRate currencyRate = commonServices.getValidCurrencyRate(currencyCode);
        if (currencyRate != null) {
            return roundOff(unitPrice * currencyRate.getRate(), 2);
        }
        return 0;
    }

    private static double convertToEURO(double unitPrice, String currencyCode, CommonServices commonServices) {
        CurrencyRate currencyRate = commonServices.getValidCurrencyRate(CURRENCY_EUR);
        if (currencyRate != null) {
            return roundOff(unitPrice / currencyRate.getRate(), 2);
        }
        return 0;
    }

    private static boolean isCurrencyToBeConvertedNotInEuro(String currencyCode) {
        return !currencyCode.equals(CURRENCY_EUR);
    }

    private static boolean isCurrencyToBeConvertedNotInSEK(String currencyCode) {
        return !currencyCode.equals(CURRENCY_SEK);
    }

    public static double roundOff(Double value, int scale) {
        if (value != null && value != 0) {
            return new BigDecimal(value).setScale(scale, RoundingMode.FLOOR).doubleValue();
        } else {
            return 0;
        }
    }
    
    public static double convertCurrencyFromActualToDefault(double unitPrice, String actualCurrencyCode, String defaultCurrencyCode,
            CommonServices commonServices) {
        double currentunitPrice = unitPrice;
        if (unitPrice != 0) {
            String baseCurrencyCode = getBaseCurrencyCode(actualCurrencyCode, commonServices);
            if (!StringUtils.isEmpty(actualCurrencyCode) && !StringUtils.isEmpty(defaultCurrencyCode) && !actualCurrencyCode.equals(defaultCurrencyCode)) {
                if (StringUtils.isEmpty(baseCurrencyCode)) {
                    baseCurrencyCode = actualCurrencyCode;
                }
                if (!actualCurrencyCode.equals(baseCurrencyCode)) {
                    double convUnitPrice = convertActualUnitPriceToBaseCurrency(currentunitPrice, actualCurrencyCode, commonServices);
                    if (convUnitPrice != 0) {
                        currentunitPrice = convUnitPrice;
                    }
                }
                if (!defaultCurrencyCode.equals(baseCurrencyCode)) {
                    currentunitPrice = convertBaseUnitPriceToDefaultCurrency(currentunitPrice, defaultCurrencyCode, commonServices);
                }
            }
        }
        return roundOff(currentunitPrice, 2);
    }

    private static String getBaseCurrencyCode(String defaultCurrency, CommonServices commonServices) {
        CurrencyRate currencyRate = commonServices.getValidCurrencyRate(defaultCurrency);
        String baseCurrency = null;
        if (currencyRate != null) {
            baseCurrency = currencyRate.getBaseCurrencyCode();
        }
        return baseCurrency;
    }

    private static double convertActualUnitPriceToBaseCurrency(double unitPrice, String currencyCode, CommonServices commonServices) {
        CurrencyRate currencyRate = commonServices.getValidCurrencyRate(currencyCode);
        if (!StringUtils.isEmpty(currencyRate) && currencyRate.getRate() != 0) {
            return (unitPrice * currencyRate.getRate());
        }
        return 0;
    }

    private static double convertBaseUnitPriceToDefaultCurrency(double unitPrice, String currencyCode, CommonServices commonServices) {
        CurrencyRate currencyRate = commonServices.getValidCurrencyRate(currencyCode);
        if (!StringUtils.isEmpty(currencyRate) && currencyRate.getRate() != 0) {
            return (unitPrice / currencyRate.getRate());
        }
        return 0;
    }
    
    
    public static double convertCurrencyFromActualToDefault(double unitPrice, String actualCurrencyCode, String defaultCurrencyCode,
            Map<String, CurrencyRate> currencyToCurrencyRateMap) {
        double currentunitPrice = unitPrice;
        if (unitPrice != 0) {
            CurrencyRate currencyRate = currencyToCurrencyRateMap.get(actualCurrencyCode);
            String baseCurrencyCode = null;
            if (currencyRate != null) {
                baseCurrencyCode = currencyRate.getBaseCurrencyCode();
            }
            if (!StringUtils.isEmpty(actualCurrencyCode) && !StringUtils.isEmpty(defaultCurrencyCode) && !actualCurrencyCode.equals(defaultCurrencyCode)) {
                if (StringUtils.isEmpty(baseCurrencyCode)) {
                    baseCurrencyCode = actualCurrencyCode;
                }
                if (!actualCurrencyCode.equals(baseCurrencyCode)) {
                    double convUnitPrice = convertActualUnitPriceToBaseCurrency(currentunitPrice, actualCurrencyCode, currencyToCurrencyRateMap);
                    if (convUnitPrice != 0) {
                        currentunitPrice = convUnitPrice;
                    }
                }
                if (!defaultCurrencyCode.equals(baseCurrencyCode)) {
                    currentunitPrice = convertBaseUnitPriceToDefaultCurrency(currentunitPrice, defaultCurrencyCode, currencyToCurrencyRateMap);
                }
            }
        }
        return currentunitPrice;
    }

    private static double convertActualUnitPriceToBaseCurrency(double unitPrice, String currencyCode, Map<String, CurrencyRate> currencyToCurrencyRateMap) {
        CurrencyRate currencyRate = currencyToCurrencyRateMap.get(currencyCode);
        if (!StringUtils.isEmpty(currencyRate) && currencyRate.getRate() != 0) {
            return (unitPrice * currencyRate.getRate());
        }
        return 0;
    }

    private static double convertBaseUnitPriceToDefaultCurrency(double unitPrice, String currencyCode, Map<String, CurrencyRate> currencyToCurrencyRateMap) {
        CurrencyRate currencyRate = currencyToCurrencyRateMap.get(currencyCode);
        if (!StringUtils.isEmpty(currencyRate) && currencyRate.getRate() != 0) {
            return (unitPrice / currencyRate.getRate());
        }
        return 0;
    }
}
