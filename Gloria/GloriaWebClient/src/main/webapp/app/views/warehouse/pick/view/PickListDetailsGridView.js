define(['app',
        'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'backgrid',
	    'i18next',
	    'utils/backgrid/stringHeaderCell',
	    'utils/backgrid/IntegerCell',
	    'utils/backgrid/PickListStatusCell',
	    'backgrid-select-all',
	    'backgrid-select2-cell',
	    'bootstrap',   
	    'utils/UserHelper',
	    'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, i18n,
		StringHeaderCell, IntegerCell, PickListStatusCell, BackgridSelectAll, BackgridSelect2Cell, Bootstrap, UserHelper, GridUtil) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
	
		View.PickListDetailsGridView = Marionette.View.extend({
	
	        initialize : function(options) {
	        	this.isEditable = options.status != 'PICKED' && !options.lockFailed;
	        	this.collection = options.collection;
	            this.setGrid();
	            this.listenTo(this.collection, 'backgrid:selected', this.handleSelectRow);
	            this.listenTo(Gloria.WarehouseApp, 'Pick:Grid:validate', this.validateGridData);
	            this.listenTo(this.collection, 'change:pickedQuantity', this.updateCheckBox);
	            this.listenTo(this.collection, 'backgrid:error', this.showGridErrors);
	        },
	        
	        showGridErrors : function(model, column, val) {
				Gloria.trigger('showAppMessageView', {
					type : 'error',
					title : i18n.t('errormessages:general.title'),
					message : new Array({
						message : i18n.t('errormessages:errors.GLO_ERR_058')
					})
                });
			},
	       
			setGrid : function() {
				var that = this;	
	            this.gridView = new Backgrid.Grid({
	            	className: 'backgrid',
	            	id : 'PickListDetailsGrid',
	                collection : that.collection,
					emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
	        	    	// Checkbox column
	        	        name: "",
	        	        cell: "select-row",
	        	        headerCell: "select-all"
	        	    }, {
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
	        	    }, {
	        	    	name : 'pPartNumber',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.pPartNumber'),
						cell : 'string',
						editable : false,						
						headerCell : StringHeaderCell,
		                sortable: false
	        	    },{
	        	    	name : 'pPartVersion',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.pPartVersion'),
						cell : 'string',
						editable : false,					
		                sortable: false
	        	    }, {
	        	    	name : 'pPartName',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.pPartName'),
						cell : 'string',
						editable : false,						
						headerCell : StringHeaderCell,
		                sortable: false
	        	    },{
						name : 'pPartModification',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.pPartModification'),
						cell : 'string',
						editable : false,
		                sortable: false
					},		
					{
						name : 'unitOfMeasure',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.unitOfMeasure'),
						cell : 'string',
						editable : false,						
		                sortable: false
					},{
	        	    	name : 'binlocationBalance',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.binlocationBalance'),
						cell : 'string',
						editable : false,
		                sortable: false
	        	    },{
						name : 'binLocationCode',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.binLocation'),
						cell : 'string',
						editable : false,					
		                sortable: false
					},{
						name : 'quantity',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.quantity'),
						cell: Backgrid.StringCell.extend({
							render: function() {
								  var qty = parseInt(this.model.get('quantity')) || 0 ;
			                      this.$el.html(qty);         		
								  return this;
							}
						}),
						editable : false,						
		                sortable: false
					},{
						name : 'pickedQuantity',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.pickedQuantity'),
						cell :  IntegerCell,
						formatter : {
							fromRaw: function (rawData, model) {
								model.set('pickedQuantity', (rawData || parseInt(model.get('quantity'))));
								return model.get('pickedQuantity');
							},
							toRaw : function(formattedData, model) {								
								return (formattedData == Math.round(formattedData).toString() && (parseInt(formattedData) >= 0) &&								
										(parseInt(formattedData)<= parseInt(model.get('quantity')))) ? formattedData : undefined;
							}
						},
		                editable : this.isEditable,
		                sortable: false
					},{
						name : 'balanceExceeded',
						label : i18n.t('Gloria.i18n.warehouse.pick.header.balanceExceeded'),
						cell : Backgrid.BooleanCell.extend({
							events : {
								'change input[id^="balanceExceeded_"]' : function(e) {
									this.model.set(this.column.get('name'), e.currentTarget.checked, {
										silent : true
									});
								}
							},
		                    render: function() {
								Backgrid.BooleanCell.prototype.render.apply(this, arguments);
								this.$el.empty();
								var model = this.model;
								this.$el.append($('<input>', {
									id : 'balanceExceeded_'	+ model.get('id'),
									tabIndex : -1,
									type : 'checkbox',
									checked : model.get('balanceExceeded'),
									disabled : !that.isEditable
								}));
								return this;
		                    }
		                }),                
		                editable : false,
		                sortable : false,
		                renderable : !this.isPicked
					}]
	            });
			},			
			
			handleSelectRow : _.debounce(function(e) {
	        	var selectedModels = this.gridView.getSelectedModels();
				Gloria.WarehouseApp.trigger('PickListDetailsGrid:select', selectedModels);
	        }, 200),
	        
			updateCheckBox : function(model, value) {
				if(model.get('quantity') != value) {
					model.set('balanceExceeded', false);
					$('input#balanceExceeded_' + model.id).attr('checked', false);
					$('input#balanceExceeded_' + model.id).attr('disabled', true);
				} else {
					$('input#balanceExceeded_' + model.id).removeAttr('disabled');
				}
			},
			
			validateGridData : function(action) {
				// Grid Validation has been taken care by backgrid itself,
				// so check if there is no error in any cell to submit!
				if($('table.backgrid tr:has(td.error)').length != 0) return;
				switch(action) {
					case 'pick':
						Gloria.WarehouseApp.trigger('PickButton:clicked', this.collection);
						break;
					default:
						Gloria.WarehouseApp.trigger('Pick:PickAndMarkAsShip');
						break;
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
	            var $gridView = this.gridView.render().$el;
	            this.$el.html($gridView);	          
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
            }
	    });
	});
	
    return Gloria.WarehouseApp.View.PickListDetailsGridView;
}); 
