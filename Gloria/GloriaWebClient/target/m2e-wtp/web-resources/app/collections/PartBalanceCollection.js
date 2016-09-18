define([ 
	'utils/backbone/GloriaPageableCollection',
	'models/PartBalanceModel',
	'utils/UserHelper'
], function(PageableCollection, PartBalanceModel, UserHelper) {
	
	var PartBalanceCollection = PageableCollection.extend({
	    model : PartBalanceModel,
	    
		url : function() {
		    return '/warehouse/v1/partbalance?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
		}
		
	});
	return PartBalanceCollection;
});