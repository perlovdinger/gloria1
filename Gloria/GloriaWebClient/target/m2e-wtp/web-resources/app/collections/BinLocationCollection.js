define([ 
	'utils/backbone/GloriaCollection', 
	'models/BinLocationModel',
	'utils/UserHelper'
], function(Collection, BinLocationModel, UserHelper) {
	
	var BinLocationCollection = Collection.extend({
	    
	    model : BinLocationModel,
	    
	    url : function() {
	        return '/warehouse/v1/binlocations?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
	    }

	});
	
	return BinLocationCollection;
});