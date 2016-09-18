/**
 * Material Status Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'i18next' ],
		function(Gloria, BaseSelector, i18n) {

	var MaterialTypeSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			options.defaultDataSet = this.getDefaultData();
			BaseSelector.prototype.initialize.call(this, options);
		},

		getDefaultData : function() {
			return [{
					id : 'ADDITIONAL',
					text : i18n.t('Gloria.i18n.reports.materialType.ADDITIONAL')
				},{
					id : 'RELEASED',
					text : i18n.t('Gloria.i18n.reports.materialType.RELEASED')
				},{
					id : 'USAGE',
					text : i18n.t('Gloria.i18n.reports.materialType.USAGE')
				},
				{
                    id : 'USAGE_REPLACED',
                    text : i18n.t('Gloria.i18n.reports.materialType.USAGE_REPLACED')
                }];
		}
	});

	return MaterialTypeSelector;
});