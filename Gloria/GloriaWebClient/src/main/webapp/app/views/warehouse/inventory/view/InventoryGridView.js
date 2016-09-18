define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'backgrid',
	    'i18next',
	    'views/common/paginator/PaginatorView',
	    'utils/backgrid/dateHeaderCell',
	    'utils/backgrid/integerHeaderCell',
	    'utils/backgrid/stringHeaderCell',
	    'utils/backgrid/dateFormatter',
	    'utils/backgrid/clickableRow',
	    'utils/backgrid/DeviationCell',
	    'utils/backgrid/DeviationHeaderCell',
	    'backgrid-select-all',
	    'backgrid-select2-cell',
	    'bootstrap',
	    'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, DateHeaderCell, IntegerHeaderCell, StringHeaderCell, 
		BackgridDateFormatter, ClickableRow, DeviationCell, DeviationHeaderCell, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.InventoryGridView = Marionette.View.extend({
	
			initialize : function(options) {
	            this.collection = options.collection;
	            this.listenTo(this.collection, 'add remove backgrid:selected', this.handleSelectRow);
	            this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
	            this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
	        },
	        
	        clearFilter : function() {
	        	this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
	
	        handleSelectRow : _.debounce(function(e) {
	        	var selectedModels = this.gridView.getSelectedModels();
	            Gloria.WarehouseApp.trigger('Inventory:select', selectedModels);
	        }, 200),
	        
	        clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
	        render : function() {	
	            // Initialize the grid
	            this.gridView = new Backgrid.Grid({
	            	id : 'InventoryGrid',
	                row : ClickableRow,
	                collection : this.collection,
	                emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
		                    name: "",
		                    cell: "select-row",
		                    headerCell: "select-all",
		                },{
		        	    	name : 'deviation',
							label : i18n.t('Gloria.i18n.warehouse.inventory.header.deviation'),
							cell : DeviationCell,
							editable : false,						
							headerCell : DeviationHeaderCell,
			                sortable: false
		        	    }, {
		                    name : 'partNumber',
		                    label : i18n.t('Gloria.i18n.warehouse.inventory.header.pPartNumber'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    headerCell : function(options) {
		                        options.tooltip = {
		                            'tooltipText': i18n.t('Gloria.i18n.warehouse.inventory.header.pPartNumberTooltip'),
		                            'displayText': i18n.t('Gloria.i18n.warehouse.inventory.header.pPartNumber')
		                        };                    
		                        return new StringHeaderCell(options);
		                    }
		                }, {
		                    name : 'partVersion',
		                    label : i18n.t('Gloria.i18n.warehouse.inventory.header.pPartVersion'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    headerCell : StringHeaderCell
		                }, {
		                    name : 'partName',
		                    label : i18n.t('Gloria.i18n.warehouse.inventory.header.pPartName'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    headerCell : StringHeaderCell
		                } ,{
                            name : 'partModification',
                            label : i18n.t('Gloria.i18n.warehouse.inventory.header.partModification'),
                            cell : 'string',
                            sortable : false,
                            editable : false,
                            headerCell : StringHeaderCell
                        }, {
		                    name : 'code',
		                    label : i18n.t('Gloria.i18n.warehouse.inventory.header.code'),
		                    cell : 'string',
		                    sortable : false,
		                    editable : false,
		                    headerCell : StringHeaderCell
		                }, {
		                    name : 'quantity',
		                    label : i18n.t('Gloria.i18n.warehouse.inventory.header.stockBalance'),
		                    cell : 'integer',
		                    sortable : false,
		                    editable : false,
		                    headerCell : function(options) {
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
		                }, {
		                    name : 'scrapExpirationDate',
		                    label : i18n.t('Gloria.i18n.warehouse.inventory.header.scrapDate'),
		                    cell : Backgrid.Cell.extend({
								render : function() {
					                Backgrid.Cell.prototype.render.apply(this, arguments);
					                if(this.model.get('markPassedDate')) {             
					                    this.$el.addClass('markPassedDate');
					                } else {
					                    this.$el.removeClass('markPassedDate');
					                }
					                return this;
					            }
							}),
		                    sortable : false,
		                    editable : false,
		                    formatter : BackgridDateFormatter,
		                    headerCell : DateHeaderCell,
		                    renderable:false
		                }
		             ]
	            });
	
	            // Render the grid
	            var $gridView = this.gridView.render().$el;
	            this.$el.html($gridView);
	
	            // Initialize the paginator
	            this.paginator = new GloriaPaginator({
	                collection : this.collection,
	                grid : this.gridView,
					postbackSafe : true
	            });
	
	            // Render the paginator
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
	        
	        onDestroy: function() {
	            this.gridView.remove();
	            this.paginator.remove();
	        }
	    });
	});

    return Gloria.DeliveryControlApp.View.InventoryGridView;
});
