define([ 
    'utils/backbone/GloriaCollection', 
    'models/QualityDocModel' 
    ], function(Collection, QualityDocModel) {
    
	var QualityDocCollection = Collection.extend({
		url : '/v1/receivedocs',
		model : QualityDocModel
	});
	return QualityDocCollection;
});
