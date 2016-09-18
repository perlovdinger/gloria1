/**
 * Supplier Parma Id Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'utils/UserHelper' ],
		function(Gloria, BaseSelector, UserHelper) {

	var SupplierParmaIdSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			options.module = 'supplierparma';
			this.listenTo(Gloria.ReportsApp, 'select2-selected-companycode', this.handleCompanyCodeChange);
			this.listenTo(Gloria.ReportsApp, 'select2-selected-suffix', this.handleSuffixChange);
			BaseSelector.prototype.initialize.call(this, options);
		},
		
		cachePrefix: function() {
			return 'gloria.selectors.supplierParmaIds.' + (this.companyCode ? this.companyCode : 'all') + '.' + (this.suffix ? this.suffix : 'all') + '.';
		},
		
		url : function() {
			return '/report/v1/supplierParmaIds?userId=' + UserHelper.getInstance().getUserId()
				+ '&companyCode=' + (this.companyCode || '') + '&suffix=' + (this.suffix || '');
		},
		
		handleCompanyCodeChange : function(options) {
			this.companyCode = options.value;
			this.refresh();
			Gloria.ReportsApp.trigger('select2-selected-supplierparma:refresh');
		},

		handleSuffixChange : function(options) {
			this.suffix = options.value;
			this.refresh();
			Gloria.ReportsApp.trigger('select2-selected-supplierparma:refresh');
		}
	});

	return SupplierParmaIdSelector;
});