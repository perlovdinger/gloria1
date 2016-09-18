package com.volvo.gloria.procurematerial.repositories.b;

import java.util.List;

import com.volvo.gloria.procurematerial.d.entities.OrderSap;
import com.volvo.gloria.procurematerial.d.entities.OrderSapAccounts;
import com.volvo.gloria.procurematerial.d.entities.OrderSapLine;
import com.volvo.gloria.procurematerial.d.entities.OrderSapSchedule;
import com.volvo.gloria.util.persistence.GenericRepository;

/**
 * 
 */
public interface OrderSapRepository extends GenericRepository<OrderSap, Long> {


    void addOrderSapLine(OrderSapLine orderSapLine);

    OrderSapLine findOrderSapLineById(Long orderSapLineOid);

    OrderSapLine updateOrderSapLine(OrderSapLine orderSapLine);

    void deleteOrderSapLine(long orderSapLineOid);

    void addOrderSapSchedule(OrderSapSchedule orderSapSchedule);

    OrderSapSchedule findOrderSapScheduleById(Long orderSapScheduleOid);

    OrderSapSchedule updateOrderSapSchedule(OrderSapSchedule orderSapSchedule);

    void deleteOrderSapSchedule(long orderSapScheduleOid);

    void addOrderSapAccounts(OrderSapAccounts orderSapAccounts);

    OrderSapAccounts findOrderSapAccountsById(Long orderSapAccountsOid);

    OrderSapAccounts updateOrderSapAccounts(OrderSapAccounts orderSapAccounts);

    void deleteOrderSapAccounts(long orderSapAccountsOid);

    OrderSap findOrderSapByUniqueExtOrder(String uniqueExtOrder);
    
    List<OrderSap> findOrderSapForCompanyCode(String companyCode);
}
