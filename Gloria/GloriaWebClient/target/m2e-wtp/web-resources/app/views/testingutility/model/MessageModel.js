define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var MessageModel = Model.extend({

		urlRoot : '/testingutility/v1/messages'

	});

	return MessageModel;
});