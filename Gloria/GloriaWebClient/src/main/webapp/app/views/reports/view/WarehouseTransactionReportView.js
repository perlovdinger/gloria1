/**
 * Warehouse Transaction Report View
 */
define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
        'backbone.syphon',
        'datepicker',
        'moment',
        'i18next',
		'hbs!views/reports/view/warehouse-transaction-report',
		'views/reports/components/WarehouseSelector',
		'views/reports/components/StorageRoomSelector'
], function(Gloria, $, _, Handlebars, Marionette, Syphon, Datepicker, moment, i18n, compiledTemplate,
		WarehouseSelector, StorageRoomSelector) {

	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.WarehouseTransactionReportView = Backbone.Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
			},
			
			regions : {
				warehouseSelectorContainer : 'div#warehouseSelectorContainer',
				storageRoomSelectorContainer : 'div#storageRoomSelectorContainer'
			},
			
			events : {
				'change #action' : 'handleActionChange'
			},
			
			actions : [{
				'ALL_RECEIVALS' : 'Gloria.i18n.reports.text.actionType.ALL_RECEIVALS'
			}, {
				'ALL_STORES' : 'Gloria.i18n.reports.text.actionType.ALL_STORES'
			}, {
				'ALL_PICKS' : 'Gloria.i18n.reports.text.actionType.ALL_PICKS'
			}, {
				'ALL_SHIPMENTS' : 'Gloria.i18n.reports.text.actionType.ALL_SHIPMENTS'
			}, {
				'ALL_RETURNS' : 'Gloria.i18n.reports.text.actionType.ALL_RETURNS'
			}],
			
			// Warehouse Code Selector
			warehouseSelector : function() {
				this.warehouseSelectorContainer.show(new WarehouseSelector({
					el : this.$('#warehouse')
				}));
			},
			
			// Storage Room Selector
			storageRoomSelector : function() {
				var formData = Backbone.Syphon.serialize(this);
				this.storageRoomSelectorContainer.show(new StorageRoomSelector({
					el : this.$('#storageRoom'),
					warehouse : formData.dropdown.warehouse
				}));
			},
			
			handleActionChange : function(e) {
				var selectedOption = e.currentTarget.value;
				if(selectedOption == 'ALL_STORES') {
					this.$el.find('.storage').show();
				} else {
					this.$el.find('.storage').hide();
				}
				this.$el.find('#fromDateHints').text(i18n.t('Gloria.i18n.reports.text.' + selectedOption + '_FromDateText'));
				this.$el.find('#toDateHints').text(i18n.t('Gloria.i18n.reports.text.' + selectedOption + '_ToDateText'));
			},
			
			populate : function() {
				this.warehouseSelector();
				this.storageRoomSelector();
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					actions : this.actions
				}));
				this.$('.date').datepicker();
				return this;
			},
			
			onShow : function() {
				this.populate();
			},
			
			onDestroy : function() {
				this.$('.date').datepicker('remove');
			}
		});
	});

	return Gloria.ReportsApp.View.WarehouseTransactionReportView;
});
