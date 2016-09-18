define([ 
         'utils/backbone/GloriaCollection', 
         'models/DangerousGoodsModel' 
         ], function(Collection,DangerousGoodsModel) {

	var DangerousGoodsCollection = Collection.extend({

		model :DangerousGoodsModel,

		url : '/common/v1/dangerousgoods'
		    
	});

	return DangerousGoodsCollection;
});