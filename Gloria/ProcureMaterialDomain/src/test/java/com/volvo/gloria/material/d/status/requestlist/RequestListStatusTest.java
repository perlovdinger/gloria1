package com.volvo.gloria.material.d.status.requestlist;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.RequestList;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.d.entities.status.requestlist.RequestListStatus;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class RequestListStatusTest extends AbstractTransactionalTestCase {
    public RequestListStatusTest() {
        System.setProperty("com.volvo.jvs.applicationContextName", "junit-test/applicationContext.xml");
    }
    
    private RequestList requestList;
    private UserDTO userDto;
    private MaterialHeaderRepository materialHeaderRepository;
    private TraceabilityRepository traceabilityRepository;
    private MaterialLine materialLine;
    private PickList pickList;
   
    @Before
    public void setUp() {
       requestList = Mockito.mock(RequestList.class);
       userDto = Mockito.mock(UserDTO.class);
       materialHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);
       traceabilityRepository = Mockito.mock(TraceabilityRepository.class); 
       materialLine = Mockito.mock(MaterialLine.class);
       pickList = Mockito.mock(PickList.class);
       userDto.setUserName("test");
       userDto.setId("test");
    }

    @Test
    public void testCreatedCancelRequestList() throws GloriaApplicationException {
       Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.STORED);
       Mockito.when(materialLine.getPreviousStatus()).thenReturn(MaterialLineStatus.QI_OK);
       Mockito.when(requestList.getStatus()).thenReturn(RequestListStatus.CREATED);
       Mockito.when(materialHeaderRepository.getMaterialLinesByRequestListId(0)).thenReturn(Arrays.asList(materialLine));
       requestList.getStatus().cancel(requestList, userDto, materialHeaderRepository, traceabilityRepository);
       Mockito.verify(materialLine, Mockito.never()).setStatus(Mockito.eq(MaterialLineStatus.STORED));
    }
    
    @Test
    public void testSentCancelRequestList() throws GloriaApplicationException {
       Mockito.when(materialLine.getStatus()).thenReturn(MaterialLineStatus.STORED);
       Mockito.when(materialLine.getPreviousStatus()).thenReturn(MaterialLineStatus.STORED);
       Mockito.when(requestList.getStatus()).thenReturn(RequestListStatus.SENT);
       Mockito.when(materialHeaderRepository.getMaterialLinesByRequestListId(0)).thenReturn(Arrays.asList(materialLine));
       requestList.getStatus().cancel(requestList, userDto, materialHeaderRepository, traceabilityRepository);
       Mockito.verify(materialLine).setStatus(Mockito.eq(MaterialLineStatus.STORED));
    }
    
    
    @Test(expected=GloriaApplicationException.class)
    public void testReqListWithPickListCancelRequestList() throws GloriaApplicationException {
       Mockito.when(requestList.getStatus()).thenReturn(RequestListStatus.SENT);
       Mockito.when(materialHeaderRepository.findPickListByRequestListId(0)).thenReturn(Arrays.asList(pickList));
       requestList.getStatus().cancel(requestList, userDto, materialHeaderRepository, traceabilityRepository);
    }


    

}