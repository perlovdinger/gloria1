/**
 * Bin Location Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var BinLocationSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			this.listenTo(Gloria.ReportsApp,'select2-selected-warehouse', this.handleWarehouseChange);
			BaseSelector.prototype.initialize.call(this, options);
		},
		
		cachePrefix: function() {
			return 'gloria.selectors.binlocations.' + (this.whSiteId ? this.whSiteId : 'all') + '.' ;
		},

		url : function() {
			return '/report/v1/binlocations?userId=' + UserHelper.getInstance().getUserId()
					+ '&whSiteId=' + (this.whSiteId || '');
		},
		
		handleWarehouseChange : function(options) {
			this.whSiteId = options.value;
			this.refresh();
		}
	});

	return BinLocationSelector;
});