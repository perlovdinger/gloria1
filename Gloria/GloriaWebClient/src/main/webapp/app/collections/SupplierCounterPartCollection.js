define(['utils/backbone/GloriaCollection', 
        'models/SupplierCounterPartModel'
],function(Collection, SupplierCounterPartModel) {

	var SupplierCounterPartCollection = Collection.extend({
		model : SupplierCounterPartModel
	});

	return SupplierCounterPartCollection;
});