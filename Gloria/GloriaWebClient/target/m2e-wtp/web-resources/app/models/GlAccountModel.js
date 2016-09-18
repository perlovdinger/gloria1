define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var GlAccountModel = Model.extend({

		urlRoot : '/procurement/v1/glaccounts'

	});

	return GlAccountModel;
});