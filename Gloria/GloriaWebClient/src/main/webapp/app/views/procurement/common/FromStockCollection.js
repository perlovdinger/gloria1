define(['utils/backbone/GloriaPageableCollection',
		'views/procurement/common/FromStockModel'
], function(PageableCollection, FromStockModel) {

	var FromStockCollection = PageableCollection.extend({
		model : FromStockModel,
		url : '/procurement/v1/materiallines/available',
		
		
		 statusOrder: ['RELEASED', 'ADDITIONAL', 'USAGE'], // when there is no specific ordering (neither asc / desc)
		    
		    comparator: function(item) {
		    return _.indexOf(this.statusOrder, item.get('materialType'));
		    }
	});

	return FromStockCollection;
});