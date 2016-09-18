define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'views/common/paginator/PaginatorView',
        'utils/backgrid/clickableRow',
        'utils/backgrid/IntegerCell',
        'utils/backgrid/StringCell',
        'backgrid-select-all',
        'bootstrap',
        'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, GloriaPaginator,
		ClickableRow, IntegerCell, StringCell, BackgridSelectAll, Bootstrap, GridUtil) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.RackGridView = Marionette.ItemView.extend({
			
			initialize : function(options) {
				this.collection = options.collection;
				this.setGrid();
			},
    
			events : {
				'change .select-row-cell input' : 'handleSelectRow',
                'change .select-all-header-cell input' : 'handleSelectRow'
			},
			
			handleSelectRow : function(e, model) {                
                var selectedModels = this.gridView.getAllSelectedModelIds();
                var selectedRows = _.map(selectedModels, function(model) {
                    return model.id;
                });
                Gloria.WarehouseApp.trigger('RackGridView:select', selectedRows);
            },
            
			setGrid : function() {
                this.gridView = new Backgrid.Grid({
                	id : 'RackGrid',
                    row : ClickableRow,
                    className: 'backgrid',
                    collection : this.collection,
                    emptyText : i18n.t('Gloria.i18n.general.noRow'),
                    columns : [{
							name : "",
							cell : "select-row",
							headerCell : "select-all",
						},
						{
							name : 'code',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.rack.code'),
							cell : StringCell
						},
						{
							name : 'bay',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.rack.bay'),
							cell :  IntegerCell,              
			                editable : true
						},
						{
							name : 'level',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.rack.level'),
							cell :  IntegerCell,              
			                editable : true
						},
						{
							name : 'position',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.rack.position'),
							cell :  IntegerCell,              
			                editable : true
						} ]
                });
                
                // Initialize the paginator
                this.paginator = new GloriaPaginator({
                    collection : this.collection,
                    grid : this.gridView
                });
            },

			render : function() {
                var $gridView = this.gridView.render().$el;
                this.$el.html($gridView);
                $gridView.after(this.paginator.render().$el);
				return this;
			},
			
			onShow : function() {
	        	this.gridView.enableGridColumnManipulation({
	        		grid : this.gridView,
					resizable : {
						postbackSafe : true
					}
				});
			},
			
			onDestroy : function() {
				this.gridView.remove();
				this.paginator.remove();			
			}
		});
	});

	return Gloria.WarehouseApp.View.RackGridView;
});
