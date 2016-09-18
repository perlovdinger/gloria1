define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',		
		'i18next',
		'utils/dialog/dialog',
		'hbs!views/warehouse/receive/deliverynotelines/view/deliverynoteline-overview-layout'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Dialog, Template) {

	Gloria.module('WarehouseApp.Receive.DeliveryNoteLineInformation.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.DeliveryNoteLineOverViewLayout = Marionette.LayoutView.extend({
			
			regions : {				
				'titlePane' : '#titlePane',
				'buttonPane' : '#buttonPane',
				'gridPane' : '#gridPane'
			},

			getTemplate : function() {
				return Template;
			},
			
			events: {							    
                'click #receive': 'receive',
			    'click #cancel' : 'cancel'
			},
			
			initialize: function(option) {
			    Gloria.WarehouseApp.on('loaded:deliveryNoteLine', this.deliveryNoteLinesLoaded, this);
			    this.listenTo(Gloria.WarehouseApp, 'deliveryNoteLine:start:update', this.deliveryNoteLinesStartUpdate);			    
			    this.listenTo(Gloria.WarehouseApp, 'deliveryNoteLine:complete:update', this.deliveryNoteLinesCompleteUpdate);
			},
			
			deliveryNoteLinesLoaded: function(collection) {
			    if(collection.length > 0) {
			        this.$('#receive').prop('disabled', false);                    
                } else {
                    this.$('#receive').prop('disabled', true); 
                }
			},
			
			deliveryNoteLinesStartUpdate: function() {			    
			    this.$('#receive').prop('disabled', true);
			    this.$('#cancel').prop('disabled', true);			    			    
			},
			
			deliveryNoteLinesCompleteUpdate: function(model) {			    
			    this.$('#receive').prop('disabled', false);
			    this.$('#cancel').prop('disabled', false);
            },
			
			receive: function(e) {
			    if($('.backgrid').find('.error').length == 0){
			    	Gloria.WarehouseApp.trigger('receive:deliveryNoteLine');
			    }
			},
			
			cancel: function(e) {			
				Dialog.show({
					title: i18n.t('Gloria.i18n.warehouse.receive.details.text.cancel.header'),
					message: '<b>' + i18n.t('Gloria.i18n.warehouse.receive.details.text.cancel.mainText') + '</b>'
								+ '<br/><br/>' + i18n.t('Gloria.i18n.warehouse.receive.details.text.cancel.subText'),
					buttons: {
		                yes: {
		                    label: i18n.t('Gloria.i18n.buttons.yes'),
		                    className: "btn btn-primary",
		                    callback: function(e) {
		                        e.preventDefault();
		                        Gloria.WarehouseApp.trigger('cancel:deliveryNoteLine');
		                        return true;
		                    }
		                },
		                no: {
		                    label: i18n.t('Gloria.i18n.buttons.no'),
		                    className: "btn btn-default",
		                    callback: function(e) {
		                        e.preventDefault();
		                        return false;
		                    }
		                }
		            }
				});			   
			}			
		});
	});

	return Gloria.WarehouseApp.Receive.DeliveryNoteLineInformation.View.DeliveryNoteLineOverViewLayout;
});