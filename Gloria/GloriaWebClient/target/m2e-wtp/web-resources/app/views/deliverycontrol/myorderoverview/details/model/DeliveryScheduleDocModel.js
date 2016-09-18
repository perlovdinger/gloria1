define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var DeliveryScheduleDocModel = Model.extend({
		urlRoot : '/documents/v1/attacheddocs'
	});

	return DeliveryScheduleDocModel;
});