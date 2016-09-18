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
        'views/warehouse/receive/deliverynotelines/view/TransportLabelSelectCell',
        'backgrid-select-all',
        'bootstrap',
        'grid-util',
        'utils/backgrid/SpannedCell',
        'views/warehouse/qualityinspection/overview/view/DeliveryNoteCell'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, StringHeaderCell, IntegerCell,
        ClickableRow, TransportLabelSelectCell, BackgridSelectAll, Bootstrap, GridUtil, SpannedCell, DeliveryNoteCell) {

	var CustomStringCell  = Backgrid.Cell.extend({
		className : 'string-cell attachment-icon',
		formatter : Backgrid.StringFormatter
	});
	
    Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

        View.MandatoryGridView = Marionette.View.extend({

            initialize : function(options) {
            	this.transportLabels = options.transportLabels;
            	this.module = options.module;
				if(this.transportLabels) {
					this.transportLabels.off('add remove change');
				};
				this.listenTo(Gloria.WarehouseApp, 'MaterialLineQI:TransLabel:updateCollection', function(model) {
					this.transportLabels.add(model);
				});
                this.setGrid();
				this.listenTo(Gloria.WarehouseApp, 'MandatoryGrid:clearselection', this.clearSelectedModels);
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
            },
            
            collectionEvents: {            	
                'invalid': 'showErrors',
                'add': 'handleSelectRow',
                'remove': 'handleSelectRow',
                'backgrid:selected': 'handleSelectRow'
            },
            
            clearFilter : function() {
            	this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
			
            showErrors: function(model, errors, options) {
            	var action = (options && options.action) || '';
            	if(action.toUpperCase() === 'APPROVE' && errors) {
            		_.each(errors, function(error){    
            			var errorAttr = error.errorAttr;
            			model.trigger("backgrid:error", model, this.gridView.columns.findWhere({name: errorAttr}), model.get(errorAttr));
            		}, this);            		            		         		            		
            	}  	
            },

            handleSelectRow : _.debounce(function(model, isSelected) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.WarehouseApp.trigger('qualityInspection:mandatoryGrid:select', selectedModels);
			}, 200),
			
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},

            setGrid : function() {
                // Initialize the grid
            	var that = this;
                this.gridView = new Backgrid.Grid({
                	id : that.module == 'blockedPart' ? 'BlockedPartGrid' : 'MandatoryGrid',
                    row : ClickableRow,
                    collection : this.collection,
                    emptyText : i18n.t('Gloria.i18n.general.noRow'),
                    columns :  [
                                {
                                    // Checkbox column
                                    name : "",
                                    cell : "select-row",
                                    headerCell : "select-all",
                                },                                
                                {
                                    name : 'partNumber',
                                    label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partNo'),
                                    cell : function(options) {
         			                	if(options.model.get('qiDetailsUpdated')) {
         			                		return new CustomStringCell(options);
         			                	} else {
         			                		return new Backgrid.StringCell(options);
         			                	}
         			        		},
                                    editable : false,                          
         	                        sortable: false,
         	                        headerCell : function(options) {
                                        options.tooltip = {
                                            'tooltipText': i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partNoTooltip'),
                                            'displayText': i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partNo')
                                        };                    
                                        return new StringHeaderCell(options);
                                    }
                                },
                                {
        			                name : 'partAlias',
        			                label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partAlias'),
        			                cell : 'string',
        			                editable : false,
        			                sortable: false
        			            },
                                {
                                    name : 'partVersion',
                                    label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.version'),
                                    cell : 'string',
                                    editable : false,                          
         	                       	sortable: false,
         	                       	headerCell : StringHeaderCell
                                }, 
                                {
                                    name : 'partName',
                                    label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.partName'),
                                    cell : 'string',
                                    editable : false,
                                    sortable: false,
                                    headerCell : StringHeaderCell         	                       
                                },  
                                {
                                    name : 'receivedQuantity',
                                    label : that.module == 'blockedPart' ? i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.blockedQty') 
                                                        : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.receivedQty'),
                                    cell :  IntegerCell,
                                    formatter : {
                                        fromRaw: function (rawData, model) {
                                            return that.module == 'blockedPart' ? model.get('blockedQuantity') : model.get('receivedQuantity');
                                        }
                                    },
                                    editable : false,
                                    sortable: false
                                },
                                {
        			                name : 'directSendQuantity',             
        			                label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.directSendQuantity'),               
        			                cell : IntegerCell,			                
        			                editable : false,
        			                sortable: false
        			            },
                                {
                                    name : 'directsends',
                                    label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.toApproveQty'),
                                    cell : function(options) {
                                    	options.module = that.module;
                                    	options.transportLabels = that.transportLabels;
        			                	return new DeliveryNoteCell(options);
        			                },
        			                editable : false,
         	                        sortable: false
                                },
                                {
                                    name : 'suggestedBinLocation',
                                    label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.nextLocation'),
                                    cell : SpannedCell,
                                    editable : false,
         	                       	sortable: false
                                }, 
                                {
                                    name : 'transportLabelId',
                                    label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.transportLabel'),
                                    cell : SpannedCell,
                                    editable : false,
         	                       	sortable: false
                                }, 
                                {
                                    name : 'binLocation',
                                    label : i18n.t('Gloria.i18n.warehouse.qualityInspection.qioverview.header.binLocation'),
                                    cell : SpannedCell,
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
	        	if(this.transportLabels) {
	        		this.listenTo(this.transportLabels, 'add remove change', function() {
	        			Gloria.WarehouseApp.trigger('TransportLabel:refresh');
					});
	        	};
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

    return Gloria.WarehouseApp.View.MandatoryGridView;
});