define([ 
    'utils/backbone/GloriaModel',
    'utils/UserHelper'
], function(Model, UserHelper) {
    
	var BinLocationModel = Model.extend({
	    
        urlRoot : function() {
            return '/warehouse/v1/binlocations?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
        }
            
	});
	return BinLocationModel;
});