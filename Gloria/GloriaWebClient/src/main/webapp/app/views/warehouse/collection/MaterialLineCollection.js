define(['utils/backbone/GloriaPageableCollection', 
        'views/warehouse/models/MaterialLineModel',
        'utils/UserHelper'
        ], function(PageableCollection, MaterialLineModel, UserHelper) {

	var MaterialLineCollection = PageableCollection.extend({
		model : MaterialLineModel,
		url : function() {
		    return '/warehouse/v1/materiallines?whSiteId=' + UserHelper.getInstance().getDefaultWarehouse(); //OK
		},
		
		sumOfAttributeByCondition: function(attrName, condition) {
		    condition = (typeof condition === 'function' ? condition() : condition);
		    return _.reduce(this.where(condition), function(memo, model){ 
		        return memo + Number(model.get(attrName)); 
		    }, 0);
		},
		
		sumOfScrappableQuanity: function() {
		    return this.sumOfAttributeByCondition('quantity', {materialType: 'RELEASED'});		    
		},
		
		getTotalNumber: function(attr) {
            return this.reduce(function(memo, value) { 
                return memo + (Number(value.get(attr)) || 0);
            }, 0);
        }
	});

	return MaterialLineCollection;
});