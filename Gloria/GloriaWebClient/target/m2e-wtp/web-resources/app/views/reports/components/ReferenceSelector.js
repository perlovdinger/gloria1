/**
 * Reference Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var ReferenceSelector = BaseSelector.extend({

		cachePrefix : 'gloria.selectors.references.',
		
		url : function() {
			return '/report/v1/references?userId=' + UserHelper.getInstance().getUserId();
		}
	});

	return ReferenceSelector;
});