define(['app',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'i18next',
		'backbone.syphon',
		'jquery-validation',
		'views/mobile/warehouse/view/common/TransportLabelSelectorView',
		'utils/backbone/GloriaModel',
		'utils/UserHelper',
		'hbs!views/mobile/warehouse/view/receive/quantity-item'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Syphon, Validation, TransportLabelSelectorView, Model, UserHelper, compiledTemplate) {

	Gloria.module('WarehouseApp.Receive.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.QuantityItemView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.model = options.model;
				this.dnlModel = options.dnlModel;
				this.module = options.module;
				this.listenTo(Gloria, 'scanner:scanned', this.onScan);
			},

			events : {
				'focusin input[id^="receivedQuantity"],input[id^="toShipQuantity"],input[id^="binLocation"]' : 'onEditStarted',
				'focusout input[id^="receivedQuantity"],input[id^="toShipQuantity"]' : 'onEdited',
				'change select[id^="transportLabelId"]' : 'updateTransportLabelId',
				'focusout input[id^="binLocation"]' : 'handleBinLocationChange',
			},
			
			onScan: function(options) {
				 if(!options || !options.data) return;
				 this.$('input#binLocation' + this.model.cid).val(scannedData.data);
				 this.updateBinLocation(scannedData.data);
			},
			
			onEditStarted: function(e) {
				this.model.trigger('edit:started', this.model, e.currentTarget.id);
			},
			
			onEdited: function(e) {
				this.model.trigger('edit:ended', this.model, e.currentTarget.id);
				if($(e.target).val().length == 0) {
					$(e.target).val('0');
				}
				this.updateQuantity($(e.target).val(), e);
			},
			
			handleBinLocationChange: function(e) {
				this.updateBinLocation($(e.target).val(), e);
			},
			
			updateBinLocation: function(val, e) {
				var that = this;
				that.model.trigger('edit:ended', this.model, e.currentTarget.id);
				this.removeError('binLocation');
				if(val) {
					this.listenTo(Gloria.WarehouseApp, 'Receive:BinLocation:invalid', function(binLocationCode) {
						var $binLocations = that.$('input[id^="binLocation"]');
						$.each($binLocations, function(index, element) {
							if($(element).val() == binLocationCode) {
								$(element).closest('div').addClass('has-error');
								that.model.trigger('edit:started', this.model, e.currentTarget.id);
							}
						});
					});
					this.listenTo(Gloria.WarehouseApp, 'Receive:dnsl:saved', function(binLocationCode) {
						that.model.trigger('edit:ended', this.model);
					});
					Gloria.WarehouseApp.trigger('Receive:dnsl:save:binLocation', this.model, this.dnlModel, val);
				} else {
					this.model.set('binLocation', null);
					$('#receive').removeClass('disabled');
				}
			},

			updateQuantity : function(value, e) {
				var maxToReceiveQty = this.model.get('directSend') ? this.dnlModel.get('directSendQuantity') : 
						(this.dnlModel.get('possibleToReceiveQuantity') - this.dnlModel.get('orderLineReceivedQuantity') - this.dnlModel.get('directSendQuantity'));
				this.listenTo(Gloria.WarehouseApp, 'Receive:dnsl:saved', function(binLocationCode) {
					this.model.trigger('edit:ended', this.model, e.currentTarget.id);
				});
				if ((this.module == 'regular')
						&& value == Math.round(value).toString()
						&& parseInt(value) >= 0 && (parseInt(value) <= maxToReceiveQty)) {
					this.removeError('receivedQuantity');
					this.model.set('toReceiveQty', value);
					Gloria.WarehouseApp.trigger('Receive:dnsl:save', this.model, this.dnlModel);
				} else if ((this.module == 'transfer' || this.module == 'return' || this.module == 'returntransfer')
						&& value == Math.round(value).toString() && parseInt(value) >= 0
						&& (parseInt(value) <= parseInt(this.dnlModel.get('possibleToReceiveQuantity')))) {
					this.removeError('receivedQuantity');
					this.model.set('toReceiveQty', value);
					Gloria.WarehouseApp.trigger('Receive:dnsl:save', this.model, this.dnlModel);
				} else {
					this.model.get('directSend') ? this.addError('toShipQuantity') : this.addError('receivedQuantity');
				}
			},

			updateTransportLabelId : function(e) {
				var value = e.target.value;
				this.model.set('transportLabelId', value || 0);
				Gloria.WarehouseApp.trigger('Receive:dnsl:save', this.model, this.dnlModel);
			},

			removeError : function(fieldName) {
				this.$('#' + fieldName + this.model.cid).closest('div').removeClass('has-error');
			},

			addError : function(fieldName) {
				this.$('#' + fieldName + this.model.cid).closest('div').addClass('has-error');
			},

			transportLabelSelector : function(options) {
				var csv = new TransportLabelSelectorView(options.hash);
				var returnString = new Handlebars.SafeString(csv.el.outerHTML);
				csv.destroy();
				delete csv;
				return returnString;
			},

			render : function() {
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {},
					cid : this.model.cid,
					transportLabelSelector : this.transportLabelSelector,
					module : this.module,
					dnlModel : this.dnlModel ? this.dnlModel.toJSON() : {},
					directSend : this.model.get('directSend'),
					hasEditAccess : UserHelper.getInstance().hasPermission('edit', ['MobileReceive']),
					readOnly : !UserHelper.getInstance().hasPermission('edit', ['MobileReceive'])
				}));
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.Receive.View.QuantityItemView;
});
