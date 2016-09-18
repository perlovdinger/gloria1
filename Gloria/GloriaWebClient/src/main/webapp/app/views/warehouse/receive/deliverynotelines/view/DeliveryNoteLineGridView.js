define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'i18next',	
		'utils/backgrid/clickableRow',
		'utils/backgrid/IntegerCell',
		'views/common/zone/ZoneSelectCell',
		'backgrid-select-all',
	    'backgrid-select2-cell',
		'bootstrap',
        'grid-util',
        'utils/backgrid/SpannedCell',
        'views/warehouse/receive/deliverynotelines/view/DeliveryNoteCell',
        'grid-util'
], function(Gloria, _, Handlebars, Backbone, Marionette, backgrid, i18n, ClickableRow, IntegerCell, ZoneSelectCell,
		BackgridSelectAll, BackgridSelect2Cell, Bootstrap, GridUtil, SpannedCell, DeliveryNoteCell, GridUtil) {

	Gloria.module('WarehouseApp.Receive.DeliveryNoteLineInformation.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.GridView = Marionette.View.extend({

			initialize : function(options) {
				this.transportLabels = options.transportLabels;
				this.collection = options.collection;
				this.deliveryNoteModel = options.deliveryNoteModel;
				this.listenTo(this.collection, 'add remove change', this.handleSelectRow);
				this.setGrid();
				this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:clearSelectedModels', this.clearSelectedModels);
			},

			events : {
				'change .select-row-cell input' : 'handleSelectRow',
				'change .select-all-header-cell input' : 'handleSelectRow',
				'rowdoubleclicked table.backgrid tr' : 'rowDoubleClick'
			},
			
			rowDoubleClick : function(e, model) {
				var checkToReceiveQty = function(model) {
					var isQtyEntered = false;
					_.each(model.directsends.models, function(thisModel) {
						if(!isQtyEntered) {
							isQtyEntered = thisModel.get('toReceiveQty') > 0;
						}
					});
					return isQtyEntered;
				};
				if(model && model.directsends && checkToReceiveQty(model)) {
					Gloria.WarehouseApp.trigger('DeliveryNoteLine:addInformation', model.id);
				}
			},	
			
			handleSelectRow : function(e, model) {
			    var selectedModels = this.gridView.getSelectedModels();
                var selectedRows = _.map(selectedModels, function(model) {
                    if(model && model.id)
                        return model.id;
                });
                if($('.backgrid').find('.error').length == 0){
                	Gloria.WarehouseApp.trigger('select:deliveryNoteLineGrid', selectedRows, selectedModels);
                }
			},
			
			clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},

			setGrid : function() {
				var that = this;
				var receiveType = that.deliveryNoteModel.get('receiveType') && that.deliveryNoteModel.get('receiveType').toUpperCase();
				// Initialize the grid
				this.gridView = new Backgrid.Grid({
					id : 'DeliveryNoteLineGrid' + that.deliveryNoteModel.get('receiveType'),
					row : ClickableRow,
					collection : this.collection,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [
			            {
			                // Checkbox column
			                name : "",
			                cell : "select-row",
			                headerCell : "select-all",
			            },
			            {
			                name : 'hasNotes',
			                label : i18n.t('Gloria.i18n.warehouse.receive.text.hasNotes'),
			                cell : Backgrid.StringCell.extend({
			                    render: function() {
			                    	if(this.model.get('hasNotes') == true) {
			                    		this.$el.html('<i class="fa fa-envelope-o"></i>');
			                    	} else {
			                    		this.$el.html('');
			                    	};                  
			                        return this;
			                    }
			                }),
			                editable : false,
			                sortable: false,
			                renderable: receiveType == 'REGULAR' || receiveType == 'TRANSFER'
			            },
			            {
			                name : 'projectId',
			                label : i18n.t('Gloria.i18n.warehouse.receive.text.projectId'),
			                cell : 'string',
			                editable : false,
			                sortable: false
			            },
			            {
			                name : 'referenceId',
			                label : i18n.t('Gloria.i18n.warehouse.receive.text.referenceId'),
			                cell : 'string',
			                editable : false,
			                sortable: false,
			                renderable: receiveType == 'RETURN'
			            },
			            {
			                name : 'partNumber',
			                label : i18n.t('Gloria.i18n.warehouse.receive.text.partNo'),
			                cell : function(options) {
			                	if(options.model.get('receivedDetailsUpdated')) {
			                        var CustomStringCell  = Backgrid.Cell.extend({
			                            className : 'string-cell attachment-icon',
			                            formatter : Backgrid.StringFormatter
			                        });
			                		return new CustomStringCell(options);
			                	} else {
			                		return new Backgrid.StringCell(options);
			                	}
			        		},
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
			                name : 'partVersion',
			                label : i18n.t('Gloria.i18n.warehouse.receive.partVersion'),
			                cell : 'string',
			                editable : false,
			                sortable: false
			            },
			            {
			                name : 'partName',
			                label : i18n.t('Gloria.i18n.warehouse.receive.partName'),
			                cell : 'string',
			                editable : false,
			                sortable: false
			            },
			            {
			                name : 'partModification',
			                label : i18n.t('Gloria.i18n.warehouse.receive.partModification'),
			                cell : 'string',
			                editable : false,
			                sortable: false
			            },
			            {
			                name : 'possibleToReceiveQuantity',
			                label : receiveType == 'REGULAR' ? i18n.t('Gloria.i18n.warehouse.receive.text.allowedQuantity') 
			                		: (receiveType == 'RETURN' ?  i18n.t('Gloria.i18n.warehouse.receive.text.shippedQty') 
			                		: i18n.t('Gloria.i18n.warehouse.receive.text.materialLineQuantity')),               
			                cell : 'integer',
			                editable : false,
			                sortable: false,
			                renderable: true
			            },
			            {
			                name : 'orderLineReceivedQuantity',             
			                label : i18n.t('Gloria.i18n.warehouse.receive.text.receivedQty'),               
			                cell : 'integer',
			                editable : false,
			                sortable: false,
			                renderable: receiveType == 'REGULAR'
			            },           
			            {
			                name : 'directSendQuantity',             
			                label : i18n.t('Gloria.i18n.warehouse.receive.text.directSendQuantity'),               
			                cell : 'integer',			                
			                editable : false,
			                sortable: false,
			                renderable: receiveType == 'REGULAR'
			            },
			            {
			                name : 'directsends',
			                label : receiveType == 'RETURN' ? i18n.t('Gloria.i18n.warehouse.receive.text.toReturnQty') 
			                		:  i18n.t('Gloria.i18n.warehouse.receive.toReceiveQty'),
			                cell : function(options) {
			                	options.receiveType = receiveType;
			                	options.transportLabels = that.transportLabels;
			                	return new DeliveryNoteCell(options);
			                },
			                editable : false,
                            sortable: false
			            },
			            {
			                name : 'nextZoneCode',
			                label : i18n.t('Gloria.i18n.warehouse.receive.nextLocation'),
			                cell : SpannedCell,
			                editable : false,
			                sortable: false
			            },
			            {
			                name : 'transportLabelId',
			                label : i18n.t('Gloria.i18n.warehouse.receive.transportLabel'),
			                cell : SpannedCell,
			                editable : false,
			                sortable: false
			            },
			            {
			                name : 'binLocation',
			                label : i18n.t('Gloria.i18n.warehouse.receive.binLocation'),
			                cell : SpannedCell,
			                editable : false,
			                sortable: false
			            }]
				});				
			},

			render : function() {
				// Render the grid
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

			onDestroy : function() {
				this.gridView.remove();				
			}
		});
	});

	return Gloria.WarehouseApp.Receive.DeliveryNoteLineInformation.View.GridView;
});