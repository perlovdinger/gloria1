define(['utils/backbone/GloriaCollection', 
        'models/MaterialLineModel',
        'utils/UserHelper'
        ], function(Collection, MaterialLineModel, UserHelper) {

	var MaterialLineCollection = Collection.extend({
		model : MaterialLineModel,
		url : function() {
		    return '/procurement/v1/materiallines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse();
		},
		
		sumOfAttributeByCondition: function(attrName, condition) {
		    condition = (typeof condition === 'function' ? condition() : condition);
		    return _.reduce(this.where(condition), function(memo, model){ 
		        return memo + Number(model.get(attrName)); 
		    }, 0);
		},
		
		sumOfScrappableQuanity: function() {
		    return this.sumOfAttributeByCondition('quantity', {materialType: 'RELEASED'});		    
		}
	});

	return MaterialLineCollection;
});