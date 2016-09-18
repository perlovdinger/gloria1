define([ 
	'utils/backbone/GloriaPageableCollection',
	'models/WarehouseMaterialLineModel' 
], function(PageableCollection, WarehouseMaterialLineModel) {
	
	var WarehouseMaterialLineCollection = PageableCollection.extend({
		initialize: function(models, options) {
			options || (options = {});
			this.beforeFetch = options.beforeFetch;
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

	    model : WarehouseMaterialLineModel,
	    
		url : '/warehouse/v1/materiallines/transferReturn'
		
	});
	return WarehouseMaterialLineCollection;
});