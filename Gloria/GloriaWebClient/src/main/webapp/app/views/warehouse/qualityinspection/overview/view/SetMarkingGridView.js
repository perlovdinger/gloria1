define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'views/common/paginator/PaginatorView',
		'utils/backgrid/stringHeaderCell',
		'utils/backgrid/dateFormatter',
        'utils/backgrid/IntegerCell',
		'utils/backgrid/clickableRow',
		'backgrid-select-all',
		'bootstrap',
		'views/warehouse/qualityinspection/overview/view/MarkHeaderCell',
		'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell, BackgridDateFormatter, IntegerCell,
		ClickableRow, BackgridSelectAll, Bootstrap, MarkHeaderCell, GridUtil) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.SetMarkingGridView = Marionette.View.extend({

			initialize : function(options) {
				this.setGrid();
				this.listenTo(Gloria.WarehouseApp, 'SetMarkingGrid:clearselection', this.clearSelectedModels);
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},

			collectionEvents : {
				'add' : 'handleSelectRow',
				'remove' : 'handleSelectRow',
				'backgrid:selected' : 'handleSelectRow'
			},
			
			clearFilter : function() {
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},

			handleSelectRow : _.debounce(function(model, isSelected) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.WarehouseApp.trigger('qualityInspection:setMarkingGrid:select', selectedModels);
			}, 200),

			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
			setGrid : function() {
				// Initialize the grid
				var that = this;
				this.gridView = new Backgrid.Grid({
					id : 'SetMarkingGrid',
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
					{
						name : "",
						cell : "select-row",
						headerCell : "select-all",
					},
					{
						name : 'qiMarking',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.qiMarking'),
						cell : Backgrid.StringCell.extend({
		                    render: function() {
		                    	if(this.model.get('qiMarking') == 'MANDATORY') {
		                    		this.$el.html('<i class="fa fa-star"></i>');
		                    	} else if(this.model.get('qiMarking') == 'OPTIONAL') {
		                    		this.$el.html('<i class="fa fa-star-half-empty"></i>');
		                    	} else {
		                    		this.$el.html('');
		                    	};
		                        return this;
		                    }
		                }),
						editable : false,
						sortable : false,
						headerCell : MarkHeaderCell
					},
					{
						name : 'directSend',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.directSend'),
						cell : Backgrid.StringCell.extend({
		                    render: function() {
		                    	if(this.model.get('directSend') == true) {
		                    		this.$el.html('<i class="fa fa-paper-plane-o"></i>');
		                    	} else {
		                    		this.$el.html('');
		                    	};
		                        return this;
		                    }
		                }),
						editable : false,
						sortable : true
					},
					{
						name : 'supplierId',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.supplierId'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : StringHeaderCell
					},
					{
						name : 'supplierName',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.supplierName'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : StringHeaderCell
					},
					{
						name : 'orderNo',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.orderNo'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : function(options) {                 
                            options.tooltip = {
                                    'tooltipText': i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.orderNoTooltip'),
                                    'displayText': i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.orderNo')
                            };                       
                            return new StringHeaderCell(options);
                        }
					},
					{
						name : 'partNumber',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partNo'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : function(options) {
		                    options.tooltip = {
		                        'tooltipText': i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partNoTooltip'),
		                        'displayText': i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partNo')
		                    };                    
		                    return new StringHeaderCell(options);
		                }
					},
					{
						name : 'partVersion',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.version'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : StringHeaderCell
					},
					{
						name : 'partName',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partName'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : StringHeaderCell
					},
					{
						name : 'referenceId',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.referenceId'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : StringHeaderCell
					},
					{
						name : 'buildStartDate',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.buildStartDate'),
						cell : 'string',
						editable : false,
						sortable : false,
						formatter : BackgridDateFormatter
					},
					{
						name : 'orderStaDate',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.orderStaDate'),
						cell : 'string',
						editable : false,
						sortable : false,
						formatter : BackgridDateFormatter,
						headerCell : function(options) {                 
                            options.tooltip = {
                                    'tooltipText': i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.orderStaDateTooltip'),
                                    'displayText': i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.orderStaDate')
                            };  
                            options.column.isSearchable = false;
                            return new StringHeaderCell(options);
                        }
					},
					{
						name : 'quantity',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.quantity'),
						cell : 'integer',
						editable : false,
						sortable : false
					},
					{
						name : 'inStock',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.inStock'),
						cell : Backgrid.StringCell.extend({
		                    render: function() {
		                    	if(this.model.get('inStock')) {
		                    		this.$el.html('<i class="fa fa-check"></i>');
		                    	} else {
		                    		this.$el.html('');
		                    	};
		                        return this;
		                    }
		                }),
						editable : false,
						sortable : true
					} ]
				});
				
				// Initialize the paginator
				this.paginator = new GloriaPaginator({
					collection : this.collection,
					grid : this.gridView,
					postbackSafe : true
				});
			},

			render : function() {
				// Render the grid
				var $gridView = this.gridView.render().$el;
				this.$el.html($gridView);
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

			onDestroy : function() {
				this.gridView.remove();
				this.paginator.remove();
			}
		});
	});

	return Gloria.WarehouseApp.View.SetMarkingGridView;
});