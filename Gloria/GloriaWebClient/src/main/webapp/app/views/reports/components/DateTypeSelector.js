/**
 * Material Status Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'i18next' ],
		function(Gloria, BaseSelector, i18n) {

	var DateTypeSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			options.defaultDataSet = this.getDefaultData();
			options.noDefaultData = true;
			options.filter = this.filter;
			BaseSelector.prototype.initialize.call(this, options);
		},
		
		getDefaultData : function() {
			return [{
					id : 'PPREQ',
					text : i18n.t('Gloria.i18n.reports.text.dateType.PPREQ')
				},
				{
					id : 'ORDERSTA',
					text : i18n.t('Gloria.i18n.reports.text.dateType.ORDERSTA')
				},
				{
					id : 'AGREEDSTA',
					text : i18n.t('Gloria.i18n.reports.text.dateType.AGREEDSTA')
				},
				{
					id : 'RECEIVED',
					text : i18n.t('Gloria.i18n.reports.text.dateType.RECEIVED')
				},
				{
					id : 'REQUEST',
					text : i18n.t('Gloria.i18n.reports.text.dateType.REQUEST')
				}];
		},
		
		filter : function(data, selection) {
			if(selection && selection.length > 0) {
				var thisData = [];
				var isORDERSTA = _.any(selection, function(value, key) {
					return value == 'ORDERSTA';
				});
				var isAGREEDSTA = _.any(selection, function(value, key) {
					return value == 'AGREEDSTA';
				});
				if(isORDERSTA && isAGREEDSTA) {
					thisData = [];
				} else if(isORDERSTA) {
					thisData = [{
						id : 'AGREEDSTA',
						text : i18n.t('Gloria.i18n.reports.text.dateType.AGREEDSTA')
					}];
				} else if(isAGREEDSTA) {
					thisData = [{
						id : 'ORDERSTA',
						text : i18n.t('Gloria.i18n.reports.text.dateType.ORDERSTA')
					}];
				}
				return thisData;
			} else {
				return data;
			}
		}
	});

	return DateTypeSelector;
});