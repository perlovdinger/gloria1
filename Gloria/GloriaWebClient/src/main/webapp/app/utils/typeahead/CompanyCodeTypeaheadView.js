define([ 'utils/typeahead/TypeaheadView', 'utils/UserHelper' ], function(
		TypeaheadView, UserHelper) {

	var CompanyCodeTypeaheadView = TypeaheadView.extend({
		
		key: 'code',
		
		initialize : function(options) {
			options || (options = {});
			this.type = options.type;
			this.userId = options.userId === false ? false : true;
			TypeaheadView.prototype.initialize.call(this, options);
		},

		url : function() {
			var url = '/common/v1/companycodes?type=' + this.type;
			if(this.userId) {
				url = url.concat('&userId=' + UserHelper.getInstance().getUserId());
			}
			return url;
		},

		//cachePrefix : 'tpah.companycodes.',
		
		selectFirstOption : true,

		resultMap : {
			id : 'code',
			text : '{{code}} - {{name}}'
		},

		searchTerm : function(term) {
			var data = {};
			if (this.key) {
				data[this.key] = '*' + term;
			}
			return data;
		}
	});

	return CompanyCodeTypeaheadView;
});