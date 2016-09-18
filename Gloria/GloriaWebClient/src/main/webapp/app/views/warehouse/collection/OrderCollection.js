define([ 
	'utils/backbone/GloriaCollection',
	'models/OrderModel',
    'utils/UserHelper' 
], function(Collection, OrderModel, UserHelper) {
	
	var OrderCollection = Collection.extend({
	   
		model : OrderModel,
		
	    url : function() {
            return '/material/v1/orders/warehouse?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
	    }

	});
	
	return OrderCollection;
});