define(['app',
        'jquery',
        'i18next',
        'underscore',
		'handlebars', 
		'marionette',
		'utils/dialog/dialog',
		'hbs!views/warehouse/receive/deliverynotelines/view/control-button'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Dialog, compiledTemplate) {
    
	Gloria.module('WarehouseApp.Receive.DeliveryNoteLineInformation.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.OverviewButtonView = Marionette.LayoutView.extend({
			
			events : {
				'click #addInformation-button' : 'addInformation',
				'click #edit-button' : 'editInformation',
				'click #createTranLabel-button' : 'createTranLabelClick',
				'click #printPL' : 'printPartLabel',
				'click #printTL' : 'printTransportLabel'
			},
			
			addInformation : function() {
			    if(this.selectedRows) {
                    Gloria.WarehouseApp.trigger('DeliveryNoteLine:addInformation', _.first(this.selectedRows));
			    }
			},
			
			editInformation : function() {
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('DeliveryNoteLine:editInformation', this.selectedModels);
			},
			
			createTranLabelClick : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('DeliveryNoteLine:createTransLabel');
			},
			
			printPartLabel : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				if(!this.$('#printPL').closest('li').hasClass('disabled')) {
					Gloria.WarehouseApp.trigger('DeliveryNoteLine:printPL', this.selectedModels);
	        	}
			},
			
			printTransportLabel : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('DeliveryNoteLine:printTL');
			},
	        
	        initialize : function(options) {	        	
	            this.permittedActions = options.permittedActions || {}; 
	            this.listenTo(Gloria.WarehouseApp, 'DeliveryNoteLine:ResetButtons', this.resetButtons);
	            this.listenTo(Gloria.WarehouseApp, 'select:deliveryNoteLineGrid', this.selectDeliveryNoteLineGrid);
				this.listenTo(Gloria.WarehouseApp, 'deliveryNoteLine:directsend:updated', this.handleDirectSendUpdate);
	        },

			render : function() {
				this.$el.html(compiledTemplate({
					permittedActions : this.permittedActions
				}));
				return this;
			},
			
			resetButtons : function() {
	        	this.permittedActions = {};
	        	this.render();
			},
			
			checkToReceiveQty : function(model) {
				var isQtyEntered = false;
				_.each(model.directsends.models, function(thisModel) {
					if(!isQtyEntered) {
						isQtyEntered = thisModel.get('toReceiveQty') > 0;
					}
				});
				return isQtyEntered;
			},
			
			selectDeliveryNoteLineGrid : function(selectedRows, selectedModels) {
				this.selectedModels = selectedModels;
				if(selectedRows) {					
					this.selectedRows = selectedRows;					
					if(selectedRows.length == 1 && this.checkToReceiveQty(_.first(selectedModels))) {
                        this.permittedActions['add'] = true;
                    } else {
                        delete this.permittedActions['add'];
                    };
					if(selectedRows.length >= 1) {
						this.permittedActions['edit'] = true;
						this.permittedActions['printPL'] = true;
					} else {	
						delete this.permittedActions['edit'];
						delete this.permittedActions['printPL'];
					};
				} else {
					delete this.selectedRows;
				};
				this.render();
			},
			
			handleDirectSendUpdate : function() {
				this.selectDeliveryNoteLineGrid(this.selectedRows, this.selectedModels);
			},
			
			onDestroy : function() {
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});
	
	return Gloria.WarehouseApp.Receive.DeliveryNoteLineInformation.View.OverviewButtonView;
});
