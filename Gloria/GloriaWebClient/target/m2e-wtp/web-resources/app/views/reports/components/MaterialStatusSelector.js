/**
 * Material Status Selector
 */
define([ 'app', 'views/reports/components/BaseSelector', 'i18next' ],
		function(Gloria, BaseSelector, i18n) {

	var MaterialStatusSelector = BaseSelector.extend({

		initialize : function(options) {
			options || (options = {});
			options.defaultDataSet = this.getDefaultData();
			BaseSelector.prototype.initialize.call(this, options);
		},

		getDefaultData : function() {
			return [{
					id : 'CREATED',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.CREATED')
				},
				{
					id : 'WAIT_TO_PROCURE',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.WAIT_TO_PROCURE')
				},
				{
					id : 'REQUISITION_SENT',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.REQUISITION_SENT')
				},
				{
					id : 'ORDER_PLACED_INTERNAL',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.ORDER_PLACED_INTERNAL')
				},
				{
					id : 'ORDER_PLACED_EXTERNAL',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.ORDER_PLACED_EXTERNAL')
				},
				{
					id : 'QI_READY',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.QI_READY')
				},
				{
					id : 'BLOCKED',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.BLOCKED')
				},
				{
					id : 'MARKED_INSPECTION',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.MARKED_INSPECTION')
				},
				{
					id : 'READY_TO_STORE',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.READY_TO_STORE')
				},
				{
					id : 'STORED',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.STORED')
				},
				{
					id : 'REQUESTED',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.REQUESTED')
				},
				{
					id : 'DEVIATED',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.DEVIATED')
				},
				{
					id : 'MISSING',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.MISSING')
				},
				{
					id : 'READY_TO_SHIP',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.READY_TO_SHIP')
				},
				{
					id : 'SHIPPED',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.SHIPPED')
				},
				{
					id : 'IN_TRANSFER',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.IN_TRANSFER')
				},
				{
					id : 'SCRAPPED',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.SCRAPPED')
				},
				{
					id : 'REMOVED',
					text : i18n.t('Gloria.i18n.reports.materialLineStatus.REMOVED')
				} ];
		}
	});

	return MaterialStatusSelector;
});