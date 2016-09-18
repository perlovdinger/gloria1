define([ 'utils/typeahead/TypeaheadView' ], function(TypeaheadView) {

	var WBSTypeaheadView = TypeaheadView.extend({
		
		key : 'wbs',

		url : function() {
			return '/common/v1/wbselements';
		},
		
		//cachePrefix : 'tpah.wbselements.',

		initialize : function(options) {
			options || (options = {});
			this.companyCode = options.companyCode;
			this.projectId = options.projectId;
			TypeaheadView.prototype.initialize.call(this, options);
		},

		resultMap : {
			id : 'wbs',
			text : 'wbs'
		},

		searchTerm : function(term) {
			var data = {};
			if (this.companyCode) {
				data['companyCode'] = this.companyCode;
				data['projectId'] = this.projectId;
				if (this.key) {
					data[this.key] = '*' + term;
				}
			}
			return data;
		}
	});

	return WBSTypeaheadView;
});