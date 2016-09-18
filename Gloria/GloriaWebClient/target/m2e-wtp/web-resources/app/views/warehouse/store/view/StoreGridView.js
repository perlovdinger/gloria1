define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',
		'views/common/paginator/PaginatorView',
		'utils/backgrid/stringHeaderCell',
        'utils/backgrid/dropdownHeaderCell',
        'utils/backgrid/IntegerCell',
		'views/common/binlocation/BinLocationCell',
		'utils/handlebars/TransportLabelSelectorHelper',
		'utils/backgrid/clickableRow',
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
		'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, PaginatorView, StringHeaderCell, DropdownHeaderCell, IntegerCell,
		BinLocationCell, transportLabelSelectorHelper, ClickableRow, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil) {
		
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		var data = undefined;
		
		View.StoreGridView = Marionette.LayoutView.extend({
			
			initialize : function(options) {
				data = null;
				this.isToStoreModule = options.isToStoreModule;
				this.collection = options.collection;
	            this.setGrid();
	            this.listenTo(this.collection, 'change:storedQuantity backgrid:selected', this.handleSelectRow);
				this.listenTo(Gloria.WarehouseApp, 'Store:SaveStoreInfoIfValid', this.saveStoreInfoIfValid);
				this.listenTo(Gloria.WarehouseApp, 'Store:clearSelectedModels', this.clearSelectedModels);
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},
			
			clearFilter : function() {
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
			
			setGrid : function() { 
			    var that = this;
	            // Initialize the grid
	            this.gridView = new Backgrid.Grid({
	            	id : this.isToStoreModule ? 'ToStoreGrid' : 'StoreGrid',
	                collection : this.collection,
					emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [
                   		{
                			// Checkbox column
                			name : "",
                			cell : "select-row",
                			headerCell : "select-all"
                		},
                		{
                            name : 'pPartNumber',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.pPartNumber'),
                            cell : 'string',
                            sortable : !this.isToStoreModule,
                            editable : false,
                            headerCell : function(options) {
                                if(that.isToStoreModule) options.column.isSearchable = false;
                                options.tooltip = {
                                    'tooltipText': i18n.t('Gloria.i18n.warehouse.store.header.pPartNumberTooltip'),
                                    'displayText': i18n.t('Gloria.i18n.warehouse.store.header.pPartNumber')
                                }; 
                                return new StringHeaderCell(options);
                            }
                        },
                        {
                            name : 'pPartVersion',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.pPartVersion'),
                            cell : 'string',
                            sortable : false,
                            editable : false,
                            headerCell : function(options) {
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
                        },{
                            name : 'pPartName',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.pPartName'),
                            cell : 'string',
                            sortable : false,
                            editable : false,
                            headerCell : function(options) {
                                if(that.isToStoreModule) options.column.isSearchable = false;
                                return new StringHeaderCell(options);
                            }
                        },{
                            name : 'pPartModification',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.pPartModification'),
                            cell : 'string',
                            sortable : false,
                            editable : false,
                            headerCell : function(options) {
                                if(that.isToStoreModule) options.column.isSearchable = false;
                                return new StringHeaderCell(options);
                            }
                        },{
                            name : 'stockBalance',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.stockBalance'),
                            cell : IntegerCell,
                            sortable : false,
                            editable : false,
                            headerCell : function(options) {
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
                        },{
                            name : 'quantity',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.quantity'),
                            cell : IntegerCell,
                            sortable : false,
                            editable : false,
                            headerCell : function(options) {
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
                        },{
                            name : 'storedQuantity',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.storedQuantity'),
                            cell : IntegerCell,
                            sortable : false,
                            editable : function(model, column) {
                            	return !model.isSplitted;
   						    },
                            formatter : {
								fromRaw: function (rawData, model) {
								    if(rawData.length == 0) return rawData;
									return rawData || (model.get('quantity') && model.set('storedQuantity', model.get('quantity')));
								},
								toRaw : function(formattedData, model) {
									return formattedData;
								}
							},
                            renderable : this.isToStoreModule,
                            headerCell : function(options) {
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
                        },{
                            name : 'transportLabel',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.transportLabel'),
                            cell : 'string',
                            sortable : false,
                            editable : false,
                            renderable : !this.isToStoreModule,
                            headerCell : function(options) {
            				    var data = transportLabelSelectorHelper.constructTransportLabelList();
            				    //options.column.type = 'select';
            				    options.column.defaultData = _.map(data, function(value, key) {
                                    if(value && value.code && value.id) {
                                        return {
                                            id : value.code,
                                            text : value.code
                                        };
                                    } else {
                                        return;
                                    }
                                });
            				    options.column.select2Options = {
			                    	minimumResultsForSearch : 1
			                    };
                                return new DropdownHeaderCell(options);
                            }
                        },{
                            name : 'suggestedBinLocation',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.suggestedBinLocation'),
                            cell : 'string',
                            sortable : !this.isToStoreModule,
                            editable : false,
                            headerCell : function(options) {
                                options.column.isSearchable = false;
                                return new StringHeaderCell(options);
                            }
                        },{
                            name : 'binlocation',
                            label : i18n.t('Gloria.i18n.warehouse.store.header.binlocation'),
                            cell : function(options) {
                            	options.disabled = !!options.model.isSplitted;
                            	options.select2Options = {								
    								initSelection: function(element, callback) {
    									callback({
    										id:  options.model.get('binlocation') || options.model.get('suggestedBinLocationId'),
    										text:  options.model.get('binLocationCode') || options.model.get('suggestedBinLocation')
    									});
    								}
    							};
                            	return new BinLocationCell(options);
                            },
                            sortable : false,
                            editable : false,
                            renderable : this.isToStoreModule,
                            headerCell : function(options) {
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                			}
                        }
                	]
	            });
	            
				if(!this.isToStoreModule) {
					this.paginator = new PaginatorView({
						collection : this.collection,
						gridId : 'StoreGrid',
						grid: this.gridView,
						postbackSafe : true
					});
				}
			},

			handleSelectRow : _.debounce(function(e) {
				var selectedModels = this.gridView.getSelectedModels();
				Gloria.WarehouseApp.trigger('Store:setSelectedItems', selectedModels);
	        }, 200),
			
	        clearSelectedModels : function() {
				this.gridView.clearSelectedModels();
			},
	        
			saveStoreInfoIfValid : function() {
				var errorList = new Array();
				var modelsToNotStore = new Array();
				data.each(function(model) {
					var storedQtyZeroOrEmpty = (model.get('storedQuantity') == 0) || (model.get('storedQuantity') == "");
					var binLocationEmpty = !model.get('binlocation');					
					if(storedQtyZeroOrEmpty && binLocationEmpty){
						modelsToNotStore.push(model.id);
					}else{
						if(!model.get('storedQuantity') || model.get('storedQuantity') > model.get('quantity')
								|| parseInt(model.get('storedQuantity')) <= 0 || parseInt(model.get('storedQuantity')) %1 != 0) {
								errorList.push({
									message : i18n.t('errormessages:errors.GLO_ERR_050'),
									element : '#storedQuantity_' + (model.id || model.cid)
								});
						 }
						if(!model.get('binlocation')) {
								errorList.push({
									message : i18n.t('errormessages:errors.GLO_ERR_051'),
									element : '#binlocation_' + (model.id || model.cid)
								});
						}	
					}
					
				});
				if(errorList.length == 0) {					
					_.each(modelsToNotStore, function(modelId) {
						data.remove(modelId, {silent: true});
					});
					Gloria.WarehouseApp.trigger('Store:SaveStoreInfo', data);
				} else {
					this.showErrors(errorList);					
				}				
				
			},
			
			showErrors: function (errorList) {
				this.hideErrors();
        		Gloria.trigger('showAppMessageView', {
        			type : 'error',
        			title : i18n.t('errormessages:general.title'),
        			message : errorList,
        			duplicate : false
        		});
	        },

	        hideErrors: function() {
	        	Gloria.trigger('hideAppMessageView');
	        	$('td').removeClass('has-error');
	        	$('td').removeClass('error');
			},
	        
			render : function() {
				data = this.gridView.collection;				
	            var $gridView = this.gridView.render().$el;
	            this.$el.html($gridView);
	            if(!this.isToStoreModule) {
	            	$gridView.after(this.paginator.render().$el);
	            }
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

	return Gloria.WarehouseApp.View.StoreGridView;
});