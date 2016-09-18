package com.volvo.gloria.authorization.d.userrole;

import java.util.Set;

import com.volvo.gloria.authorization.d.entities.GloriaUser;
import com.volvo.gloria.common.b.CommonServices;


/**
 * Enum class for Direct send.
 */
public enum UserRole implements UserRoleOperations{
    Material_Controller(new MaterialController()), 
    Internal_Procurer(new InternalProcurer()), 
    Delivery_Controller(new DeliveryController()), 
    Warehouse_personell(new WarehousePersonell()), 
    Warehouse_manager(new WarehousePersonell()), 
    PSQA(new WarehousePersonell()), 
    GR_Only(new Viewer()), 
    Requester_for_Pull(new Viewer()), 
    Key_user(new Viewer()), 
    IT_support(new ItSupport()), 
    BORROW(new Viewer()), 
    Viewer(new Viewer()), 
    Viewer_Price(new Viewer());

    
    private final UserRoleOperations userRoleOperations;

    UserRole(UserRoleOperations userRoleOperations) {
        this.userRoleOperations = userRoleOperations;
    }

    @Override
    public Set<String> getUserCompanyCodeCodeFilters(GloriaUser gloriaUser, CommonServices commonServices) {
        return userRoleOperations.getUserCompanyCodeCodeFilters(gloriaUser, commonServices);
    }

    @Override
    public Set<String> getUserSiteSiteIdFilters(GloriaUser gloriaUser, CommonServices commonServices) {
        return userRoleOperations.getUserSiteSiteIdFilters(gloriaUser, commonServices);
    }

    public boolean isItSupport() {
        return userRoleOperations.isItSupport();
    }    
}
