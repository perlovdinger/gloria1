define(['utils/backbone/GloriaCollection',
        'utils/backbone/GloriaModel'
], function(Collection, Model) {

    var DeliveryNoteLineModel = Model.extend({
    	
    	initialize: function() {
            this.directsends = new Collection();
        },

        urlRoot : '/material/v1/deliverynotelines',
        
        parse: function(resp, options) {			
			if(resp && resp.receivedQuantity === 0) {
				resp.receivedQuantity = null;
			}
		    return resp;
		},
        
//        validate: function(attrs, options) {
//            var possibleToReceiveQuantity = attrs.possibleToReceiveQuantity || this.get('possibleToReceiveQuantity') || 0;
//            var orderLineReceivedQuantity = attrs.orderLineReceivedQuantity || this.get('orderLineReceivedQuantity') || 0; 
//            if(attrs.receivedQuantity && 
//                    (parseInt(attrs.receivedQuantity) < 0 || ( parseInt(attrs.receivedQuantity)+  parseInt(orderLineReceivedQuantity)) > parseInt(possibleToReceiveQuantity))
//            ) {                
//                return {
//                    attr: 'receivedQuantity',
//                    message: 'Gloria.i18n.warehouse.receive.validation.receivedQuantityNotValid'
//                };                
//            }
//        }
    });
    
    return DeliveryNoteLineModel;
}); 