define([
    'utils/backbone/GloriaCollection', 
    'models/GlAccountModel' 
    ], function(Collection, GlAccountModel) {

	var GlAccountCollection = Collection.extend({

		model : GlAccountModel,

		url : '/procurement/v1/glaccounts'

	});

	return GlAccountCollection;
});