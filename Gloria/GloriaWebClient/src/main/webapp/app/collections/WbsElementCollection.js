define([ 
    'utils/backbone/GloriaCollection', 
    'models/WbsElementModel' 
    ], function(Collection, WbsElementModel) {

	var WbsElementCollection = Collection.extend({

		model : WbsElementModel,

		url : '/procurement/v1/wbselements'

	});

	return WbsElementCollection;
});