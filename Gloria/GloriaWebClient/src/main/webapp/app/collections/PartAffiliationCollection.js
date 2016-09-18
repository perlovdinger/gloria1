define([ 
    'utils/backbone/GloriaCollection', 
    'models/PartAffiliationModel' 
    ], function(Collection, PartAffiliationModel) {

	var PartAffiliationCollection = Collection.extend({

		model : PartAffiliationModel,

		url : '/procurement/v1/partaffiliations?requestable=true'

	});

	return PartAffiliationCollection;
});