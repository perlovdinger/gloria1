package com.volvo.gloria.procurematerial.d.status.procureline;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.mockito.Mockito;

import com.volvo.gloria.common.c.dto.UserDTO;
import com.volvo.gloria.procurematerial.c.dto.ProcureLineDTO;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;
import com.volvo.gloria.procurematerial.d.entities.status.procureline.ProcureLineHelper;
import com.volvo.gloria.procurematerial.repositories.b.PurchaseOrganisationRepository;
import com.volvo.gloria.util.GloriaApplicationException;

public class ProcureLineHelperTest {

    @Test
    public void testAllowNullMaxPrice() throws GloriaApplicationException {
        ProcureLineDTO procureLineDTO = Mockito.mock(ProcureLineDTO.class);
        Mockito.when(procureLineDTO.getMaxPrice()).thenReturn(null);
        UserDTO userDTO = Mockito.mock(UserDTO.class);
        ProcureLine procureLine = new ProcureLine();
        PurchaseOrganisationRepository purchaseOrganisationRepo = Mockito.mock(PurchaseOrganisationRepository.class);
        
        ProcureLineHelper.updateAttributesByProcureType(procureLineDTO, procureLine, purchaseOrganisationRepo, userDTO);
        
        assertNull("Should allow null maxPrice", procureLine.getMaxPrice());
    }

}
