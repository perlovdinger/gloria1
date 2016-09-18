define(['app',
        'jquery',
        'backgrid',
        'bootstrap',
        'i18next',
		'utils/backgrid/IntegerCell',
		'utils/backgrid/NestedGridCell',		
		'views/warehouse/qualityinspection/overview/view/TransportLabelSelectCell',
		'views/warehouse/qualityinspection/overview/view/BinLocationCell',
		'utils/UserHelper'
], function(Gloria, $, Backgrid, Bootstrap, i18n, IntegerCell, NestedGridCell, TransportLabelSelectCell, BinLocationCell, UserHelper) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.DeliveryNoteCell = NestedGridCell.extend({

			className : (NestedGridCell.prototype.className || '') + ' no-padding',

			attributes : {
				'colspan' : '4'
			},
			
			columnMap: {
                'directsends': 'toApproveQty',
                'nextZoneCode': 'nextZoneCode',
                'transportLabelId': 'transportLabelId',
                'binLocation': 'binLocation'
            },

			initialize : function(options) {
				var that = this;
				this.module = options.module;
				this.transportLabels = options.transportLabels;
				this.makeNestedGridOptions();
				NestedGridCell.prototype.initialize.apply(this, arguments);
				this.listenTo(this.model.collection, 'backgrid:refresh', function() {
					Gloria.WarehouseApp.trigger('QI:DeliveryNoteLine:sublines:fetch', {
	                    source: this,
	                    model: this.model,
	                    transportLabels : that.transportLabels
	                });
                });
				
				this.listenTo(this.model.directsends, 'backgrid:error', this.showErrors);
			},
			
			showErrors: function(model, column, val) {
				Gloria.trigger('showAppMessageView', {
					type : 'error',
					title : i18n.t('errormessages:general.title'),
					message : new Array({
						message : i18n.t('errormessages:errors.GLO_ERR_012')
					})
				});
			},

			makeNestedGridOptions : function() {
				var that = this;
				return this.nestedGridOptions = {
					className : 'table no-margin nested-table',
					collection : that.model.directsends,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [{
						name : 'toApproveQty',
						cell : IntegerCell,
						formatter : {
                            fromRaw: function (rawData, model) {
                                return rawData;
                            },
                            toRaw : function(formattedData, model) {                            	
                            	if(!formattedData) { // Support RESET
									return null;
								}
                            	if (that.module == 'blockedPart') {
                            	    var thisQty;
                                    if(model.get('directSend')) {
                                        thisQty = that.model.get('directSendQuantity');
                                    } else {
                                        thisQty = that.model.get('blockedQuantity') - that.model.get('directSendQuantity');
                                    }
                                    return (formattedData == Math.round(formattedData).toString() && parseInt(formattedData) >= 0 
                                            && (parseInt(formattedData) <= parseInt(thisQty))) ? formattedData : undefined;
								} else {
								    var thisQty;
                                    if(model.get('directSend')) {
                                        thisQty = that.model.get('directSendQuantity');
                                    } else {
                                        thisQty = that.model.get('receivedQuantity') - that.model.get('directSendQuantity');
                                    }
                                    return (formattedData == Math.round(formattedData).toString() && parseInt(formattedData) >= 0 
                                            && (parseInt(formattedData) <= parseInt(thisQty))) ? formattedData : undefined;
								}
                            }
                        },
						editable : UserHelper.getInstance().hasPermission('edit', ['QualityInspection']),
						sortable : false
					},
					{
						name : 'nextZoneCode',
						cell : 'string',
						editable : false,
						sortable : false
					}, {
						name : 'transportLabelId',
						cell : function(options) {							
							options.transportLabels = that.transportLabels;
							return new TransportLabelSelectCell(options);
						},
						formatter : {
                            fromRaw: function (rawData, model) {
                                return model.get('transportLabelCode') || '';
                            }
						},						
						editable : UserHelper.getInstance().hasPermission('edit', ['QualityInspection']),
						sortable : false
					}, {
						name : 'binLocation',
						cell : function(options) {							
							options.defaultSelect = options.model.get('binLocationCode');
							options.triggerInitially = false;
							options.select2Options = {								
								initSelection: function(element, callback) {
									callback({
										id: options.model.get('binLocation'),
										text: options.model.get('binLocationCode')
									});
								}
							};
							return new BinLocationCell(options);
						},
						formatter : {
                            fromRaw: function (rawData, model) {
                                return model.get('binLocationCode') || '';
                            }
						},
						editable : UserHelper.getInstance().hasPermission('edit', ['QualityInspection']),
						sortable : false
					}]
				};
			},

			render : function() {
				NestedGridCell.prototype.render.apply(this, arguments);
				this.grid && this.grid.header.remove();
				return this;
			},

			remove : function() {
				NestedGridCell.prototype.remove.apply(this, arguments);
			}
		});
	});

	return Gloria.WarehouseApp.View.DeliveryNoteCell;
});
