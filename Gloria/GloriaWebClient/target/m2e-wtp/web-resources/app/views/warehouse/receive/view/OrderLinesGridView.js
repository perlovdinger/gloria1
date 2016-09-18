define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'backgrid',
        'i18next',
        'views/common/paginator/PaginatorView',
        'utils/backgrid/dropdownHeaderCell',
        'utils/backgrid/stringHeaderCell',
        'utils/backgrid/integerHeaderCell',
        'utils/backgrid/numberHeaderCell',
        'utils/backgrid/clickableRow',
        'utils/backgrid/IntegerCell',
        'backgrid-select-all',
        'backgrid-select2-cell',
        'bootstrap',
        'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, DropdownHeaderCell, StringHeaderCell,
        IntegerHeaderCell, NumberHeaderCell, ClickableRow, IntegerCell, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil) {

    var columnModel = [
            {
                // Checkbox column
                name : "",
                cell : "select-row",
                headerCell : "select-all",
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
                    options.column.isSearchable = false;
                    return new StringHeaderCell(options);
                }
            },
            {
                name : 'supplierId',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.supplierParmaID'),
                cell : 'string',
                editable : false,
                sortable: false
            },
            {
                name : 'supplierName',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.supplierName'),
                cell : 'string',
                editable : false,
                sortable: false
            },
            {
                name : 'projectId',
                label : i18n.t('Gloria.i18n.warehouse.receive.project'),
                cell : 'string',
                editable : false,
                sortable: false
            },
            {
                name : 'partNumber',
                label : i18n.t('Gloria.i18n.warehouse.receive.partNo'),
                cell : 'string',
                editable : false,
                sortable: false,
                headerCell : function(options) {
                    options.tooltip = {
                        'tooltipText': i18n.t('Gloria.i18n.warehouse.receive.text.partNoTooltip'),
                        'displayText': i18n.t('Gloria.i18n.warehouse.receive.partNo')
                    };
                    options.column.isSearchable = false;
                    return new StringHeaderCell(options);
                }
            },
            {
                name : 'partAlias',
                label : i18n.t('Gloria.i18n.warehouse.receive.partAlias'),
                cell : 'string',
                editable : false,
                sortable: false
            },
            {
                name : 'allowedQuantity',
                label : i18n.t('Gloria.i18n.warehouse.receive.text.allowedQuantity'),              
                cell : IntegerCell,
                editable : false,
                sortable: false
            },
            {
                name : 'receivedQuantity',                
                label : i18n.t('Gloria.i18n.warehouse.receive.text.receivedQty'),              
                cell : IntegerCell,
                editable : false,
                sortable: false
            }];

    Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

        View.OrderLinesGridView = Marionette.LayoutView.extend({

            initialize : function(options) {
                this.setGrid();
            },

            className: 'col-sm-12',
            
            collectionEvents : {
                'add remove backgrid:selected' : 'handleSelectRow'               
            }, 
            
            handleSelectRow : _.debounce(function(model, selected) {
                var selectedModels = this.gridView.getSelectedModels();
                var selectedRows = _.map(selectedModels, function(model) {
                    if(model && model.id)
                        return model.id;
                });
                Gloria.WarehouseApp.trigger('select:orderlinegrid', selectedRows);
            }, 200),

            setGrid : function() {
                // Initialize the grid
                this.gridView = new Backgrid.Grid({
                	id : 'OrderLinesGrid',
                    row : ClickableRow,
                    collection : this.collection,
                    emptyText : i18n.t('Gloria.i18n.general.noRow'),
                    columns : columnModel
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

    return Gloria.WarehouseApp.Receive.View.OrderLinesGridView;
});