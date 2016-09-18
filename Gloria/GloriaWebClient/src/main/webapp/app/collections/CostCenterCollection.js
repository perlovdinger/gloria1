define([ 
         'utils/backbone/GloriaCollection', 
         'models/CostCenterModel' 
         ], function(Collection, CostCenterModel) {

	var CostCenterCollection = Collection.extend({

		model : CostCenterModel,

		url : '/procurement/v1/costcenters'
		    
	});

	return CostCenterCollection;
});