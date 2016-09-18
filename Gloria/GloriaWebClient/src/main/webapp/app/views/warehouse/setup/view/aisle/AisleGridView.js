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
        'views/warehouse/setup/view/aisle/AisleRadioButtonCell',
        'bootstrap',
        'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, 
		ClickableRow, IntegerCell, StringCell, BackgridSelectAll, AisleRadioButtonCell, Bootstrap, GridUtil) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AisleGridView = Marionette.ItemView.extend({
			
			initialize : function(options) {
			    this.warehouseModel = options.warehouseModel;
				this.collection = options.collection;
				this.setGrid();
			},
			
			renderable: function() {
			    return (this.warehouseModel && this.warehouseModel.get('setUp').toUpperCase() == 'AISLE'); 
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
                Gloria.WarehouseApp.trigger('AisleGridView:select', selectedRows);
            },
            
			setGrid : function() {
			    var context = {context: this.warehouseModel.get('setUp').toUpperCase()};
                this.gridView = new Backgrid.Grid({
                	id : 'AisleGrid',
                    row : ClickableRow,
                    className: 'backgrid',
                    collection : this.collection,
                    emptyText : i18n.t('Gloria.i18n.general.noRow'),
                    columns : [{
							name : "",
							cell : "select-row",
							headerCell : "select-all"
						},
						{
							name : 'code',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.aisle.code', context),
							cell : StringCell,
							sortable: false,
						},
						{
							name : 'numberOfBays',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.aisle.numberOfBays', context),
							cell :  IntegerCell,              
                            sortable: false
						},
						{
							name : 'baySidesLEFT',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.aisle.baySideLEFT'),
							cell : function(options) {
								return new AisleRadioButtonCell(options);
							},
							editable : false,
                            sortable: false,
                            renderable: this.renderable()
						},
						{
							name : 'baySidesRIGHT',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.aisle.baySideRIGHT'),
							cell : function(options) {
								return new AisleRadioButtonCell(options);
							},
							editable : false,
                            sortable: false,
                            renderable: this.renderable()
						},
						{
							name : 'baySidesBOTH',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.aisle.baySideBOTH'),
							cell : function(options) {
								return new AisleRadioButtonCell(options);
							},
							editable : false,
                            sortable: false,
                            renderable: this.renderable()
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

	return Gloria.WarehouseApp.View.AisleGridView;
});
