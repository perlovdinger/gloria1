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
		'hbs!views/deliverycontrol/myorderoverview/details/view/delivery-schedule'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, fileupload, Datepicker, moment, i18n, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.DeliveryScheduleItemView = Marionette.LayoutView.extend({

			initialize : function(options){
	    		 this.model = options.model;
	    		 this.isEditable = options.isEditable;
	    		 // If Orderline.staAgreedDate is not set, then expectedDate needs to be set as Orderline.staAgreedDate when it changes for the first time 
	    		 this.listenTo(Gloria.DeliveryControlApp, 'OrderLineDetail:staAgreedDate', this.onOrderLineSTAAgreedDate);
	    		 this.expectedDate = options.expectedDate;
	    		 if(this.expectedDate) {
	    			 this.model.set('expectedDate', this.expectedDate);
	    		 }
	    	},
	    	
	    	onOrderLineSTAAgreedDate: function(options) {
	    		if(options && options.type === 'changed') {
	    			this.$('input#expectedDate' + this.model.id).datepicker('update', options.args);
	    		}	    		
	    	},
	    	
	    	className: "panel panel-default panel-visible",
	    	
	    	events : {
	    		'click .flagClass' : 'handleFlagChange',
	    		'show .js-date' : 'stopPropagation'
	        },
	        
	        handleFlagChange : function(e) {
				this.refreshFlagDropdown(e);
			},
			
			refreshFlagDropdown : function(e) {
				var currentFlagDropdown = $(e.currentTarget.parentElement).closest('div.dropdown');
				var currentDeliveryId = currentFlagDropdown[0].id.match(/\d+/)[0];
	    		var hiddenInputName = "deliveryschedules[" + currentDeliveryId +"][statusFlag]" ;
	    		
				this.$el.find('.flagClass').unbind('click');	
				this.$el.find('.dropdown.open .dropdown-toggle').dropdown('toggle');
				var attributes = this.$el.find('#' + e.currentTarget.id + ' a i').prop("attributes");
				currentFlagDropdown.find('#flagSelect').empty();
	    		$.each(attributes, function() {
	    			currentFlagDropdown.find('#flagSelect').attr(this.name, this.value);
	    		}); 
	    		var selectedText = this.$el.find('#' + e.currentTarget.id + ' a').text();
	    		currentFlagDropdown.find('#flagSelectText').text(selectedText);    	
	    		this.selectedFlag = e.currentTarget.id;
	    		this.selectedFlag = this.selectedFlag == 'DEFAULT' ? '' : this.selectedFlag; 
	    		this.$el.find('input:hidden[name="'+ hiddenInputName+'"]').val( this.selectedFlag );    		
			},
	    	
	    	stopPropagation : function(e) {
				e.stopPropagation();
				e.stopImmediatePropagation();			 
	    	},
	    	
	    	handleDeliveryScheduleClick : function(vent) {
				var id = vent.currentTarget.hash.split('#poDeliverySchedule')[1];
				Gloria.DeliveryControlApp.trigger('DeliverySchedule:showDocuments', id);
			},
			
			showError : function(errorMessage, errors) {
				Gloria.trigger('showAppMessageView', {
					type : 'error',
					title : i18n.t('Gloria.i18n.warehouse.receive.validation.title'),
					message : errorMessage
				});
			},
			
			configureDeliveryScheduleDocUpload : function(vent) {
				var that = this;
				var id = vent.currentTarget.hash.split('#poDeliverySchedule')[1];
				var divId = '#attachedDocupload' + id;
				$(divId).fileupload({
					url : '/documents/v1/deliveryschedules/' + id + '/attacheddoc',
					dataType : 'json',
					singleFileUploads : 'true',
					add : function(e, data) {
						data.submit();
					},
					progressall : function(e, data) {
						
					},
					done : function(e, data) {
						Gloria.DeliveryControlApp.trigger('DeliverySchedule:showDocuments', id);
					},
					fail : function(e, data) {

					},
					validationError : that.showError
				});
			},
			
			render : function() {      	
			 	this.$el.html(compiledTemplate({ 
	 				'data' : this.model ? this.model.toJSON() : {},
	 				isDisabled : !this.isEditable
	 		    }));			 	
	            this.$('.date').datepicker(); 
			    return this;
			},
			
			onShow : function() {
				var that = this;
				$('a[href^="#poDeliverySchedule'+ this.model.get('id') + '"]').click(function(e) {
					if(e.currentTarget.className.indexOf('collapsed') != -1) {
						that.handleDeliveryScheduleClick(e);
						that.configureDeliveryScheduleDocUpload(e);
					};
				});
			},
            
            onDestroy: function() {
                this.$('.date').datepicker('remove');
            }
		});
	});

	return Gloria.DeliveryControlApp.View.DeliveryScheduleItemView;
});
