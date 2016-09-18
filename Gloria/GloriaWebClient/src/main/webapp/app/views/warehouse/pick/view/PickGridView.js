define(['app',
        'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'backgrid',
	    'i18next',
	    'views/common/paginator/PaginatorView',
	    'utils/backgrid/stringHeaderCell',   
	    'utils/backgrid/dropdownHeaderCell',
	    'utils/backgrid/dateHeaderCell',
		'utils/backgrid/dateFormatter',
		'utils/backgrid/numberHeaderCell',
	    'utils/backgrid/PickListStatusCell',
	    'utils/backgrid/PickListStatusHeaderCell',
	    'backgrid-select-all',
	    'backgrid-select2-cell',
	    'bootstrap',   
	    'utils/UserHelper',
	    'utils/DateHelper',
	    'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator,
		StringHeaderCell, DropdownHeaderCell, DateHeaderCell, BackgridDateFormatter, NumberHeaderCell, 
		PickListStatusCell, PickListStatusHeaderCell, BackgridSelectAll, 
		BackgridSelect2Cell, Bootstrap, UserHelper, DateHelper, GridUtil) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
	
		View.PickGridView = Marionette.View.extend({
	
	        initialize : function(options) {
	        	this.collection = options.collection;
	            this.setGrid();
	            this.listenTo(this.collection, 'backgrid:selected', this.handleSelectRow);
	            this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
	        }, 
	       
			events : {        
				'click a[id^="code_"]' : 'handlePickListCodeClick'
	        },
	        
	        clearFilter : function() {
	            this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},
	        
	        handlePickListCodeClick : function(e){	
	        	e.preventDefault();
	        	var pickListId = $(e.currentTarget).attr('data-picklistid');	        	
	        	Backbone.history.navigate('warehouse/pick/pickListDetails/' + pickListId, {
					trigger : true
				});
	        },
	        
	        handleSelectRow : _.debounce(function(e) {
	        	var selectedModels = this.gridView.getSelectedModels();
				var isValidStatus = false;
				var isEnableCount = 0;
				var isDisableCount = 0;
				$.each(selectedModels, function(i, model) {
					if (model.get('status')) {
						isValidStatus = true;
					}
	                if(model.get('status') == 'CREATED' || model.get('status') == 'IN_WORK'){
	                    isEnableCount = isEnableCount+1;
	                    Gloria.WarehouseApp.trigger('CancelPickListButton:enable');
	                    Gloria.WarehouseApp.trigger('PickGrid:select', selectedModels);
	                }
	                else if(model.get('status') == 'PICKED'){
	                    isDisableCount = isDisableCount+1;
                        Gloria.WarehouseApp.trigger('CancelPickListButton:disable');
                    }
	                else{
	                    isDisableCount = isDisableCount+1;
	                    Gloria.WarehouseApp.trigger('CancelPickListButton:disable');
	                }
				});
				
				if (!isValidStatus) {
					Gloria.WarehouseApp.trigger('PickGrid:select', selectedModels);
				} else {
					Gloria.WarehouseApp.trigger('CreatePickListButton:disable');
				}
				
				if(isEnableCount >= 1){
				    if(isDisableCount >= 1){
	                    Gloria.WarehouseApp.trigger('CancelPickListButton:disable');
	                }
				}
	        }, 200),
	        
	        clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
	        
			setGrid : function() {
				var that = this;
	            this.gridView = new Backgrid.Grid({
	            	id : 'PickGrid',
	            	className: 'backgrid',
	                collection : that.collection,
					emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
	        	    	// Checkbox column
	        	        name: "",
	        	        cell: "select-row",
	        	        headerCell: "select-all",
	        	    }, {
	        	    	name : 'zoneId',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.zone'),
						cell : 'string',
						editable : false,
						headerCell : StringHeaderCell
	        	    }, {
	        	    	name : 'items',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.items'),
						cell : 'string',
						editable : false,	
		                sortable: false
	        	    },{
						name : 'totalQuantity',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.totalQty'),
						cell : 'integer',
						editable : false,
		                sortable: false
					},		
					{
						name : 'priority',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.priority'),
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
						},
		                sortable: false
					},{
	        	    	name : 'projectId',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.project'),
						cell : 'string',
						editable : false,						
						headerCell : StringHeaderCell,
		                sortable: false
	        	    },{
						name : 'referenceGroup',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.referenceGroup'),
						cell : 'string',
						editable : false,
						headerCell : StringHeaderCell,
		                sortable: false
					},{
						name : 'referenceId',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.referenceId'),
						cell : 'string',
						editable : false,
						headerCell : StringHeaderCell,
		                sortable: false
					},{
						name : 'changeRequestIds',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.changeRequestIds'),
						cell : 'string',
						editable : false,
						headerCell : function(options) {                 
                            options.tooltip = {
                                    'tooltipText': i18n.t('Gloria.i18n.warehouse.pick.header.changeRequestIdsTooltip'),
                                    'displayText': i18n.t('Gloria.i18n.warehouse.pick.header.changeRequestIds')
                            };                       
                            return new StringHeaderCell(options);
                        },
		                sortable: false
					},{
	        	    	name : 'requiredDeliveryDate',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.requiredDeliveryDate'),
						cell : 'date',
						editable : false,						
						formatter : BackgridDateFormatter,						
						headerCell : function(options) {                 
                            options.tooltip = {
                                    'tooltipText': i18n.t('Gloria.i18n.warehouse.pick.header.requiredDeliveryDateTooltip'),
                                    'displayText': i18n.t('Gloria.i18n.warehouse.pick.header.requiredDeliveryDate')
                            };                            
                            return new DateHeaderCell(options);
                        }
	        	    },{
	        	    	name : 'requestListID',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.requestListId'),
						cell : Backgrid.StringCell.extend({
		                    render: function() {
		                        var val = this.model.get('requestListID') || '';
		                        this.$el.html('<span>' + val + '</span>');                           
		                        return this;
		                    }
		                }),
						editable : false,											
						headerCell : StringHeaderCell.extend({							
							actAsNumber: true
						}),
		                sortable: false
	        	    },{
						name : 'outboundLocationId',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.outboundLocationName'),
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
						headerCell : StringHeaderCell,
						filterable : false
					},{
						name : 'requesterUserId',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.requesterUserId'),
						cell: Backgrid.StringCell.extend({
							render: function() {
								  var cid = this.model.get('requesterUserId') || '';
			                      var cname = this.model.get('requesterUserName') || '';
			                      var value = cid + " " + cname;
			                      this.$el.html(value);         		
								  return this;
							}
						}),
						editable : false,
						headerCell : StringHeaderCell,
		                sortable: false
					},{
						name : 'createdDate',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.createdDate'),
						cell : Backgrid.DatetimeCell.extend({
							formatter : {
								fromRaw : function(rawValue) {
									return DateHelper.formatDateTimewithUTC(rawValue); //utc
								}
							}
						}),
						editable : false,
		                sortable: false
					},{
						name : 'pickListCode',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.code'),
						cell : Backgrid.StringCell.extend({
							render: function() {
								  var pickCodeValue = this.model.get('pickListCode') || '';
			                      var pickCodeLinkId = this.model.get('pickListId');
			                      this.$el.html('<a id="code_'+pickCodeLinkId + '" data-picklistid="' + pickCodeLinkId + '">'+pickCodeValue+ '</a>');         		
								  return this;
							}
						}),						
						editable : false,
						headerCell : StringHeaderCell,
		                sortable: false
					},{
						name : 'reservedUserId',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.inPickingBy'),
						cell : 'string',
						headerCell : StringHeaderCell,
						editable : false,
						sortable: false
					}]
	            });
	            
	            // Initialize the paginator
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
                isEnableCount=null;
                isDisableCount=null;
	        }
	    });
	});
	
    return Gloria.WarehouseApp.View.PickGridView;
}); 
