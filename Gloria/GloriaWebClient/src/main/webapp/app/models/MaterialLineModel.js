define([ 
         'utils/backbone/GloriaModel',
         'utils/UserHelper'
         ], function(Model, UserHelper) {

	var MaterialLineModel = Model.extend({
		urlRoot : function() {
		    return '/procurement/v1/materiallines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
		},
		
		validate: function(attrs, options) {
		    var pickedQuantity = attrs.pickedQuantity && Number(attrs.pickedQuantity);
		    if (pickedQuantity > this.getPullQuantity()) {
		      return new RangeError('ActualPickedQty is more than PullQuantity');
		    }
		    
		    if(pickedQuantity < 0){
		      return new RangeError('ActualPickedQty cannot be a neagtive value');
		    }
		},
		
		getPullQuantity: function() {
		    var quantity = this.get('quantity') || 0;
		    var additionalQuantity = this.get('additionalQuantity') || 0;
		    return quantity + additionalQuantity;
		}
	});

	return MaterialLineModel;
});