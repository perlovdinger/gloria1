/**
 * Company Code Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var CompanyCodeSelector = BaseSelector.extend({
		
		resultMap : {
			id : 'id',
			text : 'id'
		},

		cachePrefix : 'gloria.selectors.companyCodes.',
			
		initialize : function(options) {
			options || (options = {});
			options.module = 'companycode';
			BaseSelector.prototype.initialize.call(this, options);
		},

		url : function() {
			return '/report/v1/companyCodes?userId=' + UserHelper.getInstance().getUserId();
		}
	});

	return CompanyCodeSelector;
});
