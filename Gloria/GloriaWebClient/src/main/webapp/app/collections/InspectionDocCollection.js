define([ 
    'utils/backbone/GloriaCollection', 
    'models/InspectionDocModel' 
    ], function(Collection, InspectionDocModel) {

	var InspectionDocCollection = Collection.extend({
		url : '/v1/qidocs',
		model : InspectionDocModel
	});

	return InspectionDocCollection;
});
