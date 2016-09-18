define(['utils/backbone/GloriaPageableCollection', 
        'models/MaterialLineModel',
        'utils/UserHelper'
        ], function(PageableCollection, MaterialLineModel, UserHelper) {

	var MaterialLineCollection = PageableCollection.extend({
		model : MaterialLineModel,
		url : function() {
		    return '/procurement/v1/materiallines';
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