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

	Gloria.module('WarehouseApp.Pick.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PickListGridView = Marionette.View.extend({

			initialize : function(options) {
				this.setGrid();
			},

			events : {
				'rowclicked table.backgrid tr' : 'rowClick'
			},

			rowClick : function(e, model) {
				Backbone.history.navigate('warehouse/pick/' + model.id, {
					trigger : true
				});
			},

			setGrid : function() {
				this.gridView = new Backgrid.Grid({
					row : ClickableRow,
					className : 'backgrid table table-striped',
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
							{
								name : 'code',
								label : i18n
										.t('Gloria.i18n.warehouse.pick.header.code'),
								cell : 'string',
								editable : false
							},
							{
								name : 'zoneId',
								label : i18n
										.t('Gloria.i18n.warehouse.pick.header.zone'),
								cell : 'string',
								editable : false
							},
							{
								name : 'items',
								label : i18n
										.t('Gloria.i18n.warehouse.pick.header.items'),
								cell : 'string',
								editable : false
							},
							{
								name : 'totalQty',
								label : i18n
										.t('Gloria.i18n.warehouse.pick.header.totalQty'),
								cell : 'string',
								editable : false
							} ]
				});
			},

		/*	buttonEvents : function() {
				return {
		
			},*/

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
				//Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			},

			onDestroy : function() {
				this.gridView.remove();
				this.paginator && this.paginator.remove();
			}
		});
	});

	return Gloria.WarehouseApp.Pick.View.PickListGridView;
});