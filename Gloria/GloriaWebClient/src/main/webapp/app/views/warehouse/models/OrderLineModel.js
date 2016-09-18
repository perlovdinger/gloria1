define([
    'utils/backbone/GloriaModel',
    'moment',
    'views/deliverycontrol/myorderoverview/collection/DeliveryScheduleCollection'
], function(Model, moment, DeliveryScheduleCollection) {
    
    var OrderLineModel = Model.extend({
        
        urlRoot : '/warehouse/v1/orderlines',

        initialize: function() {
            this.deliverySchedules = new DeliveryScheduleCollection();
        },
        
        validate : function(attrs, options) {
            var errors = {};

            if (!attrs.agreedStaDate) {
                errors.agreedStaDate = "agreedStaMandatory";
            } else if (!moment(attrs.agreedStaDate).isValid()) {
                errors.agreedStaDate = "agreedStaInvalidDate";
            }
            
            if (!attrs.expectedDate) {
                errors.expectedDate = "expectedDateMandatory";
            } else if (!moment(attrs.expectedDate).isValid()) {
                errors.expectedDate = "expectedDateInvalidDate";
            }
            
            if (!attrs.plannedDispatchDate) {
                errors.plannedDispatchDate = "plannedDispatchMandatory";
            } else if (!moment(attrs.plannedDispatchDate).isValid()) {
                errors.plannedDispatchDate = "plannedDispatchInvalidDate";
            }
            
            if (!attrs.actualDispatchDate) {
                errors.actualDispatchDate = "actualDispatchMandatory";
            } else if (!moment(attrs.actualDispatchDate).isValid()) {
                errors.actualDispatchDate = "actualDispatchInvalidDate";
            }
            
            if (!attrs.carrier) {
                errors.carrier = "carrierMandatory";
            }
            
            if (!attrs.trackingNo) {
                errors.trackingNo = "trackingNoMandatory";
            }
            
            if (! _.isEmpty(errors)) {
                 return errors;
            }
        }
    });
    
    return OrderLineModel;
});