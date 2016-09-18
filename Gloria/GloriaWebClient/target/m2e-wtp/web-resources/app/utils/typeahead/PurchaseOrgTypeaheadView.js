define([ 'utils/typeahead/TypeaheadView' ], function(TypeaheadView) {

	var PurchaseOrgTypeaheadView = TypeaheadView.extend({
		
		key : 'organisationCode',

		url : function() {
			return '/procurement/v1/purchaseorganization';
		},

		cachePrefix : 'tpah.purchaseorganization.',

		initialize : function(options) {
			options || (options = {});
		},

		resultMap : {
			id : 'organisationCode',
			text : 'organisationName'
		}
	});

	return PurchaseOrgTypeaheadView;
});