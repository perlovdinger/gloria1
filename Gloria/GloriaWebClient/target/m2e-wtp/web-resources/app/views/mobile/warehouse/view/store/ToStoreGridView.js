define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'utils/backgrid/stringHeaderCell',
		'utils/backgrid/clickableRow',
		'bootstrap'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, StringHeaderCell, ClickableRow, Bootstrap) {

	Gloria.module('WarehouseApp.Store.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ToStoreGridView = Marionette.View.extend({

			initialize : function(options) {
				this.setGrid();
			},

			events : {
				'rowclicked table.backgrid tr' : 'rowClick'
			},

			rowClick : function(e, model) {
				Gloria.WarehouseApp.trigger('Store:ToStoreView:show', model);
			},

			setGrid : function() {
				this.gridView = new Backgrid.Grid({
					row : ClickableRow,
					className : 'backgrid table table-striped',
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
							{
								name : 'pPartNumber',
								label : i18n
										.t('Gloria.i18n.warehouse.store.partNo'),
								cell : 'string',
								editable : false
							},
							{
								name : 'pPartVersion',
								label : i18n
										.t('Gloria.i18n.warehouse.store.ver'),
								cell : 'string',
								editable : false
							},
							{
								name : 'suggestedBinLocation',
								label : i18n
										.t('Gloria.i18n.warehouse.store.suggestedBinLocation'),
								cell : 'string',
								editable : false
							} ]
				});
			},

			render : function() {
				var $gridView = this.gridView.render().$el;
				this.$el.html($gridView);
				return this;
			},

			onDestroy : function() {
				this.gridView.remove();
			}
		});
	});

	return Gloria.WarehouseApp.Store.View.ToStoreGridView;
});
