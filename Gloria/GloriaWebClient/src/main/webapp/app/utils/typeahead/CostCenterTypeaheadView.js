define([ 'utils/typeahead/TypeaheadView' ], function(TypeaheadView) {

	var CostCenterTypeaheadView = TypeaheadView.extend({
		
		key : 'costCenter',

		url : function() {
			return '/common/v1/costcenters';
		},
		
		//cachePrefix : 'tpah.costcenters.',

		initialize : function(options) {
			options || (options = {});
			this.companyCode = options.companyCode;
			TypeaheadView.prototype.initialize.call(this, options);
		},

		resultMap : {
			id : 'costCenter',
			text : 'costCenter'
		},

		searchTerm : function(term) {
			var data = {};
			if (this.companyCode) {
				data['companyCode'] = this.companyCode;
				if (this.key) {
					data[this.key] = '*' + term;
				}
			}
			return data;
		}
	});

	return CostCenterTypeaheadView;
});