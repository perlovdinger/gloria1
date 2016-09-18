package com.volvo.gloria.procurematerial.d.type.internalexternal;

import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class InternalExternalTest extends AbstractTransactionalTestCase {
    public InternalExternalTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    @Test
    public void testOrderNoEvaluationForCountryUS() throws GloriaApplicationException{
        //arrange
        String orderNo = "M123456-sss";
        //act
        String[] orderTokens = InternalExternal.EXTERNAL.evaluateOrderNo(orderNo,"675");
        //assert   
        Assert.assertEquals("675", orderTokens[0]);
        Assert.assertEquals("sss", orderTokens[1]);
    }
    
    @Test
    public void testOrderNoEvaluationForCountryOthers() throws GloriaApplicationException{
        //arrange
        String orderNo = "ppp-nnnnnn-sss";
        //act
        String[] orderTokens = InternalExternal.EXTERNAL.evaluateOrderNo(orderNo,"675");
        //assert   
        Assert.assertEquals("ppp", orderTokens[0]);
        Assert.assertEquals("sss", orderTokens[1]);
    }
    
}
