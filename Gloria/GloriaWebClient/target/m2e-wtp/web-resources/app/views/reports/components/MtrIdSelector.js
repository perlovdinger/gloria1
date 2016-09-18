/**
 * MTR Id Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var MtrIdSelector = BaseSelector.extend({

		cachePrefix : 'gloria.selectors.mtrIds.',
		
		url : function() {
			return '/report/v1/mtrIds?userId=' + UserHelper.getInstance().getUserId();
		}
	});

	return MtrIdSelector;
});