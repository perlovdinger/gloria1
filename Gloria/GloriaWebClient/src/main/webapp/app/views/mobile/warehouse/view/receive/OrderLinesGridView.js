define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'utils/backgrid/stringHeaderCell',
		'utils/backgrid/clickableRow',
		'bootstrap',
		'views/mobile/common/paginator/PaginatorView'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, StringHeaderCell, ClickableRow, Bootstrap, GloriaPaginator) {

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.OrderLinesGridView = Marionette.View.extend({

			initialize : function(options) {
				this.module = options.module;
				this.listenTo(Gloria, 'scanner:scanned', this.onScan);
				this.setGrid();
			},

			onScan : function(options) {
				if (!options || !options.data)
					return;
				var deliveryNote;
				if (this.collection && (deliveryNote = this.collection.findWhere({partNumber : options.data}))) {
					this.gridSelect(null, deliveryNote);
				}
			},

			events : {
				'rowclicked table.backgrid tr' : 'gridSelect'
			},

			gridSelect : function(e, model) {
				Gloria.WarehouseApp.trigger('Receive:select:orderline', model.id);
			},

			setGrid : function() {
				this.gridView = new Backgrid.Grid({
					row : ClickableRow,
					className : 'backgrid table table-striped',
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
							{
								name : 'orderNo',
								label : i18n.t('Gloria.i18n.warehouse.receive.orderNo'),
								cell : 'string',
								renderable : this.module == 'regular',
								editable : false
							},
							{
								name : 'dispatchNoteNo',
								label : i18n.t('Gloria.i18n.warehouse.receive.deliveryNoteNo'),
								cell : 'string',
								renderable : this.module == 'transfer' || this.module == 'return' || this.module == 'returntransfer',
								editable : false
							},
							{
								name : 'supplierId',
								label : i18n.t('Gloria.i18n.warehouse.receive.supplierParmaID'),
								cell : 'string',
								renderable : this.module == 'regular',
								editable : false
							},
							{
								name : 'parmaID',
								label : i18n.t('Gloria.i18n.warehouse.receive.supplierParmaID'),
								cell : 'string',
								renderable : this.module == 'transfer' || this.module == 'return' || this.module == 'returntransfer',
								editable : false
							} ]
				});
			},

			render : function() {
				var $gridView = this.gridView.render().$el;
				this.$el.html($gridView);
				// If total pages more than 1; then show pagination
				if(this.collection.state.totalPages > 1) {
					// Initialize the paginator
					this.paginator = new GloriaPaginator({
						collection : this.collection,
						grid : this.gridView
					});
					// Render the paginator
					$gridView.append(this.paginator.render().$el);
				}
				return this;
			},

			onDestroy : function() {
				this.gridView.remove();
				this.paginator && this.paginator.remove();
			}
		});
	});

	return Gloria.WarehouseApp.Receive.View.OrderLinesGridView;
});
