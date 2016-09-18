define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var InspectionDocModel = Model.extend({

	    urlRoot : '/v1/qidocs'
	});

	return InspectionDocModel;
});