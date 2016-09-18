define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
        'utils/backbone/GloriaCollection',
        'utils/UserHelper'
], function(Backbone, Marionette, Handlebars, i18n, Collection, UserHelper) {

    var constructWarehouseSiteList = function() {
    	var options = undefined;
    	var user = UserHelper.getInstance().getUser();
		var whSite = UserHelper.getInstance().getDefaultWarehouse();
        var collection = new Collection();
        collection.url = '/warehouse/v1/warehouses';
        collection.fetch({
        	async : false,
            success : function(data) {
                options = data.toJSON();
            }
        });
        return options;
    };
    
	return {
	    'constructWarehouseSiteList' : constructWarehouseSiteList
	};
	
});