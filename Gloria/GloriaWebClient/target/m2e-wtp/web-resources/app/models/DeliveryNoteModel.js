define([
    'utils/backbone/GloriaModel'
], function(Model) {
    
    var DeliveryNoteModel = Model.extend({
        
        urlRoot : '/material/v1/deliverynotes',

        isEditable: function() {
            return this.isNew();
        }        
    });
    
    return DeliveryNoteModel;
});