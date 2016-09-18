define([ 'utils/typeahead/TypeaheadView' ], function(TypeaheadView) {

	var ProjectTypeaheadView = TypeaheadView.extend({
		
		key : 'projectId',

		url : function() {
			return '/common/v1/projects?teamName=' + this.teamName + '&teamType=' + this.teamType;
		},

		//cachePrefix : 'tpah.projects.',

		initialize : function(options) {
			options || (options = {});
			this.teamName = options.teamName;
			this.teamType = options.teamType;
		},

		resultMap : {
			id : 'projectId',
			text : 'projectId'
		},

		select2InitSelect : function(element, callback) {
			var id = element.val();
			if (id != '') {
				callback({
					id : id,
					text : id
				});
			}
		},

		searchTerm : function(term) {
			var data = {};
			if (this.key) {
				data[this.key] = '*' + term;
			}
			return data;
		}
	});

	return ProjectTypeaheadView;
});