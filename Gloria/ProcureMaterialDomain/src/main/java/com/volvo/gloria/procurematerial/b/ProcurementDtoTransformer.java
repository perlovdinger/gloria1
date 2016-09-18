package com.volvo.gloria.procurematerial.b;

import java.util.List;
import java.util.Map;

import com.volvo.gloria.procurematerial.c.dto.BuyerCodeDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialDTO;
import com.volvo.gloria.procurematerial.c.dto.MaterialHeaderDTO;
import com.volvo.gloria.procurematerial.c.dto.OrderLineDTO;
import com.volvo.gloria.procurematerial.c.dto.SupplierDTO;
import com.volvo.gloria.procurematerial.d.entities.Buyer;
import com.volvo.gloria.procurematerial.d.entities.DeliverySchedule;
import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.MaterialHeader;
import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLine;
import com.volvo.gloria.procurematerial.d.entities.OrderLineLog;
import com.volvo.gloria.procurematerial.d.entities.Supplier;

/**
 * Services for ProcurementDtoTransformer.
 */
public interface ProcurementDtoTransformer {

    MaterialDTO transformAsDTO(Material material);

    List<MaterialDTO> transformAsMaterialDTOs(List<Material> materials);

    BuyerCodeDTO transformAsDTO(Buyer buyerCode);

    List<BuyerCodeDTO> transformBuyerCodeEtyToDTO(List<Buyer> buyerCodes);

    MaterialHeaderDTO transformAsDTO(MaterialHeader requestHeader);

    List<MaterialHeaderDTO> transformAsDTO(List<MaterialHeader> requestHeaders);

    OrderLineDTO transformAsDTO(OrderLine orderLine, boolean loadDeliverySchedule);

    List<OrderLineDTO> transformAsOrderLineDTOs(List<OrderLine> orderLines, boolean loadDeliverySchedule);
    
    List<OrderLineDTO> transformAsOrderLineDTOs(List<OrderLine> orderLines, boolean loadDeliverySchedule, List<Long> orderLineOIDList);
    
    OrderLineDTO transformDeliveryScheduleAsOrderLineDTOs(DeliverySchedule deliverySchedule);

    List<OrderLineDTO> transformDeliverySchedulesAsOrderLineDTOs(List<DeliverySchedule> deliverySchedules);

    List<MaterialDTO> transformAsUsageReplacedMaterialDTOs(List<Material> materials);
    
    SupplierDTO transformAsDTO(Supplier supplier);

    List<SupplierDTO> transformAsSuppliersDTO(List<Supplier> suppliers);
    
    OrderLineDTO transformAsDTO(OrderLine orderLine, boolean loadDeliverySchedule, List<Material> materials,
            Map<Long, List<MaterialLine>> materialIdMaterialLineMap, List<DeliverySchedule> deliverySchedules, List<OrderLineLog> orderLineLogs);
}
