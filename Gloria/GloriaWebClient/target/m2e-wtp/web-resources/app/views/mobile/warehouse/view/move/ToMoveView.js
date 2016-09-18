define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'backbone.syphon',
		'jquery-validation',
		'hbs!views/mobile/warehouse/view/move/to-move'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Syphon, Validation, compiledTemplate) {

	Gloria.module('WarehouseApp.Move.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ToMoveView = Marionette.View.extend({

			initialize : function(options) {
				this.listenTo(Gloria, 'scanner:scanned', this.onScan);
				this.listenTo(Gloria.WarehouseApp, 'loaded:inventory', this.loadedInventory);
			},

			template : compiledTemplate,
			
			onScan: function(scannedData) {				
				if(scannedData && ('AZTEC' == scannedData.type || 'QR' == scannedData.type)) {
					var data;				
					try{
						data = JSON.parse(scannedData.data.trim() || '{}');
					} catch(e) {
						alert(e.message);
					}					
					this.$('input#partAffiliation').val(data.a);
					this.$('input#partNumber').val(data.no);
					this.$('input#partVersion').val(data.v);
					this.$('input#partName').val(data.n);
					this.$('input#partModification').val(data.m);
				} else {
					this.$('input#binLocation').val(scannedData.data);
				}				
				this.toMove();				
			},
			
			events: {
				'shown.bs.collapse': 'updateIcon',
				'hidden.bs.collapse': 'updateIcon'
			},
			
			updateIcon: function(e) {
				var element;
				if(e.type == 'shown') {
					element = this.$(e.target).parent().find('.glyphicon-chevron-right');
					element.removeClass('glyphicon-chevron-right').addClass('glyphicon-chevron-down');
				} else {
					element = this.$(e.target).parent().find('.glyphicon-chevron-down');
					element.removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-right');
				}
			},

			buttonEvents : function() {
				return {
					next : {
						label : 'Gloria.i18n.buttons.toMove',
						className : 'btn-primary rightAlign',
						callback : _.bind(this.toMove, this)
					}
				};
			},

			toMove : function(e) {	
				e && e.preventDefault();
				this.$('div#generalInfo').collapse('show');
				if(this.isValidForm(this.validationSettings())) {
					var formData = Backbone.Syphon.serialize(this);
					Gloria.WarehouseApp.trigger('load:inventory', formData);
				}
			},

			loadedInventory : function(collection) {
				if (collection && collection.length > 0) {
					if(collection.first().get('zoneType') == "STORAGE") {
						Gloria.WarehouseApp.trigger('show:move:inventory', collection);
					} else {
						this.showErrors([], [ {
							element : {
								id : 'binLocation'
							}
						}]);
					}					
				} else {					
					this.showErrors([], [ {
						element : {
							id : 'binLocation'
						}
					}, {
						element : {
							id : 'partAffiliation'
						}
					}, {
						element : {
							id : 'partNumber'
						}
					}, {
						element : {
							id : 'partVersion'
						}
					}]);
				}
			},

			validationSettings : function() {
				return {
					showErrors : _.bind(this.showErrors, this),
					onfocusin : false,
					onfocusout : false,
					onkeyup : false,
					onclick : false
				};
			},

			validator : function(options) {
				var validator = this.$('form').validate(options);
				return validator;
			},

			isValidForm : function(options) {
				return this.validator(options).form();
			},

			showErrors : function(errorsMap, errorsList) {
				this.hideErrors();
				_.each(errorsList, function(error) {
					var controlGroup = this.$('.' + error.element.id);
					controlGroup.addClass('has-error');
				}, this);
			},

			hideErrors : function() {
				this.$('.form-group').removeClass('has-error');
			},

			render : function() {
				this.$el.html(this.template());
				Gloria.trigger('showAppControlButtonView', this.buttonEvents());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.Move.View.ToMoveView;
});
