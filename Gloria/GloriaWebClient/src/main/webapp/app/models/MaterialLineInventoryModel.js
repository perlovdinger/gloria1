define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

    var MaterialLineInventoryModel = Model.extend({
        
        isScrapable: function() {
            return !!this.get('materialType') && this.get('materialType').toUpperCase() === 'RELEASED';
        }
    });

    return MaterialLineInventoryModel;
});