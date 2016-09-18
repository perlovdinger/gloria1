define([ 
        'utils/backbone/GloriaModel'
        ], function(Model) {

	var DangerousGoodsModel = Model.extend({

		urlRoot : '/common/v1/dangerousgoods'

	});

	return DangerousGoodsModel;
});