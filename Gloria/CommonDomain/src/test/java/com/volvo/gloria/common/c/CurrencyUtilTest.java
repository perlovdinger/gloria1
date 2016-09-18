package com.volvo.gloria.common.c;

import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.common.b.CommonServices;
import com.volvo.gloria.common.d.entities.CurrencyRate;
import com.volvo.gloria.util.GloriaApplicationException;

public class CurrencyUtilTest {

    private CommonServices commonServices;
    private CurrencyRate currencyRateSek;
    private CurrencyRate currencyRateEuro;
    private CurrencyRate currencyRateJPY;

    @Before
    public void setUp() {
        commonServices = Mockito.mock(CommonServices.class);
        currencyRateSek = Mockito.mock(CurrencyRate.class);
        currencyRateEuro = Mockito.mock(CurrencyRate.class);
        currencyRateJPY = Mockito.mock(CurrencyRate.class);
    }

    /**
     * test no conersion rates defined for currency
     * 
     */
    @Test
    public void testConversion_NoRatesDefined() throws GloriaApplicationException {
        // arrange
        double unitPrice = 2.12;
        // act
        double convertedUnitPrice = CurrencyUtil.convertUnitPriceToEUR(unitPrice, "SEK", commonServices);
        // assert
        Assert.assertEquals(0, convertedUnitPrice, 0);
    }

    /**
     * no conversion applied for EURO
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testConversionFromEuro() throws GloriaApplicationException {
        // arrange
        double unitPrice = 2.12;
        // act
        double convertedUnitPrice = CurrencyUtil.convertUnitPriceToEUR(unitPrice, "EUR", commonServices);
        // assert
        Assert.assertEquals(2.12, convertedUnitPrice, 0);
    }

    /**
     * Base Currency Rate 1 EUR = 9.19 SEK
     * 
     * convert 55.14 SEK to EURO
     * 
     * expected 6 EUROS
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testConversionFromSEK() throws GloriaApplicationException {
        // arrange
        double unitPrice = 55.14;
        Mockito.when(commonServices.getValidCurrencyRate("EUR")).thenReturn(currencyRateEuro);
        Mockito.when(currencyRateEuro.getRate()).thenReturn(9.19);
        // act
        double convertedUnitPrice = CurrencyUtil.convertUnitPriceToEUR(unitPrice, "SEK", commonServices);
        // assert
        Assert.assertEquals(6, convertedUnitPrice, 0);
    }

    /**
     * Base Currency Rate 1 USD = 8.71 SEK
     * 
     * convert 10 USD to SEK
     * 
     * Base Currency Rate 1 EUR = 9.19 SEK
     * 
     * convert 87.1 SEK to EURO
     * 
     * expected 9.47 EUROS
     * 
     * @throws GloriaApplicationException
     */
    @Test
    public void testConversionFromUSD() throws GloriaApplicationException {
        // arrange
        double unitPrice = 10;
        Mockito.when(commonServices.getValidCurrencyRate("SEK")).thenReturn(currencyRateSek);
        Mockito.when(currencyRateSek.getRate()).thenReturn(8.71);
        Mockito.when(commonServices.getValidCurrencyRate("EUR")).thenReturn(currencyRateEuro);
        Mockito.when(currencyRateEuro.getRate()).thenReturn(9.19);
        // act
        double convertedUnitPrice = CurrencyUtil.convertUnitPriceToEUR(unitPrice, "USD", commonServices);
        // assert
        Assert.assertEquals(1.09, convertedUnitPrice, 2);
    }
    
    
    @Test
    public void testConversionFromJPY() {
        // arrange
        double unitPrice = 65000;
        Mockito.when(commonServices.getValidCurrencyRate("SEK")).thenReturn(currencyRateSek);
        Mockito.when(currencyRateSek.getRate()).thenReturn(8.71);
        Mockito.when(commonServices.getValidCurrencyRate("JPY")).thenReturn(currencyRateJPY);
        Mockito.when(currencyRateJPY.getRate()).thenReturn(0.08);
        Mockito.when(commonServices.getValidCurrencyRate("EUR")).thenReturn(currencyRateEuro);
        Mockito.when(currencyRateEuro.getRate()).thenReturn(9.19);
        // act
        double convertedUnitPrice =  CurrencyUtil.convertUnitPriceToEUR(unitPrice, "JPY", commonServices);
        // assert
        Assert.assertEquals(565.83, convertedUnitPrice, 2);
    }
    
    @Test
    public void testConvertCurrencyFromActualToDefault1() {
        String actualCurrencyCode = "INR";
        String defaultCurrencyCode = "EUR";
        // arrange
        double unitPrice = 20;
        Mockito.when(commonServices.getValidCurrencyRate("INR")).thenReturn(currencyRateSek);
        Mockito.when(currencyRateSek.getRate()).thenReturn(0.12);
        Mockito.when(commonServices.getValidCurrencyRate("SEK")).thenReturn(null);
        Mockito.when(commonServices.getValidCurrencyRate("EUR")).thenReturn(currencyRateEuro);
        Mockito.when(currencyRateEuro.getRate()).thenReturn(9.35);
        // act
        double convertedUnitPrice =  CurrencyUtil.convertCurrencyFromActualToDefault(unitPrice, actualCurrencyCode, defaultCurrencyCode, commonServices);
        // assert
        Assert.assertEquals(0.25, convertedUnitPrice, 2);
    }
    
    
    @Test
    public void testConvertCurrencyFromActualToDefault2() {
        String actualCurrencyCode = "INR";
        String defaultCurrencyCode = "BRL";
        // arrange
        double unitPrice = 20;
        Mockito.when(commonServices.getValidCurrencyRate("INR")).thenReturn(currencyRateSek);
        Mockito.when(currencyRateSek.getRate()).thenReturn(0.12);
        Mockito.when(currencyRateSek.getBaseCurrencyCode()).thenReturn("SEK");
        Mockito.when(commonServices.getValidCurrencyRate("SEK")).thenReturn(null);
        Mockito.when(commonServices.getValidCurrencyRate("BRL")).thenReturn(currencyRateEuro);
        Mockito.when(currencyRateEuro.getRate()).thenReturn(2.09);
        // act
        double convertedUnitPrice =  CurrencyUtil.convertCurrencyFromActualToDefault(unitPrice, actualCurrencyCode, defaultCurrencyCode, commonServices);
        // assert
        Assert.assertEquals(1.14, convertedUnitPrice, 2);
    }
    
    
    @Test
    public void testConvertCurrencyFromActualToDefaultNoBaseCurrencyRate() {
        String actualCurrencyCode = "SEK";
        String defaultCurrencyCode = "EUR";
        // arrange
        double unitPrice = 20;
        Mockito.when(commonServices.getValidCurrencyRate("SEK")).thenReturn(null);
        Mockito.when(commonServices.getValidCurrencyRate("EUR")).thenReturn(currencyRateEuro);
        Mockito.when(currencyRateEuro.getRate()).thenReturn(9.35);
        // act
        double convertedUnitPrice =  CurrencyUtil.convertCurrencyFromActualToDefault(unitPrice, actualCurrencyCode, defaultCurrencyCode, commonServices);
        // assert
        Assert.assertEquals(2.14, convertedUnitPrice, 2);
    }
    
    
    @Test
    public void testConvertCurrencyFromActualToDefaultSame() {
        String actualCurrencyCode = "SEK";
        String defaultCurrencyCode = "SEK";
        // arrange
        double unitPrice = 20;
        // act
        double convertedUnitPrice =  CurrencyUtil.convertCurrencyFromActualToDefault(unitPrice, actualCurrencyCode, defaultCurrencyCode, commonServices);
        // assert
        Assert.assertEquals(20, convertedUnitPrice, 2);
    }
}
