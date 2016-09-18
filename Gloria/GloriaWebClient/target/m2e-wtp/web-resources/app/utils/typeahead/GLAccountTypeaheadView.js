define([ 'utils/typeahead/TypeaheadView' ], function(TypeaheadView) {

	var GLAccountTypeaheadView = TypeaheadView.extend({

		key : 'accountName',

		url : function() {
			return '/common/v1/glaccounts';
		},

		// cachePrefix : 'tpah.glaccounts.',

		initialize : function(options) {
			options || (options = {});
			this.companyCode = options.companyCode;
			TypeaheadView.prototype.initialize.call(this, options);
		},

		resultMap : {
			id : 'accountNumber',
			text : '{{accountNumber}} - {{accountName}}'
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

	return GLAccountTypeaheadView;
});