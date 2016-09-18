define(['utils/backbone/GloriaPageableCollection',
		'views/deliverycontrol/assign/model/DeliveryFollowUpTeamFilterModel'
],function(PageableCollection, DeliveryFollowUpTeamFilterModel) {

	var DeliveryFollowUpTeamFilterCollection = PageableCollection.extend({
	    
		model : DeliveryFollowUpTeamFilterModel,
		
		url : '/common/v1/deliveryfollowupteams'
		    
	});

	return DeliveryFollowUpTeamFilterCollection;
});
