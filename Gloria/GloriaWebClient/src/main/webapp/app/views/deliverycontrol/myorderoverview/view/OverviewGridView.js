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
	    'utils/backgrid/TooltipCell',
	    'utils/backgrid/clickableRow',
	    'utils/backgrid/dropdownHeaderCell',
	    'utils/backgrid/SpannedCell',
	    'utils/backgrid/DateFilterHeader/DateHeaderFilterCell',
	    'utils/backgrid/FlagHeaderCell',
	    'utils/backgrid/DateCellEditor',
	    'utils/backgrid/IntegerCell',
	    'backgrid-select-all',
	    'backgrid-select2-cell',
	    'bootstrap',
	    'datepicker',
	    'utils/DateHelper',
	    'utils/UserHelper',
	    'grid-util',
	    'views/deliverycontrol/myorderoverview/view/DeliveryScheduleCell',
	    'views/procurement/detail/view/highlightRow'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator, DateHeaderCell, IntegerHeaderCell, StringHeaderCell, 
		BackgridDateFormatter, TooltipCell, ClickableRow, DropdownHeaderCell, SpannedCell, DateHeaderFilterCell, FlagHeaderCell, DateCellEditor, IntegerCell,
		BackgridSelectAll, BackgridSelect2Cell, Bootstrap, Datepicker, DateHelper, UserHelper, GridUtil, DeliveryScheduleCell, HighlightableRow) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		var CustomStringCell  = Backgrid.Cell.extend({
			className : 'string-cell info-icon',
			formatter : Backgrid.StringFormatter
		});
		
		var CustomIntegerCell  = Backgrid.Cell.extend({
			className : 'info-icon',
			formatter : Backgrid.StringFormatter
		});		
		
		View.OverviewGridView = Marionette.LayoutView.extend({
	
	        initialize : function(options) {
	            this.module = options.module;
	            this.showDCInfo = options.showDCInfo;
	            this.collection = options.collection;
	            this.deliveryUserSettings = options.deliveryUserSettings;
	            this.listenTo(Gloria.DeliveryControlApp, 'OverviewGrid:clearselection', this.clearSelectedModels);
	            this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
	            this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
	            this.listenTo(Gloria.DeliveryControlApp, 'MyOrderOverview:grid:rerender', function(options) {
					this.showDCInfo = options.showDCInfo;
					this.render();
				});
	        },
	
	        events : {
	            'change .select-row-cell input' : 'handleSelectRow',
	            'change .select-all-header-cell input' : 'handleSelectRow',
	            'rowdoubleclicked table.backgrid tr' : 'rowDoubleClick'
	        },
	
	        collectionEvents : {
	            'backgrid:edited' : 'saveFlag'
	        },
	        
	        clearFilter : function() {
	        	this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
	
	        handleSelectRow : _.debounce(function(model, selected) {
				var selectedModels = this.gridView.getSelectedModels();
	            Gloria.DeliveryControlApp.trigger('myOrderOverview:selectGridLine', selectedModels, this.getOrderLineId(selectedModels));
	        }, 200),
	        
	        clearSelectedModels: function() {
	        	this.gridView.clearSelectedModels();
			},
			
	        saveFlag : function(orderLines, args, command) {
	        	if(command.keyCode === 13) {
	        		var shouldRefetch = orderLines.hasChanged('staAgreedDate') && orderLines.previous('staAgreedDate') === null;
	        		orderLines.collection.get(orderLines.id).save({}, {
	        			validate : false,
	        			success: function() {	        				
	        				if(orderLines.deliverySchedules && shouldRefetch) {
	        					orderLines.deliverySchedules.fetch({
	        			            url: '/material/v1/orderlines/' + orderLines.id + '/deliveryschedules'
	        			        });
	        				}
	        			}
	        		});
	        	}
	        },
	
	        getOrderLineId : function(ids) {
	            var orderLineIds = new Array();
	            $.each(ids, function(index, id) {
	                orderLineIds.push(this.collection.get(id).get('id'));
	            });
	            return orderLineIds;
	        },

	        rowDoubleClick : function(e, model) {
	        	var that = this;
	        	Gloria.DeliveryControlApp.trigger('MyOrderOverview:SaveAndRedirect', model, function() {
		        	Backbone.history.navigate('deliverycontrol/myOrderOverview/' + that.module + '/orderLineDetail/' + model.get('id'), {
		                trigger : true
		            });
				});
	        },
	
	        render : function() {
	        	var that = this;
	            // Initialize the grid
	            this.gridView = new Backgrid.Grid({
	            	id : 'MyOrderOverview' + that.module + 'Grid',
	                //row : ClickableRow,
	            	 row : HighlightableRow.extend({  //for highlighting on condition 
                         conditionCallback : function() {
                             if(that.module == 'completed'){
                                 return "";
                             }
                             else{
                             if (this.model.get('contentEdited') == true) {
                                 return "border-BurlyWood";
                             } else {
                                 return "";
                             }
                             }
                         },
                         
                     }),
	                collection : this.collection,
	                emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
		                	// Checkbox column
		                    name: "",
		                    cell: "select-row",
		                    headerCell: "select-all",
		                }, {
		                    name : 'consignorName',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.consignorId'),
		                    sortable : true,
		                    cell : 'string',
		                    editable : false,
		                    headerCell : StringHeaderCell,
		                    columnChooser: {                            	
                            	choosable: that.module == 'internal'
                            },
		                    renderable : that.module == 'internal'
		                }, {
		                    name : 'supplierId',
		                    label : that.module == 'external'? 
		                    		i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.supplierId')
		                    		: i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.supplier'),
		                    cell : function(options) {
		                    	options.column.tooltip = {
                            		title : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.text.supplierId') + ': '
                            			+ (options.model.get('supplierName') ? options.model.get('supplierName') : '')
								};
								return new TooltipCell(options);
							},
		                    editable : false,
		                    headerCell : StringHeaderCell,
		                    columnChooser: {                            	
                            	choosable: that.module == 'external' || that.module == 'completed'
                            },
		                    renderable : that.module == 'external' || that.module == 'completed'
		                }, {
		                    name : 'orderNo',
		                    label : (that.module == 'internal' || that.module == 'completed')? 
		                    		i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.internalOrderOverviewOrderNo')
		                    		: i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.orderNo'),
		                    cell : 'string',
		                    editable : false,
		                    headerCell : function(options) {            
                                if(that.module.toUpperCase() != 'INTERNAL') {
                                    options.tooltip = {
                                            'tooltipText': i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.orderNoTooltip'),
                                            'displayText': i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.orderNo')
                                    };  
                                } else {
                                    options.tooltip = {
                                            'tooltipText': i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.internalOrderNoTooltip'),
                                            'displayText': i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.internalOrderOverviewOrderNo')
                                    };
                                }
                                return new StringHeaderCell(options);
                            }
		                }, {
		                    name : 'partNumber',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.partNumber'),
		                    cell : 'string',
		                    editable : false,
		                    headerCell : function(options) {
                                options.tooltip = {
                                    'tooltipText': i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.partNumberTooltip'),
                                    'displayText' : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.partNumber')
                                };                                
                                return new StringHeaderCell(options);
                            }
		                }, {
		                    name : 'partAlias',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.partAlias'),
		                    cell : 'string',
		                    editable : false,
		                    headerCell : StringHeaderCell,
		                    columnChooser: {                            	
                            	choosable: that.module == 'internal' || that.module == 'completed'
                            },
		                    renderable : that.module == 'internal' || that.module == 'completed'
		                }, {
		                    name : 'partVersion',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.partVersion'),
		                    cell : function(options) {
                                if(that.module.toUpperCase() == 'EXTERNAL' && options.model.get('alertPartVersion')) {
                                    return new CustomStringCell(options);
                                } else {
                                    return new Backgrid.StringCell(options);
                                }
                            },
		                    editable : false,
		                    headerCell : StringHeaderCell
		                }, {
		                    name : 'partName',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.partName'),
		                    cell : 'string',						
		                    editable : false,
		                    headerCell : StringHeaderCell
		                }, {
		                    name : 'projectId',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.projectId'),
		                    cell : 'string',
		                    editable : false,
		                    headerCell : StringHeaderCell
		                }, {
		                    name : 'reference',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.reference'),
		                    cell : 'string',
		                    editable : false,
		                    headerCell : StringHeaderCell
		                }, {
		                    name : 'buildStartDate',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.buildStartDate'),
		                    cell : 'string',
		                    editable : false,
		                    formatter : BackgridDateFormatter,
		                    customFilters: ['buildStartDate', 'buildStartDateFrom', 'buildStartDateTo'],
                            headerCell :  function(options) {
                                options.dateFilterBindings = {
                                        on: 'buildStartDate',
                                        from: 'buildStartDateFrom',
                                        to: 'buildStartDateTo'
                                };
                                options.headerTitle = i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.titlebuildStartDate');
                                return new DateHeaderFilterCell(options);
                            },
		                }, {
		                    name : 'orderStaDate',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.orderStaDate'),
		                    cell : function(options) {
                                if(that.module.toUpperCase() == 'EXTERNAL' && options.model.get('orderStaChanged')) {
                                    return new CustomStringCell(options);
                                } else {
                                    return new Backgrid.StringCell(options);
                                }
                            },
		                    editable : false,
		                    formatter : BackgridDateFormatter,
		                    customFilters: ['orderStaDate', 'orderStaDateFrom', 'orderStaDateTo'],
                            headerCell :  function(options) {
                                options.dateFilterBindings = {
                                        on: 'orderStaDate',
                                        from: 'orderStaDateFrom',
                                        to: 'orderStaDateTo'
                                };
                                options.headerTitle = i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.titleorderStaDate');
                                return new DateHeaderFilterCell(options);
                            },
		                }, {
		                    name : 'staAcceptedDate',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.staAcceptedDate'),
		                    cell : function(options) {		           
		                    	return new (Backgrid.Cell.extend({		                    		
		                    		editor: function(options) {
		                    			options.datepickerOptions = {
	                    					clearBtn: true
		                    			};
		                    			return new DateCellEditor(options);
		                    		}
		                    	}))(options);
		                    },
		                    editable : that.module != 'completed' && UserHelper.getInstance().hasPermission('edit', ['DeliveryControlPartDetail']),	                  
		                    formatter : BackgridDateFormatter,
		                    headerCell :  function(options) {
		                        options.tooltip = {
                                        'tooltipText': i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.staAcceptedDateTooltip'),
                                        'displayText': i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.staAcceptedDate')
                                };
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
		                }, {
		                    name : 'staAgreedDate',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.staAgreedDate'),
		                    cell : function(options) {		           
		                    	return new (Backgrid.Cell.extend({		                    		
		                    		editor: function(options) {
		                    			options.datepickerOptions = {
	                    					clearBtn: true
		                    			};
		                    			return new DateCellEditor(options);
		                    		}
		                    	}))(options);
		                    },
		                    editable : that.module != 'completed' && UserHelper.getInstance().hasPermission('edit', ['DeliveryControlPartDetail']),
		                    formatter : BackgridDateFormatter,
		                    headerCell :  function(options) {
		                        options.tooltip = {
                                        'tooltipText': i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.staAgreedDateTooltip'),
                                        'displayText': i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.staAgreedDate')
                                };
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
		                }, {
		                    name : 'quantity',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.text.quantity'),
		                    cell : function(options) {
			                	if(that.module.toUpperCase() == 'EXTERNAL' && options.model.get('alertQuantiy')) {
			                		return new CustomIntegerCell(options);
			                	} else {
			                		return new Backgrid.IntegerCell(options);
			                	}
			        		},
		                    sortable : false,
		                    editable : false,
		                    headerCell :  function(options) {
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
		                }, {
		                    name : 'receivedQuantity',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.text.receivedQuantity'),
		                    cell : 'integer',
		                    sortable : false,
		                    editable : false,
		                    headerCell :  function(options) {
                            	options.column.isSearchable = false;
                            	return new StringHeaderCell(options);
                            }
		                }, {
                            name : 'deliverySchedule',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.expectedQuantity'),
                            cell : function(options) {
                                if(that.module == 'internal' || that.module == 'external') {
                                    return new DeliveryScheduleCell(options);
                                } else {
                                    return new Backgrid.StringCell(options);
                                }
                            },
                            editable : false,
                            headerCell :  function(options) {
                                options.className = 'xs-fixedWidth';
                                return new Backgrid.HeaderCell(options);
                            },
                            columnChooser: {                            	
                            	choosable: false
                            },
                            renderable : that.module == 'internal' || that.module == 'external'
                        }, {
                            name: 'expectedDate',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.expectedDate'),
                            cell : SpannedCell,
                            editable: false,
                            customFilters: ['expectedArrival', 'expectedArrivalFrom', 'expectedArrivalTo'],
                            headerCell :  function(options) {
                                options.dateFilterBindings = {
                                        on: 'expectedArrival',
                                        from: 'expectedArrivalFrom',
                                        to: 'expectedArrivalTo'
                                };
                                return new DateHeaderFilterCell(options);
                            },
                            columnChooser: {                            	
                            	choosable: false
                            },
                            renderable : that.module == 'internal' || that.module == 'external'
                        }, {  
                            name: 'statusFlag',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.statusFlag'),                            
                            cell : SpannedCell,
                            sortable: false,
                            editable: false,
                            headerCell : FlagHeaderCell,
                            columnChooser: {                            	
                            	choosable: false
                            },
                            renderable : that.module == 'internal' || that.module == 'external'
                        }, {
		                    name : 'possibleToReceiveQty',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.possQty'),
		                    sortable : false,
		                    cell : 'integer',
		                    editable : false,
		                    columnChooser: {                            	
                            	choosable: that.module == 'completed'
                            },
		                    renderable : that.module == 'completed'
		                }, {
                            name : 'completeType',
                            label : i18n.t('Gloria.i18n.material.overview.header.status'),
                            cell : 'string',
                            editable : false,
                            headerCell : function(options) {
                            	options.column.type = 'select';
            					options.column.defaultData = [{
            						id : 'COMPLETE',
            						text : i18n.t('COMPLETE')
            					},{
            						id : 'CANCELLED',
            						text : i18n.t('CANCELLED')
            					},{
                                    id : 'RECEIVED',
                                    text : i18n.t('RECEIVED')
                                }];
            					return new DropdownHeaderCell(options);
            				},
            				columnChooser: {                            	
                            	choosable: that.module == 'completed'
                            },
            				renderable : that.module == 'completed'
                        }, {
                            name: 'orderDateTime',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.orderDateTime'),
                            cell : 'date',
                            editable: false,
                            renderable : false,
                            formatter : BackgridDateFormatter,
                			headerCell :  function(options) {
                                options.dateFilterBindings = {
                                        on: 'orderDateTime',
                                        from: 'orderDateTimeFrom',
                                        to: 'orderDateTimeTo'
                                };
                                return new DateHeaderFilterCell(options);
                            }
                        }, {
                            name: 'requiredStaDate',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.requiredStaDate'),
                            cell : 'date',
                            editable: false,
                            renderable : false,
                            formatter : BackgridDateFormatter,
                            headerCell :  function(options) {
                                options.dateFilterBindings = {
                                        on: 'requiredStaDate',
                                        from: 'requiredStaDateFrom',
                                        to: 'requiredStaDateTo'
                                };
                                return new DateHeaderFilterCell(options);
                            }
                        }, {
		                    name : 'consignorName',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.consignorName'),
		                    cell : 'string',
		                    editable : false,
		                    headerCell : StringHeaderCell,
		                    columnChooser: {                            	
                            	choosable: that.module.toUpperCase() == 'INTERNAL'
                            },
		                    renderable : false
		                }, {
                            name: 'supplierName',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.supplierName'),
                            cell : 'string',
                            editable: false,
                            headerCell : StringHeaderCell,
                            columnChooser: {                            	
                            	choosable: that.module != 'internal'
                            },
                            renderable : false
                        }, {
                            name: 'allowedQuantity',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.possibleToReceiveQty'),
                            cell : 'integer',
                            sortable: false,
                            editable: false,
                            renderable : false
                        }, {
                            name: 'materialPartModification',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.materialPartModification'),
                            cell : 'string',
                            editable: false,
                            renderable : false,
                            headerCell : StringHeaderCell
                        }, {
                            name: 'plannedDispatchDate',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.plannedDispatchDate'),
                            cell : 'date',
                            editable: false,
                            renderable : false,
                            formatter : BackgridDateFormatter
                        }, {
                            name: 'eventTime',
                            label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.eventTime'),
                            cell : 'date',
                            editable: false,
                            renderable : false,
                            formatter : BackgridDateFormatter
                        }, {
		                    name : 'deliveryControllerUserId',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.dc'),
		                    cell : Backgrid.StringCell.extend({
                                formatter : {
                                	fromRaw : function(rawValue, model) {
                                		if(model.get('deliveryControllerUserId')) {
                                            return model.get('deliveryControllerUserId') + ' - ' + model.get('deliveryControllerUserName');
                                		} else {
                                			return '';
                                		}
                                    }
                                }
                            }),
		                    editable : false,
		                    columnChooser: {                            	
                            	choosable: false
                            },
                            headerCell : StringHeaderCell,
                            renderable : that.showDCInfo
		                }, {
		                    name : 'materialControllerUserId',
		                    label : i18n.t('Gloria.i18n.deliverycontrol.orderOverview.header.mc'),
		                    cell : Backgrid.StringCell.extend({
                                formatter : {
                                	fromRaw : function(rawValue, model) {
                                		return model.get('materialControllerUserId') ? model.get('materialControllerUserId')
                                        		+ ' - ' + model.get('materialControllerUserName') : ''; 
                                    }
                                }
                            }),
		                    editable : false,
		                    renderable : false,
		                    headerCell : StringHeaderCell,
		                    columnChooser: {                            	
                            	choosable: that.module == 'internal' || that.module == 'external'
                            }
		                }
		             ]
	            });
	
	            // Render the grid
	            var $gridView = this.gridView.render().$el;
	            this.$el.html($gridView);
	            
	            // Initialize the paginator
	            this.paginator = new GloriaPaginator({
	                collection : this.collection,
	                grid: this.gridView,
	                showColumnSettings: this.module == 'external' || this.module == 'internal',
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

    return Gloria.DeliveryControlApp.View.OverviewGridView;
});
