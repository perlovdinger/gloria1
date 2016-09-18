define([ 'utils/typeahead/TypeaheadView' ], function(TypeaheadView) {

	var SAPTypeaheadView = TypeaheadView.extend({

		key : 'code',

		url : function() {
			return '/procurement/v1/internalordernosap';
		},

		cachePrefix : 'tpah.sap.',

		resultMap : {
			id : 'code',
			text : 'code'
		}

	});

	return SAPTypeaheadView;
});