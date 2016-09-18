package com.volvo.gloria.procurematerial.d.type.request;

import org.junit.Assert;
import org.junit.Test;

import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.type.procure.ProcureType;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class RequestTypeTest extends AbstractTransactionalTestCase {
    public RequestTypeTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    @Test
    public void testSuggestForStockInternal() throws GloriaApplicationException {
        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setProcureType(ProcureType.INTERNAL);

        // Act

        // Assert
        Assert.assertEquals(RequestType.FOR_STOCK.suggestProcureType(procureLine, true), ProcureType.INTERNAL);
    }

    @Test
    public void testSuggesNotForStockInternal() throws GloriaApplicationException {
        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setProcureType(ProcureType.INTERNAL);

        // Act

        // Assert
        Assert.assertEquals(RequestType.TESTOBJECT_FIRST_BUILD.suggestProcureType(procureLine, true), ProcureType.INTERNAL_FROM_STOCK);
    }

    @Test
    public void testSuggestForStockExternal() throws GloriaApplicationException {
        // Arrange
        ProcureLine procureLine = new ProcureLine();
        procureLine.setProcureType(ProcureType.EXTERNAL);

        // Act

        // Assert
        Assert.assertEquals(RequestType.FOR_STOCK.suggestProcureType(procureLine, true), ProcureType.EXTERNAL);
    }

   

}
