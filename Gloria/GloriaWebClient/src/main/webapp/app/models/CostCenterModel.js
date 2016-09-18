define([ 
        'utils/backbone/GloriaModel'
        ], function(Model) {

	var CostCenterModel = Model.extend({

		urlRoot : '/procurement/v1/costcenters'

	});

	return CostCenterModel;
});