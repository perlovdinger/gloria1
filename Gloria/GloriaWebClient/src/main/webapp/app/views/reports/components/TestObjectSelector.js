/**
 * Test Object Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var TestObjectSelector = BaseSelector.extend({

		cachePrefix : 'gloria.selectors.testObjects.',
		
		url : function() {
			return '/report/v1/testObjects?userId=' + UserHelper.getInstance().getUserId();
		}
	});

	return TestObjectSelector;
});