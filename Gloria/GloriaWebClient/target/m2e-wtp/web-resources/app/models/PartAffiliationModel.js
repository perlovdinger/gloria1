define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var PartAffiliationModel = Model.extend({

		urlRoot : '/procurement/v1/partaffiliations?requestable=true'

	});

	return PartAffiliationModel;
});