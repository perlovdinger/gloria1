package com.volvo.gloria.material.d.status.goodsReceiptLine;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.procurematerial.d.entities.GoodsReceiptLine;
import com.volvo.gloria.procurematerial.d.entities.status.goodsReceiptLine.GoodsReceiptLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.DeliveryNoteRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class GoodsReceiptLineTest extends AbstractTransactionalTestCase {

    private GoodsReceiptLine goodsReceiptLine;

    private DeliveryNoteRepository deliveryNoteRepository;

    @Before
    public void setUp() {
        goodsReceiptLine = Mockito.mock(GoodsReceiptLine.class);
        deliveryNoteRepository = Mockito.mock(DeliveryNoteRepository.class);
    }

    @Test
    public void test_Cancel_from_RECEIVED_to_CANCELLED() throws GloriaApplicationException {
        // arrange
        Mockito.when(goodsReceiptLine.getStatus()).thenReturn(GoodsReceiptLineStatus.RECEIVED);
        Mockito.when(goodsReceiptLine.getQuantity()).thenReturn(0L);
        Mockito.when(goodsReceiptLine.getQuantityCancelled()).thenReturn(10L);
        // act
        goodsReceiptLine.getStatus().cancel(goodsReceiptLine, deliveryNoteRepository);
        // assert
        Mockito.verify(goodsReceiptLine).setStatus(Mockito.eq(GoodsReceiptLineStatus.CANCELLED));
    }

    @Test
    public void test_Cancel_from_RECEIVED_to_PARTIALLY_CANCELLED() throws GloriaApplicationException {
        // arrange
        Mockito.when(goodsReceiptLine.getStatus()).thenReturn(GoodsReceiptLineStatus.RECEIVED);
        Mockito.when(goodsReceiptLine.getQuantity()).thenReturn(10L);
        Mockito.when(goodsReceiptLine.getQuantityCancelled()).thenReturn(5L);
        // act
        goodsReceiptLine.getStatus().cancel(goodsReceiptLine, deliveryNoteRepository);
        // assert
        Mockito.verify(goodsReceiptLine).setStatus(Mockito.eq(GoodsReceiptLineStatus.PARTIAL_CANCELLED));
    }

    @Test
    public void test_Cancel_from_PARTIALY_CANCELLED_to_CANCELLED() throws GloriaApplicationException {
        // arrange
        Mockito.when(goodsReceiptLine.getStatus()).thenReturn(GoodsReceiptLineStatus.PARTIAL_CANCELLED);
        Mockito.when(goodsReceiptLine.getQuantity()).thenReturn(0L);
        Mockito.when(goodsReceiptLine.getQuantityCancelled()).thenReturn(5L);
        // act
        goodsReceiptLine.getStatus().cancel(goodsReceiptLine, deliveryNoteRepository);
        // assert
        Mockito.verify(goodsReceiptLine).setStatus(Mockito.eq(GoodsReceiptLineStatus.CANCELLED));
    }

    @Test(expected = GloriaApplicationException.class)
    public void test_Cancel_CANCELLED() throws GloriaApplicationException {
        // arrange
        Mockito.when(goodsReceiptLine.getStatus()).thenReturn(GoodsReceiptLineStatus.CANCELLED);
        // act
        goodsReceiptLine.getStatus().cancel(goodsReceiptLine, deliveryNoteRepository);
        // assert
    }
}
