define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var DeliveryFollowUpTeamModel = Model.extend({
		urlRoot : '/common/v1/deliveryfollowupteams'
	});

	return DeliveryFollowUpTeamModel;
});
