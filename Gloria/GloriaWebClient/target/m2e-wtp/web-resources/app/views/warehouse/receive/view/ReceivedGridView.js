define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'backgrid',
        'i18next',
        'views/common/paginator/PaginatorView',
        'utils/backgrid/stringHeaderCell',
        'utils/backgrid/clickableRow',
        'utils/backgrid/dateHeaderCell',
		'utils/backgrid/dateFormatter',
        'backgrid-select-all',
        'utils/DateHelper',
        'bootstrap',
        'grid-util'        
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell,
        ClickableRow, DateHeaderCell, BackgridDateFormatter, BackgridSelectAll, DateHelper, Bootstrap, GridUtil) {

    var columnModel = [{
				//Checkbox column
		    	name : '',
				cell : 'select-row',
				headerCell : 'select-all'
    		},
    		{
                name : 'deliveryNoteNo',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.delivNoteNo'),
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : StringHeaderCell
            },
            {
                name : 'orderReference',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.orderNo'),
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : function(options) {
                    options.tooltip = {
                        'tooltipText': i18n.t('Gloria.i18n.warehouse.receive.text.orderNoTooltip'),
                        'displayText': i18n.t('Gloria.i18n.warehouse.receive.text.orderNo')
                    };                    
                    return new StringHeaderCell(options);
                }
            },
            {
                name : 'receivalDate',
                label : i18n.t('Gloria.i18n.warehouse.receive.receivedDate'),
                cell : Backgrid.DatetimeCell.extend({
					formatter : {
						fromRaw : function(rawValue) {
							return DateHelper.formatDateTimewithUTC(rawValue); //in utc
						}
					}
				}),
                editable : false,
                sortable: false,
				headerCell : DateHeaderCell
            },
            {
                name : 'projectId',
                label : i18n.t('Gloria.i18n.warehouse.receive.project'),
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : StringHeaderCell
            },
            {
                name : 'referenceId',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.referenceId'),
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : StringHeaderCell
            },
            {
                name : 'partNumber',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.partNo'),
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : function(options) {
                    options.tooltip = {
                        'tooltipText': i18n.t('Gloria.i18n.warehouse.receive.text.partNoTooltip'),
                        'displayText': i18n.t('Gloria.i18n.warehouse.receive.text.partNo')
                    };                    
                    return new StringHeaderCell(options);
                }
            },
            {
                name : 'partVersion',
                label : i18n.t('Gloria.i18n.warehouse.receive.partVersion'),
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : function(options) {
                	options.column.isSearchable = false;
                	return new StringHeaderCell(options);
                }
            },
            {
                name : 'partName',
                label : i18n.t('Gloria.i18n.warehouse.receive.partName'),            
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : StringHeaderCell
            },
            {
                name : 'partModification',
                label : i18n.t('Gloria.i18n.warehouse.receive.partModification'),            
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : StringHeaderCell
            },
            {
                name : 'quantity',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.qiQty'),            
                cell : 'integer',
                editable : false,
                sortable: false
            }];

    Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

        View.ReceivedGridView = Marionette.LayoutView.extend({

            initialize : function(options) {
            	this.listenTo(Gloria.WarehouseApp, 'ReceivedGrid:clearselection', this.clearSelectedModels);
            	this.listenTo(this.collection, 'QueryParams:changed', function() {
            		this.gridView.clearSelectedModels();
					this.handleSelectRow();
				});
                this.setGrid();
                this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},
			
            events : {
				'change .select-row-cell input' : 'handleSelectRow',
				'change .select-all-header-cell input' : 'handleSelectRow'
			},
			
			clearFilter : function() {
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
			
			handleSelectRow : _.debounce(function(e) {
				var selectedModels = this.gridView.getSelectedModels();
	            Gloria.WarehouseApp.trigger('ReceivedGrid:select', selectedModels);
	        }, 200),
	        
	        clearSelectedModels: function() {
	        	this.gridView.clearSelectedModels();
			},
	        
            setGrid : function() {
				this.gridView = new Backgrid.Grid({
					id : 'ReceivedGrid',
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : columnModel
				});
				this.paginator = new GloriaPaginator({
					collection : this.collection,
					grid : this.gridView,
					postbackSafe : true
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

    return Gloria.WarehouseApp.View.ReceivedGridView;
});