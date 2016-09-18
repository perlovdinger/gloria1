define([ 
    'utils/backbone/GloriaCollection',
    'models/DeliveryNoteLineModel' 
], function(Collection, DeliveryNoteLineModel) {
    
    var DeliveryNoteLineCollection = Collection.extend({
       
        model : DeliveryNoteLineModel
    });
    
    return DeliveryNoteLineCollection;
});