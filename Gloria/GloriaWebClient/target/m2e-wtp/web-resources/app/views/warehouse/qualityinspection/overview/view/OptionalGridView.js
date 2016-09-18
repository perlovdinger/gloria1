define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'views/common/paginator/PaginatorView',
		'utils/backgrid/stringHeaderCell',
        'utils/backgrid/IntegerCell',
		'utils/backgrid/clickableRow',
		'backgrid-select-all',
		'bootstrap',
		'grid-util',
		'utils/backgrid/dropdownHeaderCell'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell, IntegerCell,
		ClickableRow, BackgridSelectAll, Bootstrap, GridUtil, DropdownHeaderCell) {

	var CustomStringCell  = Backgrid.Cell.extend({
		className : 'string-cell attachment-icon',
		formatter : Backgrid.StringFormatter
	});
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.OptionalGridView = Marionette.View.extend({

			initialize : function(options) {
				this.module = options.module;
				this.setGrid();
				this.listenTo(Gloria.WarehouseApp, 'OptionalGrid:clearselection', this.clearSelectedModels);
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
				Gloria.WarehouseApp.trigger('qualityInspection:optionalGrid:select', selectedModels);
			}, 200),

			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
			setGrid : function() {
				// Initialize the grid
				var that = this;
				this.gridView = new Backgrid.Grid({
					id : that.module == 'optional' ? 'OptionalGrid' : 'InStockGrid',
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
					{
						// Checkbox
						// column
						name : "",
						cell : "select-row",
						headerCell : "select-all",
					},
					{
						name : 'markedForInspection',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.sendToQI'),
						cell : Backgrid.StringCell.extend({
		                    render: function() {
		                    	if(this.model.get('markedForInspection')) {
		                    		this.$el.html('<i class="fa fa-minus-circle"></i>');
		                    	} else {
		                    		this.$el.html('');
		                    	};                  
		                        return this;
		                    }
		                }),
						editable : false,
						sortable : false
					},
					{
						name : 'pPartNumber',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partNo'),
						cell : function(options) {
							if (options.model.get('hasDetails')) {
								return new CustomStringCell(options);
							} else {
								return new Backgrid.StringCell(options);
							}
						},
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
						name : 'pPartVersion',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.version'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : StringHeaderCell
					},
					{
						name : 'pPartName',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partName'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : StringHeaderCell
					},
					{
						name : 'pPartModification',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partModification'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : StringHeaderCell
					},
					   {
                        name : 'materialOrderNo',
                        label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.orderNo'),
                        cell : 'string',
                        editable : false,
                        sortable : false,
                        headerCell : StringHeaderCell,
                        renderable : that.module === 'inStock' 
                       },                    
                    {
                        name : 'projectId',
                        label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.project'),
                        cell : 'string',
                        editable : false,
                        sortable : false,
                        headerCell : StringHeaderCell,
                        renderable : that.module === 'inStock' 
                    },		
					{
						name : 'quantity',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.qty'),
						cell : 'integer',
						editable : false,
						sortable : false
					},
					   {
                        name : 'storageRoomCode',
                        label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.storageRoom'),
                        cell : 'string',
                        editable : false,
                        sortable : false,
                        renderable : that.module === 'inStock' 
                    },
					{
						name : 'binLocation',
						label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.binLocation'),
						cell : 'string',
						editable : false,
						sortable : false,
						headerCell : StringHeaderCell
					},
					   {
                        name : 'referenceId',
                        label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.testObject'),
                        cell : 'string',
                        editable : false,
                        sortable : false,
                        headerCell : StringHeaderCell,
                        renderable :that.module==='inStock' 
                    },
                    {
                        name : 'contactPersonId',
                        label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.contactPersonId'),
                        cell : 'string',
                        editable : false,
                        sortable : false,
                        headerCell : StringHeaderCell,
                        renderable : that.module === 'inStock' 
                    },
                    {
                        name : 'contactPersonName',
                        label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.contactPersonName'),
                        cell : 'string',
                        editable : false,
                        sortable : false,
                        headerCell : StringHeaderCell,
                        renderable : that.module === 'inStock' 
                    },
                    {
                        name : 'inspectionStatus',
                        label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.inspectionStatus'),
                        cell : Backgrid.StringCell.extend({
		                    render: function() {
		                        var status = this.model.get('inspectionStatus');
		                        if(status) {
		                            this.$el.html('<span>' + i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.qiinspectionstatus.' + status) + '</span>');                           
		                        } else {
		                            this.$el.html('');
		                        }
		                        return this;
		                    }
		                }),
                        editable : false,
                        sortable : false,
                        renderable : that.module=== 'inStock',
                        headerCell : function(options) {
                            options.column.type = 'select';
                            options.column.noAll = true;
                            options.column.defaultData = [{
                                id : '',
                                text : i18n.t('Gloria.i18n.all')
                            },{
                                id : 'OK',
                                text : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.qiinspectionstatus.OK')
                            },{
                                id : 'NOT_OK',
                                text : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.qiinspectionstatus.NOT_OK')
                            },{
                                id : 'INSPECTING',
                                text : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.qiinspectionstatus.INSPECTING')
                            }];
                            return new DropdownHeaderCell(options);
                        }
                    }]
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

	return Gloria.WarehouseApp.View.OptionalGridView;
});