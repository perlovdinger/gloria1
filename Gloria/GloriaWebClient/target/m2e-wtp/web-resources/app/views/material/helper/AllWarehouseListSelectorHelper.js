define(['backbone', 
        'marionette', 
        'handlebars', 
        'i18next',
        'utils/backbone/GloriaCollection'
], function(Backbone, Marionette, Handlebars, i18n, Collection) {
//Fetches all the warehouses from the rest api to render in views
    var constructAllWarehouseList = function() {
        var warehouseOptions;     
        var warehouseCollection = new Collection();
        warehouseCollection.url = '/warehouse/v1/warehouses';
        warehouseCollection.fetch({
            cache : false,
            async : false,
            success : function(data) {
                warehouseOptions = JSON.stringify(data.toJSON());                  
            }
        }); 
       
        return warehouseOptions;
    };
    
    return {
        'constructAllWarehouseList' : constructAllWarehouseList
    };
    
});