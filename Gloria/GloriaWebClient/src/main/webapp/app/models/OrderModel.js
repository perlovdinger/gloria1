define([
    'utils/backbone/GloriaModel'
], function(Model) {
    
	var OrderModel = Model.extend({
	    
        urlRoot : '/material/v1/orders',
        
        validate : function(attrs, options) {
        	var errors = {};
        
        	if (!attrs.staAcceptedDate) {
        		errors.staAcceptedDate = "staAcceptedMandatory";
         	} else if (!moment(attrs.staAcceptedDate).isValid()) {
        		errors.expectedDate = "staAcceptedInvalidDate";
         	}
        
            if (! _.isEmpty(errors)) {
         		 return errors;
          	}
       	 }        
	});
	
	return OrderModel;
});