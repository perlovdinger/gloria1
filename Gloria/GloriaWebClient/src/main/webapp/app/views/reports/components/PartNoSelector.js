/**
 * Part Number Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var PartNoSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			BaseSelector.prototype.initialize.call(this, options);
		},
		
		cachePrefix : 'gloria.selectors.parts.',

		url : function() {
			return '/report/v1/parts?userId=' + UserHelper.getInstance().getUserId();
		}
	});

	return PartNoSelector;
});