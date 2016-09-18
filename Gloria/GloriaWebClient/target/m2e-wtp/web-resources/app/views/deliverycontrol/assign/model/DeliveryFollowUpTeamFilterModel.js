define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

	var DeliveryFollowUpTeamFilterModel = Model.extend({
		urlRoot : '/common/v1/deliveryfollowupteams'
	});

	return DeliveryFollowUpTeamFilterModel;
});
