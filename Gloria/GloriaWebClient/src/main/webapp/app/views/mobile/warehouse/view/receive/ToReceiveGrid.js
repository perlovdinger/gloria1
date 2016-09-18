define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'utils/backgrid/stringHeaderCell',
		'utils/backgrid/clickableRow', 'bootstrap'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, StringHeaderCell, ClickableRow, Bootstrap) {

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ToReceiveGrid = Marionette.View.extend({

			initialize : function(options) {
				this.module = options.module;
				this.collection = options.collection;
				this.listenTo(Gloria, 'scanner:scanned', this.onScan);
				this.setGrid();
			},

			onScan : function(options) {
				if (!options || !options.data)
					return;
				var deliveryNote;
				if (this.collection	&& (deliveryNote = this.collection.findWhere({partNumber : options.data}))) {
					this.toReceive(null, deliveryNote);
				}
			},

			events : {
				'rowclicked table.backgrid tr' : 'toReceive'
			},

			toReceive : function(e, model) {
				if (this.module == 'regular') {
					Gloria.WarehouseApp.trigger('Receive:QuantityView:show', model.id);
				} else {
					Gloria.WarehouseApp.trigger('Receive:parts:select', model);
				}
			},

			setGrid : function() {
				this.gridView = new Backgrid.Grid({
					row : ClickableRow,
					className : 'backgrid table table-striped',
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
							{
								name : 'partNumber',
								label : i18n.t('Gloria.i18n.warehouse.receive.partNo'),
								cell : 'string',
								editable : false
							},
							{
								name : 'partAlias',
								label : i18n.t('Gloria.i18n.warehouse.receive.partAlias'),
								cell : 'string',
								editable : false
							},
							{
								name : 'partVersion',
								label : i18n.t('Gloria.i18n.warehouse.receive.version'),
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

	return Gloria.WarehouseApp.Receive.View.ToReceiveGrid;
});