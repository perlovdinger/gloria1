define([ 'utils/typeahead/TypeaheadView' ], function(TypeaheadView) {

	var BuildSitesTypeaheadView = TypeaheadView.extend({

		key : 'siteId',

		url : function() {
			return '/common/v1/buildsites';
		},

		cachePrefix : 'tpah.buildsites.',

		resultMap : {
			id : 'siteId',
			text : '{{siteId}} - {{siteName}}'
		}

	});

	return BuildSitesTypeaheadView;
});