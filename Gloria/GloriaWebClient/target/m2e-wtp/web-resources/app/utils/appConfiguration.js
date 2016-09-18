define([
    'jquery',
    'models/PublicConfigurationModel'
], function($, PublicConfigurationModel) {
	
	var publicConfigurationModel = new PublicConfigurationModel();
	
	return publicConfigurationModel.fetch();	
});