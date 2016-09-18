define(['app',
        'jquery',
        'underscore',
 	    'handlebars',
 	    'backbone',
 	    'marionette',
        'bootstrap',
        'datepicker',
        'moment',
        'i18next',
        'hbs!views/deliverycontrol/myorderoverview/details/view/part-general-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, Datepicker, moment, i18n, compiledTemplate) {
	
	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
	    
		View.PartGeneralInfoView = Marionette.View.extend({
	    	
			initialize : function(options) {        
	            this.orderLineModel = options.orderLineModel;
	            this.isEditable = options.isEditable;
	            this.module = options.module;
	            this.orderLineModel.off('add remove change');
	            this.orderModel = options.orderModel;
	            this.isInternalOrder = options.isInternalOrder;
	            // Triggers OrderLine.staAgreedDate to be used particularly in DeliveryScheduleItemView
	            Gloria.reqres.setHandler("OrderLineDetail:staAgreedDate", _.bind(function(options){
	            	  if(!this.orderLineModel.get('staAgreedDate')) {
	            		  return this.$('input#staAgreedDate').val();  
	            	  }	            	  
            	}, this));
	        },
	        
	        events: {
	        	'changeDate input#staAgreedDate': 'onSTAAgreedDateChange'
	        },
	        // Triggers STAAgreedDate for the first time when it is changed
	        // It is used particularly in DeliveryScheduleItemView
	        onSTAAgreedDateChange: function(e) {
	        	if(!this.triggeredSTAAgreedDate && !this.orderLineModel.get('staAgreedDate') && e && e.date) {
	        		Gloria.DeliveryControlApp.trigger('OrderLineDetail:staAgreedDate', {type: 'changed', args: e.target.value});
	        		this.triggeredSTAAgreedDate = true;
	        	}
	        },
	        
	        render : function() {
	        	var isDisabled = false;
                if((this.orderLineModel.get('orderStaChanged') || !this.orderLineModel.get('staAcceptedDate')) && this.orderLineModel.get('internalExternal') == 'EXTERNAL'){
                    /* insert code to highlight the old data in case of Order STA is updated. */
                    isDisabled = false;
                }

				if(this.orderLineModel.get('deliveryDeviation') && this.module != 'completed') {
					$('#deviationMessage').show();
				} else {
					$('#deviationMessage').hide();
				}
				
                this.$el.html(compiledTemplate({
                    'data' : this.orderLineModel ? this.orderLineModel.toJSON() : {},
                    'orderModelData' :  this.orderModel ? this.orderModel.toJSON() : {},
            		'isInternalOrder': this.isInternalOrder ,
            		isDisabled : !this.isEditable || isDisabled || (this.module == 'completed')
                }));                 
                this.$('.staAcceptedDateDiv').datepicker();
                this.$('.staAgreedDateDiv').datepicker();

              
	            return this;
	        },
	        
	        onShow : function() {
	        	if(this.isEditable && this.module == 'completed') {
	        		$('#allowedQuantity').removeAttr('disabled');
	        	}
	        	this.orderLineModel.on('add remove', this.render, this);
	        	Gloria.DeliveryControlApp.trigger('OrderLineDetail:showHidePOAmdInfo', this.orderLineModel.get('internalExternal') == 'EXTERNAL');
			},
            	
            onDestroy: function() {
                this.$('.js-date').datepicker('remove');
            }
           
	    });
	});

    return Gloria.DeliveryControlApp.View.PartGeneralInfoView;
});