package com.volvo.gloria.procurematerial.d.entities.status.picklist;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.authorization.b.UserServices;
import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.common.repositories.b.TraceabilityRepository;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.PickList;
import com.volvo.gloria.procurematerial.d.entities.RequestGroup;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatus;
import com.volvo.gloria.procurematerial.repositories.b.MaterialHeaderRepository;
import com.volvo.gloria.util.GloriaApplicationException;
import com.volvo.jvs.test.AbstractTransactionalTestCase;

public class PickListStatusTest extends AbstractTransactionalTestCase {

    private UserServices userServices;

    private MaterialHeaderRepository materialHeaderRepository;

    private TraceabilityRepository traceabilityRepository;

    private PickList pickList;
    
    private RequestGroup requestGroup;
    
    private MaterialLine materialLine;

    private UserDTO userDTO;

    @Before
    public void setUp() throws GloriaApplicationException {
        userDTO = Mockito.mock(UserDTO.class);
        userServices = Mockito.mock(UserServices.class);
        materialHeaderRepository = Mockito.mock(MaterialHeaderRepository.class);
        traceabilityRepository = Mockito.mock(TraceabilityRepository.class);
        Mockito.when(userServices.getUser("ALL")).thenReturn(userDTO);
        
        pickList = new PickList();
        requestGroup = new RequestGroup();
        materialLine = new MaterialLine();
        materialLine.setStatus(MaterialLineStatus.READY_TO_STORE);
        pickList.setMaterialLines(Arrays.asList(materialLine));
        pickList.setRequestGroups(Arrays.asList(requestGroup));
        requestGroup.setMaterialLines(Arrays.asList(materialLine));
        requestGroup.setPickList(pickList);
        materialLine.setPickList(pickList);
    }

    @Test
    public void cancel_CREATED() throws GloriaApplicationException {
        PickListStatus.CREATED.cancel(pickList, userDTO, materialHeaderRepository, traceabilityRepository);
        Assert.assertNull(requestGroup.getPickList());
        Assert.assertNull(materialLine.getPickList());
    }

    @Test
    public void cancel_INWORK() throws GloriaApplicationException {
        PickListStatus.IN_WORK.cancel(pickList, userDTO, materialHeaderRepository, traceabilityRepository);
        Assert.assertNull(requestGroup.getPickList());
        Assert.assertNull(materialLine.getPickList());
    }

    @Test(expected = GloriaApplicationException.class)
    public void cancel_PICKED() throws GloriaApplicationException {
        PickListStatus.PICKED.cancel(pickList, userDTO, materialHeaderRepository, traceabilityRepository);
    }

    @Test(expected = GloriaApplicationException.class)
    public void cancel_CANCELLED() throws GloriaApplicationException {
        PickListStatus.CANCELLED.cancel(pickList, userDTO, materialHeaderRepository, traceabilityRepository);
    }
}
