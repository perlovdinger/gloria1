define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
        'utils/backgrid/clickableRow',
        'backgrid-select-all',
        'bootstrap',
        'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n,
		ClickableRow, BackgridSelectAll, Bootstrap, GridUtil) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ZoneGridView = Marionette.ItemView.extend({
			
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
                Gloria.WarehouseApp.trigger('ZoneGridView:select', selectedRows);
            },
            
			setGrid : function() {
                this.gridView = new Backgrid.Grid({
                	id : 'ZoneGrid',
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
							name : 'name',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.zone.name'),
							cell : 'string',
							editable : false,
							sortable: false
						},
						{
							name : 'code',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.zone.code'),
							cell : 'string',
							editable : false,
                            sortable: false
						},
						{
							name : 'type',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.zone.type'),
							cell : 'string',
							editable : false,
                            sortable: false
						},
						{
							name : 'description',
							label : i18n.t('Gloria.i18n.warehouse.setup.header.zone.description'),
							cell : 'string',
							editable : false,
                            sortable: false
						}]
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

	return Gloria.WarehouseApp.View.ZoneGridView;
});
