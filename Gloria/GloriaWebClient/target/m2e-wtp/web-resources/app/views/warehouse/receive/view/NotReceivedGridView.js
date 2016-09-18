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
        'utils/backgrid/IntegerCell',
        'backgrid-select-all',
        'bootstrap',
        'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell,
        ClickableRow, IntegerCell, BackgridSelectAll, Bootstrap, GridUtil) {

    var columnModel = [
            {
                name : 'directSend',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.directSend'),
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
                sortable: true,
                headerCell : function(options) {
                	options.column.isSearchable = false;
                	return new StringHeaderCell(options);
                }
            },
            {
                name : 'orderNo',
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
                name : 'supplierId',
                label : i18n.t('Gloria.i18n.warehouse.receive.supplierParmaID'),
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : StringHeaderCell
            },
            {
                name : 'supplierName',
                label : i18n.t('Gloria.i18n.warehouse.receive.supplierName'),
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : StringHeaderCell
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
                name : 'allowedQuantity',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.allowedQuantity'),
                cell : 'integer',
                editable : false,
                sortable: false,
                headerCell : function(options) {
                	options.column.isSearchable = false;
                	return new StringHeaderCell(options);
                }
            },
            {
                name : 'receivedQuantity',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.receivedQty'),              
                cell : 'integer',
                editable : false,
                sortable: false,
                headerCell : function(options) {
                	options.column.isSearchable = false;
                	return new StringHeaderCell(options);
                }
            }];

    Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

        View.NotReceivedGridView = Marionette.LayoutView.extend({

            initialize : function(options) {
                this.setGrid();
                this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
            },

            clearFilter : function() {
            	this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
			
            setGrid : function() {
				this.gridView = new Backgrid.Grid({
					id : 'NotReceivedGrid',
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

    return Gloria.WarehouseApp.View.NotReceivedGridView;
});