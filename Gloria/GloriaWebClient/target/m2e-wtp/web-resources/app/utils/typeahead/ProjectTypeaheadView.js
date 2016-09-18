define([ 'utils/typeahead/TypeaheadView' ], function(TypeaheadView) {

	var ProjectTypeaheadView = TypeaheadView.extend({
		
		key : 'projectId',

		url : function() {
			return '/common/v1/companycodes/' + this.companyCode + '/projects';
		},

		//cachePrefix : 'tpah.projects.',

		initialize : function(options) {
			options || (options = {});
			this.companyCode = options.companyCode || null;
			TypeaheadView.prototype.initialize.call(this, options);
		},

		resultMap : {
			id : 'projectId',
			text : 'projectId'
		},

		select2InitSelect : function(element, callback) {
			var id = element.val();
			if (id !== "") {
				callback({
					id : id,
					text : id
				});
			}
		},

		searchTerm : function(term) {
			var data = {};
			if (this.companyCode) {				
				if (this.key) {
					data[this.key] = '*' + term;
				}
			}
			return data;
		}
	});

	return ProjectTypeaheadView;
});