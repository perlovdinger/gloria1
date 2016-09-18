/**
 * Suffix Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var SuffixSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			options.module = 'suffix';
			BaseSelector.prototype.initialize.call(this, options);
		},
		
		cachePrefix : 'gloria.selectors.suffix.',

		url : function() {
			return '/report/v1/suffix?userId=' + UserHelper.getInstance().getUserId();
		}
	});

	return SuffixSelector;
});