define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
		'marionette',
		'bootstrap',
		'jquery.fileupload',
		'datepicker',
		'moment',
		'i18next',
		'utils/DateHelper',
		'hbs!views/deliverycontrol/myorderoverview/view/adjust-delivery-item'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, fileupload, Datepicker, moment, i18n, DateHelper, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AdjustDeliveryItemView = Marionette.ItemView.extend({
		    
		    initialize: function(options) {
		        options || (options = {});
		        this.index = options.index;
		    },
		    
		    modelEvents: {
		        'change': "modelChanged"		        
		    },
		    
		    events: {
		        'change input[id^="expectedQuantity"]': 'updateExpectedQuantity',
		        'change input[id^="expectedDate"]': 'updateExpectedDate',
		        'click a[id^="adjust"]' : 'handleAdjustClick',
                'click a[id^="remove"]' : 'handleRemoveClick',
		    },
		    
		    modelChanged: function(model) {		        
		        this.$('input[id^="expectedQuantity"]').val(model.get('expectedQuantity'));
		        this.$('input[id^="expectedDate"]').val(Handlebars.helpers.date(model.get('expectedDate')));
		        Gloria.DeliveryControlApp.trigger('AdjustDelivery:warningmessage:remove');
		    },
		    
		    updateExpectedQuantity: function(e) {
		        var value = e.target.value;
		        this.model.set('expectedQuantity', value);
		    },
		    
		    updateExpectedDate: function(e) {
		        var value = e.target.value;		        
		        this.model.set('expectedDate', DateHelper.parseDate(value));
		    },
		    
		    handleAdjustClick : function(e) {
                e.preventDefault();
                this.model.collection.add(new Backbone.Model());
            },
            
            handleRemoveClick : function(e) {
                e.preventDefault();
                this.model.collection.remove(this.model);
            },
	    	
	    	id : function() {
				return 'delivery' + this.model.cid;
			},
	    	
	    	render : function() {    	
			 	this.$el.html(compiledTemplate({ 
	 				'data': this.model ? this.model.toJSON() : {},
	 				'cid': this.model.cid,
	 				first: (this.index == 0)
	 		    }));
			 	
			 	this.$('.date').datepicker();
			 	
			    return this;
			}, 
			
			onDestroy: function() {
			    this.$('.date').datepicker('remove');
			}
		});
	});

	return Gloria.DeliveryControlApp.View.AdjustDeliveryItemView;
});
