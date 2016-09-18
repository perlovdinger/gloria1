define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
        'utils/backbone/GloriaCollection'
], function(Backbone, Marionette, Handlebars, i18n, Collection) {

    var constructBuildLocationList = function() {
    	var options = undefined;
        var collection = new Collection();
        collection.url = '/common/v1/buildsites';
        collection.fetch({
        	async : false,
            success : function(data) {
                options = data.toJSON();
            }
        });
        return options;
    };
    
	return {
	    'constructBuildLocationList' : constructBuildLocationList
	};
	
});