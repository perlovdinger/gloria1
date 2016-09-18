define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

    var WarehouseModel = Model.extend({
        urlRoot : '/warehouse/v1/warehouses',
        defaults : {
            setUp: 'AISLE'
        }
    });

    return WarehouseModel;
});