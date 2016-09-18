/**
 * Source Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'i18next' ],
		function(Gloria, BaseSelector, i18n) {

	var SourceSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			options.defaultDataSet = this.getDefaultData();
			BaseSelector.prototype.initialize.call(this, options);
		},

		getDefaultData : function() {
			return [ {
				id : 'INTERNAL',
				text : i18n.t('Gloria.i18n.reports.sourceType.INTERNAL')
			}, {
				id : 'EXTERNAL',
				text : i18n.t('Gloria.i18n.reports.sourceType.EXTERNAL')
			}];
		}
	});

	return SourceSelector;
});