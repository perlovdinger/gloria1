define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
        'utils/backbone/GloriaCollection',
        'utils/UserHelper'
], function(Backbone, Marionette, Handlebars, i18n, Collection, UserHelper) {
   
	var constructBinLocationList = function() {
    	var dropdownOptions = null;
        if (!dropdownOptions) {
            var collection = new Collection();
            collection.url = '/warehouse/v1/binlocations?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
            collection.fetch({
                data : {
                    'zoneType' : 'STORAGE'
                },
            	cache : false,
                async : false,
                success : function(data) {
                	dropdownOptions = JSON.stringify(data.toJSON());                    
                }
            });
        }
        return dropdownOptions;
    };
    
    return {
        'constructBinLocationList' : constructBinLocationList
    };
    
});