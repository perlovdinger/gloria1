define([ 
	'utils/backbone/GloriaCollection',
	'models/PartBalanceModel',
	'utils/UserHelper'
], function(GloriaCollection, PartBalanceModel, UserHelper) {
	
	var PartBalanceCollection = GloriaCollection.extend({
	    model : PartBalanceModel,
	    
		url : function() {
		    return '/warehouse/v1/partbalance?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
		},
		
		sumOfScrappableQuanity: function() {
		    return this.sumOfAttributeByCondition('quantity', {materialType: 'RELEASED'});		    
		}		
	});
	return PartBalanceCollection;
});