define(['app',
        'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'backgrid',
	    'utils/backgrid/subgrid/SubgridCell',
	    'i18next',
	    'views/common/paginator/PaginatorView',
	    'utils/backgrid/stringHeaderCell',   
	    'utils/backgrid/dropdownHeaderCell',
	    'utils/backgrid/dateHeaderCell',
		'utils/backgrid/dateFormatter',
        'utils/backgrid/numberHeaderCell',
        'utils/backgrid/StringCell',
        'utils/backgrid/IntegerCell',
        'utils/backgrid/clickableRow',
	    'backgrid-select-all',
	    'backgrid-select2-cell',
	    'bootstrap',   
	    'utils/UserHelper',
	    'grid-util',
	    'views/warehouse/pick/view/MarkedGridCell'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, SubgridCell, i18n, GloriaPaginator,
		StringHeaderCell, DropdownHeaderCell, DateHeaderCell, BackgridDateFormatter, NumberHeaderCell, StringCell, IntegerCell,
		ClickableRow, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, UserHelper, GridUtil, MarkedGridCell) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
	
		View.ShipGridView = Marionette.View.extend({
	
	        initialize : function(options) {
	        	this.collection = options.collection;
	        	this.subModule = options.subModule;
	        	this.listenTo(Backbone, 'subgrid:fetch', this.fetchSubgridInfo);
	            this.setGrid();
	            this.listenTo(this.collection, 'backgrid:selected', this.handleSelectRow);
	            this.listenTo(Gloria.WarehouseApp, 'ShipGrid:clearselection', this.clearSelectedModels);
	            this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
	        },
	        
	        clearFilter : function() {
	        	this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
	        
	        fetchSubgridInfo : function(model) {
	        	Gloria.WarehouseApp.trigger('ShipGrid:Subgrid:fetch', model);
			},
			
			events : {
                'click a[id^="pickListCode_"]' : 'handlePickListCodeClick',
                'rowdoubleclicked table.ship tr' : 'rowDoubleClick' //attaching event conditionally.
	        },
	        
	        handleSelectRow : _.debounce(function(model, selected) {
				var selectedModels = this.gridView.getSelectedModels();
	            Gloria.WarehouseApp.trigger('ShipGrid:select', selectedModels);
	        }, 200),
			
	        clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},

			rowDoubleClick : function(e, model) {
				if (model.get('dispatchNoteId')) {
					Gloria.WarehouseApp.trigger('OpenDispatchNoteButton:clicked', model);
				} else {
					Gloria.WarehouseApp.trigger('DispatchButton:clicked', model);
				}
			},
			
			handlePickListCodeClick : function(e) {
	        	e.preventDefault();
	        	e.stopPropagation();
                var pickListId = $(e.currentTarget).attr('data-picklistid');
				Backbone.history.navigate('warehouse/ship/pickListDetails/' + pickListId, {
					trigger : true
				});
	        },
	       
			setGrid : function() {
				var that = this;
				var subModule = that.subModule;
				var subcolumns = [ 
				{
					name : 'shipStatus',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.shipStatus'),
					editable : false,
					sortable: false,
					renderable : subModule == 'inpick',
					cell : function(options) {
                    		return new MarkedGridCell(options);
                    	}
				} ,
				{
					name : 'zoneId',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.zoneId'),
					cell : 'string',
					editable : false,
					sortable: false
				} , {
					name : 'items',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.items'),
					cell : 'string',
					editable : false,
					sortable: false
				}, {
					name : 'totalQuantity',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.totalQuantity'),
					cell : IntegerCell,
					editable : false,
					sortable: false
				}, {
					name : 'projectId',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.projectId'),
					cell : 'string',
					editable : false,
					sortable: false
				}, {
					name : 'referenceGroup',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.referenceGroup'),
					cell : 'string',
					editable : false,
					sortable: false
				}, {
					name : 'referenceId',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.referenceId'),
					cell : 'string',
					editable : false,
					sortable: false
				}, {
					name : 'changeRequestIds',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.changeRequestIds'),
					cell : 'string',
					editable : false,
					sortable: false
				}, {
					name : 'pickListCode',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.pickListCode'),
					cell : Backgrid.StringCell.extend({
						render : function() {
							var pickCodeValue = this.model.get('pickListCode') || '';
							var pickCodeLinkId = this.model.get('pickListId');
                            this.$el.html('<a id="pickListCode_' + pickCodeLinkId + '" data-picklistid="' + pickCodeLinkId + '">' + pickCodeValue + '</a>');               
							return this;
						}
					}),
					editable : false,
					sortable: false
				}, 
				{
					name : 'partInformation',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.partinformation'),
					cell : Backgrid.StringCell.extend({
						render : function() {
							var directSendPartNo = this.model.get('directSendPartNo') || '';
							var directSendPartVersion = this.model.get('directSendPartVersion');
							var directSendPartAffiliation = this.model.get('directSendPartAffiliation');
                            this.$el.html(directSendPartNo+"-"+directSendPartVersion+"-"+directSendPartAffiliation);               
							return this;
						}
					}),
					editable : false,
					sortable: false
				}, {
					name : 'pulledByUserId',
					label : i18n.t('Gloria.i18n.warehouse.ship.header.pulledByUserId'),
					cell : 'string',
					editable : false,
					sortable: false
				}];
	            this.gridView = new Backgrid.Grid({
	            	id : 'ShipGrid',
	            	className: subModule == 'ship'? 'backgrid ship':'backgrid', //attaching event conditionally.
	            	row : ClickableRow,
	                collection : that.collection,
					emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
	        	    	// Checkbox column
	        	        name: "",
	        	        cell: "select-row",
	        	        headerCell: "select-all",
	        	    },{
						name : "subgrid",
						label : "",
						cell : SubgridCell,
						optionValues : subcolumns,
						subModule : subModule,
						emptyText: i18n.t('Gloria.i18n.general.noRow'),
	        	    },{
	        	    	name : 'materialDirectSendType',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.directsend'),
						renderable : subModule == 'ship',
						cell : Backgrid.StringCell.extend({
		                    render: function() {
		                    	if(this.model.get('materialDirectSendType') == 'YES') {
		                    		this.$el.html('<i class="fa fa-paper-plane-o color-blue"></i>');
		                    	} else {
		                    		this.$el.html('');
		                    	};
		                        return this;
		                    }
		                }),
						editable : false,	
						sortable: false,
						headerCell : function(options) {
							options.column.type = 'select';
							options.column.defaultData = [{
								id : 'YES',
								text : i18n.t('Gloria.i18n.warehouse.ship.header.yes')
							},{
								id : 'NO',
								text : i18n.t('Gloria.i18n.warehouse.ship.header.no')
							}];
							return new DropdownHeaderCell(options);
						}
	        	    },{
	        	    	name : 'id',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.requestListId'),
						cell : 'string',
						editable : false,
						headerCell : StringHeaderCell.extend({    		                	
		                	actAsNumber: true
		                })
	        	    },{
						name : 'requestUserId',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.requesterUserId'),
						cell: Backgrid.StringCell.extend({
							render: function() {
								  var cid = this.model.get('requestUserId') || '';
			                      var cname = this.model.get('requesterName') || '';
			                      var value = cid + " - " + cname;
			                      this.$el.html(value);         		
								  return this;
							}
						}),
						editable : false,
						headerCell : StringHeaderCell
					},{
						name : 'priority',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.priority'),
						cell : 'string',
						editable : false,
						headerCell : function(options) {
							options.column.type = 'select';
							options.column.defaultData = [{
								id : '1',
								text : '1'
							},{
								id : '2',
								text : '2'
							},{
								id : '3',
								text : '3'
							}];
							return new DropdownHeaderCell(options);
						}
					},{
	        	    	name : 'requiredDeliveryDate',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.requiredDeliveryDate'),
						cell : 'string',
						editable : false,						
						formatter : BackgridDateFormatter,
						headerCell : DateHeaderCell
	        	    },{
						name : 'outboundLocationId',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.outboundLocationName'),
						cell: Backgrid.StringCell.extend({
							render: function() {
							    var deliveryAddressId = this.model.get('deliveryAddressId') || '';
                                var deliveryAddressName = this.model.get('deliveryAddressName') || '';
                                var value = deliveryAddressId ? (deliveryAddressId + " - " + deliveryAddressName) : deliveryAddressName;
                                this.$el.html(value);
								return this;
							}
						}),
						editable : false,
						headerCell : StringHeaderCell
					},{
						name : 'dispatchNoteNumber',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.dispatchNoteNo'),
						cell : 'string',
						editable : false,
						renderable : subModule == 'ship',
						headerCell : function(options) {
		                    options.tooltip = {
		                        'tooltipText': i18n.t('Gloria.i18n.warehouse.ship.header.dispatchNoteNoTooltip'),
		                        'displayText': i18n.t('Gloria.i18n.warehouse.ship.header.dispatchNoteNo')
		                    };                    
		                    return new StringHeaderCell(options);
		                }
					},{
						name : 'carrier',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.carrier'),
						cell : StringCell,
						renderable : subModule == 'ship',
						editable : function(model, column) {
							var editable = false;
							 if(model && model.get('dispatchNoteNumber')){
								 editable = true;
							 }
							 return editable && UserHelper.getInstance().hasPermission('edit', ['Ship']);
						},
						headerCell : StringHeaderCell
					},{
						name : 'trackingNo',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.trackingNo'),
						cell : StringCell,
						renderable : subModule == 'ship',
						editable : function(model, column) {
							var editable = false;
							 if(model && model.get('dispatchNoteNumber')){
								 editable = true;
							 }
							 return editable && UserHelper.getInstance().hasPermission('edit', ['Ship']);
						},
						headerCell : function(options) {
		                    options.tooltip = {
		                        'tooltipText': i18n.t('Gloria.i18n.warehouse.ship.header.trackingNoTooltip'),
		                        'displayText': i18n.t('Gloria.i18n.warehouse.ship.header.trackingNo')
		                    };                    
		                    return new StringHeaderCell(options);
		                }
					},{
	        	    	name : 'status',
						label : i18n.t('Gloria.i18n.warehouse.ship.header.status'),
						renderable : subModule == 'ship',
						cell : Backgrid.StringCell.extend({
                            formatter : {
                                fromRaw : function(rawValue) {
                                    return i18n.t('Gloria.i18n.warehouse.ship.requestListStatus.' + rawValue);
                                }
                            }
                        }),
						editable : false,	
						sortable: false,
						headerCell : function(options) {
							options.column.type = 'select';
							options.column.defaultData = [{
								id : 'PICK_COMPLETED',
								text : i18n.t('Gloria.i18n.warehouse.ship.requestListStatus.PICK_COMPLETED')
							},{
								id : 'READY_TO_SHIP',
								text : i18n.t('Gloria.i18n.warehouse.ship.requestListStatus.READY_TO_SHIP')
							},{
								id : 'SHIPPED',
								text : i18n.t('Gloria.i18n.warehouse.ship.requestListStatus.SHIPPED')
							}];
							return new DropdownHeaderCell(options);
						}
	        	    }]
	            });
	            this.paginator = new GloriaPaginator({
					collection : that.collection,
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
            
            onDestroy: function() {
                this.gridView.remove();
                this.paginator.remove();
            }
	    });
	});
	
    return Gloria.WarehouseApp.View.ShipGridView;
}); 
