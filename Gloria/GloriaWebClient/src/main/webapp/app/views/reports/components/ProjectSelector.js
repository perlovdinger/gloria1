/**
 * Project Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var ProjectSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			this.listenTo(Gloria.ReportsApp,'select2-selected-companycode', this.handleCompanyCodeChange);
			BaseSelector.prototype.initialize.call(this, options);
		},
		
		cachePrefix: function() {
			return 'gloria.selectors.projects.' + (this.companyCode ? this.companyCode : 'all') + '.' ;
		},

		url : function() {
			return '/report/v1/projects?userId=' + UserHelper.getInstance().getUserId()
					+ '&companyCode=' + (this.companyCode || '');
		},

		handleCompanyCodeChange : function(options) {
			this.companyCode = options.value;
			this.refresh();
		}
	});

	return ProjectSelector;
});