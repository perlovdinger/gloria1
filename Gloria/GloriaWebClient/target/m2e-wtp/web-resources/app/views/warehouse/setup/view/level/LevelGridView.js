define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
        'utils/backgrid/clickableRow',
        'utils/backgrid/IntegerCell',
        'utils/backgrid/StringCell',
        'backgrid-select-all',
        'bootstrap',
        'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, ClickableRow, IntegerCell, StringCell,
        BackgridSelectAll, Bootstrap, GridUtil) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.LevelGridView = Marionette.ItemView.extend({
			
			initialize : function(options) {
				this.collection = options.collection;
				this.setGrid();
			},
    
			events : {
				'change .select-row-cell input' : 'handleSelectRow',
                'change .select-all-header-cell input' : 'handleSelectRow'
			},
			
			handleSelectRow : function(e, model) {                
                var selectedModels = this.gridView.getSelectedModels();
                var selectedRows = _.map(selectedModels, function(model) {
                    return model.id;
                });
                Gloria.WarehouseApp.trigger('LevelGridView:select', selectedRows);
            },
            
			setGrid : function() {
                this.gridView = new Backgrid.Grid({
                	id : 'LevelGrid',
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
							name : 'bayCode',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.level.code'),
							sortable : false,
							cell : StringCell,
						},
						{
							name : 'numberOfLevels',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.level.bay'),
							sortable : false,
							cell :  IntegerCell,              
						},
						{
							name : 'numberOfPositions',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.level.level'),
							sortable : false,
							cell :  IntegerCell,              
						} ]
                });
            },

			render : function() {
                var $gridView = this.gridView.render().$el;
                this.$el.html($gridView);
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
			}
		});
	});

	return Gloria.WarehouseApp.View.LevelGridView;
});
