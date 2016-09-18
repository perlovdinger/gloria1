/**
 * Buyer Id Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var BuyerIdSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			BaseSelector.prototype.initialize.call(this, options);
		},
		
		cachePrefix : 'gloria.selectors.buyers.',
		
		resultMap : {
			id : 'id',
			text : 'id'
		},

		url : function() {
			return '/report/v1/buyers?userId=' + UserHelper.getInstance().getUserId();
		}
	});

	return BuyerIdSelector;
});