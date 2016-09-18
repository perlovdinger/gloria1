define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var QualityDocModel = Model.extend({

		urlRoot : '/documents/v1/receivedocs'
	});

	return QualityDocModel;
});