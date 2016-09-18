define([
    'utils/backbone/GloriaModel',
    'utils/UserHelper'
], function(Model, UserHelper) {
    
	var PartBalanceModel = Model.extend({
	    
        urlRoot : function() {
            return '/warehouse/v1/partbalance?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
        },
        
        isScrapable: function() {
            return !!this.get('materialType') && this.get('materialType').toUpperCase() === 'RELEASED';
        }
        
        
	});
	return PartBalanceModel;
});