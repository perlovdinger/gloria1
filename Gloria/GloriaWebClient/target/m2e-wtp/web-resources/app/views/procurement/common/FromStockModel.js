define([ 'utils/backbone/GloriaModel' ], function(Model) {

	var FromStockModel = Model.extend({
		urlRoot : '/procurement/v1/materiallines/available'
	});

	return FromStockModel;
});