/**
 * Order Status Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'i18next' ], function(
		Gloria, BaseSelector, i18n) {

	var OrderStatusSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			options.defaultDataSet = this.getDefaultData();
			BaseSelector.prototype.initialize.call(this, options);
		},

		getDefaultData : function() {
			return [ {
				id : 'PLACED',
				text : i18n.t('Gloria.i18n.reports.orderStatus.PLACED')
			}, {
				id : 'RECEIVED_PARTLY',
				text : i18n.t('Gloria.i18n.reports.orderStatus.RECEIVED_PARTLY')
			}, {
				id : 'RECEIVED',
				text : i18n.t('Gloria.i18n.reports.orderStatus.RECEIVED')
			}, {
				id : 'CANCELLED',
				text : i18n.t('Gloria.i18n.reports.orderStatus.CANCELLED')
			}, {
				id : 'COMPLETE',
				text : i18n.t('Gloria.i18n.reports.orderStatus.COMPLETE')
			} ];
		}
	});

	return OrderStatusSelector;
});
