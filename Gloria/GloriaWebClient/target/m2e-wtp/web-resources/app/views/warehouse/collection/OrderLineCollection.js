define([ 
	'utils/backbone/GloriaPageableCollection',
	'views/warehouse/models/OrderLineModel',
	'utils/UserHelper'
], function(PageableCollection, OrderLineModel, UserHelper) {
	
	var OrderLineCollection = PageableCollection.extend({
		
		initialize: function(models, options) {
			options || (options = {});
			this.beforeFetch = options.beforeFetch;
			PageableCollection.prototype.initialize.apply(this, arguments);
		},
		
		fetch: function() {
			var beforeFetch = true;
			if(this.beforeFetch && typeof this.beforeFetch == 'function') {
				beforeFetch = this.beforeFetch(this);				
			} 
			if(beforeFetch) {
				return PageableCollection.prototype.fetch.apply(this, arguments);
			}
		},

		model : OrderLineModel,
	    
		url : function() {		    
		    return '/warehouse/v1/orderlines/current?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
		}
	});
	
	return OrderLineCollection;
});