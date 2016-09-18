/**
 * WBS Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var WbsSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			BaseSelector.prototype.initialize.call(this, options);
		},
		
		cachePrefix : 'gloria.selectors.wbs.',

		url : function() {
			return '/report/v1/wbs?userId=' + UserHelper.getInstance().getUserId();
		}
	});

	return WbsSelector;
});