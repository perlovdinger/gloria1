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

    Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

        View.DeliveryNoteTransferReturnGridView = Marionette.LayoutView.extend({

            initialize : function(options) {
                this.selectedReceiveType = options.selectedReceiveType;
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
                Gloria.WarehouseApp.trigger('TransferReturnGrid:select', selectedRows);
            }, 200),

            setGrid : function() {
                // Initialize the grid
            	var that = this;
                this.gridView = new Backgrid.Grid({
                	id : 'DeliveryNoteLineTransferReturngrid',
                    row : ClickableRow,
                    collection : this.collection,
                    emptyText : i18n.t('Gloria.i18n.general.noRow'),
                    columns :[
                              {
                                  // Checkbox column
                                  name : "",
                                  cell : "select-row",
                                  headerCell : "select-all",
                              },
                              {
                                  name : 'dispatchNoteNo',
                                  label : i18n.t('Gloria.i18n.warehouse.receive.text.delivNoteNo'),
                                  cell : 'string',
                                  editable : false,
                                  sortable: false
                              },
                              {
                                  name : 'deliveryAddressId',
                                  label :  i18n.t('Gloria.i18n.warehouse.receive.deliveryAddress') ,              
                                  cell : IntegerCell,
                                  editable:false,
                                  renderable : that.options.selectedReceiveType === 'RETURN' || that.options.selectedReceiveType === 'RETURN_TRANSFER' ,
                                  sortable: false
                              },
                              {
                                  name : 'parmaID',
                                  label : i18n.t('Gloria.i18n.warehouse.receive.text.supplierParmaID'),
                                  cell : 'string',
                                  editable : false,
                                  sortable: false
                              },
                              {
                                  name : 'parmaName',
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
                                  sortable: false
                              },
                              {
                                  name : 'partAlias',
                                  label : i18n.t('Gloria.i18n.warehouse.receive.partAlias'),
                                  cell : 'string',
                                  editable : false,
                                  sortable: false
                              },
                              {
                                  name : 'quantity',
                                  label : that.options.selectedReceiveType=== 'TRANSFER' ? i18n.t('Gloria.i18n.warehouse.receive.text.materialLineQuantity') : 
										i18n.t('Gloria.i18n.warehouse.receive.text.shippedQty'),              
                                  cell : IntegerCell,
                                  editable : false,
                                  sortable: false
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

    return Gloria.WarehouseApp.Receive.View.DeliveryNoteTransferReturnGridView;
});