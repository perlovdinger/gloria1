define(['app',
        'jquery',
        'backgrid',
        'bootstrap',
        'i18next',
		'utils/backgrid/IntegerCell',
		'utils/backgrid/NestedGridCell',
		'views/warehouse/qualityinspection/overview/view/TransportLabelSelectCell',
		'views/warehouse/qualityinspection/overview/view/BinLocationCell',
		'views/warehouse/receive/deliverynotelines/view/HighlightableRow',
		'utils/UserHelper'
], function(Gloria, $, Backgrid, Bootstrap, i18n, IntegerCell, NestedGridCell, TransportLabelSelectCell, BinLocationCell, HighlightableRow, UserHelper) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.DeliveryNoteCell = NestedGridCell.extend({

			className : (NestedGridCell.prototype.className || '') + ' no-padding',

			attributes : {
				'colspan' : '4'
			},
			
			columnMap: {
                'directsends': 'toReceiveQty',
                'nextZoneCode': 'nextZoneCode',
                'transportLabelId': 'transportLabelId',
                'binLocation': 'binLocation'
            },

			initialize : function(options) {
				this.type = options.receiveType;
				this.transportLabels = options.transportLabels;
				this.makeNestedGridOptions();
				NestedGridCell.prototype.initialize.apply(this, arguments);
				this.listenTo(this.model.collection, 'backgrid:refresh', function() {
					Gloria.WarehouseApp.trigger('DeliveryNoteLine:sublines:fetch', {
	                    source: this,
	                    model: this.model
	                });
                });
				
				this.listenTo(this.model.directsends, 'backgrid:error', this.showErrors);
			},
			
			showErrors: function(model, column, val) {
				var that = this;
				Gloria.trigger('showAppMessageView', {
					type : 'error',
					title : i18n.t('errormessages:general.title'),
					message : new Array({
						message : that.invalidDamagedQty ? i18n.t('errormessages:errors.GLO_ERR_071') : i18n.t('errormessages:errors.GLO_ERR_043')
					})
				});
			},

			makeNestedGridOptions : function() {
				var that = this;
				return this.nestedGridOptions = {
					className : 'table no-margin nested-table',
					row : HighlightableRow.extend({
                        conditionCallback : function() {
                            if (this.model.get('directSend')) { /* Since next Location is not using any Customised cell , by default it follows this code*/
                                return 'background-orange';
                            } else {
                                return 'background-white';
                            }
                        }
                    }),
					collection : that.model.directsends,
					emptyText : i18n.t('Gloria.i18n.general.noRow'),
					columns : [{
						name : 'toReceiveQty',
						cell : IntegerCell,
						cell : function(options) {
							if(options.model.get('directSend')) {
								options.className = 'background-orange';
							}
							return new IntegerCell(options);
						},
						formatter : {
							fromRaw : function(rawData, model) {
								if(rawData == 0) rawData = '';
								return rawData;
							},
							toRaw : function(formattedData, model) {								
								if(!formattedData) { // Support RESET
									return 0;
								}
								if (that.type == 'REGULAR') {
									var thisQty;
									if(model.get('directSend')) {
										thisQty = that.model.get('directSendQuantity');
									} else {
										thisQty = that.model.get('possibleToReceiveQuantity') - that.model.get('orderLineReceivedQuantity')
													- that.model.get('directSendQuantity');
									}
									that.invalidDamagedQty = parseInt(formattedData) < that.model.get('damagedQuantity');
									return (formattedData == Math.round(formattedData).toString() && parseInt(formattedData) > 0
											&& (parseInt(formattedData) <= parseInt(thisQty))
									        && (parseInt(formattedData) >= that.model.get('damagedQuantity'))) ? formattedData : undefined;
								} else {
									return (formattedData == Math.round(formattedData).toString() && parseInt(formattedData) > 0 
											&& (parseInt(formattedData) <= parseInt(that.model.get('possibleToReceiveQuantity')))) ? formattedData : undefined;
								}
							}
						},
						editable : UserHelper.getInstance().hasPermission('edit', ['GoodsReception']),
						sortable : false
					},
					{
						name : 'nextZoneCode',
						cell : Backgrid.StringCell.extend({
							formatter : {
								fromRaw : function(rawValue, model) {
									if(rawValue && rawValue.toUpperCase() == 'QI') {
										if(model.get('directSend')) {
											return rawValue + ' (' + i18n.t('Gloria.i18n.warehouse.storageRoomType.SHIP') + ')';
										} else {
											return rawValue + ' (' + i18n.t('Gloria.i18n.warehouse.storageRoomType.STORE') + ')';
										}
									} else {
										return rawValue;
									}
								}
							}
						}),
						editable : false,
						sortable : false
					}, {
						name : 'transportLabelId',
						cell : function(options) {
							if(options.model.get('directSend')) {
								options.className = 'background-orange';
							}
							options.transportLabels = that.transportLabels;
							return new TransportLabelSelectCell(options);
						},
						formatter : {
                            fromRaw: function (rawData, model) {
                                return model.get('transportLabelCode') || '';
                            }
						},
						editable : UserHelper.getInstance().hasPermission('edit', ['GoodsReception']),
						sortable : false
					}, {
						name : 'binLocation',
						cell : function(options) {
							if(options.model.get('directSend')) {
								options.className = 'background-orange';
							}
							options.defaultSelect = options.model.get('binLocation');
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
						editable : UserHelper.getInstance().hasPermission('edit', ['GoodsReception']),
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
