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

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ProjectGridView = Marionette.View.extend({

			initialize : function(options) {
				this.setGrid();
				this.module = options.module;
			},

			events : {
				'rowclicked table.backgrid tr' : 'gridSelect'
			},

			gridSelect : function(e, model) {
				Gloria.WarehouseApp.trigger('Receive:QuantityView:show', model.id);
			},

			setGrid : function() {
				var that = this;
				this.gridView = new Backgrid.Grid({
					row : ClickableRow,
					className : 'backgrid table table-striped',
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
							{
								name : 'projectId',
								label : i18n.t('Gloria.i18n.warehouse.receive.projectId'),
								cell : 'string',
								editable : false
							},
							{
								name : 'referenceId',
								label : i18n.t('Gloria.i18n.warehouse.receive.referenceId'),
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

	return Gloria.WarehouseApp.Receive.View.ProjectGridView;
});