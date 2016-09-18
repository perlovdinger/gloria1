define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'bootstrap',
	    'i18next',
	    'jquery-validation',
	    'hbs!views/warehouse/receive/deliverynotelines/view/print-tl-modal'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, compiledTemplate) {
    
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.PrintTLModalView = Marionette.LayoutView.extend({
	    	
			className : 'modal',

			id : 'print-tl-modal',
			
	    	events : {
	    		'click #print' : 'handlePrintClick',
	    		'click #cancel' : 'handleCancelClick'
	        },
	        
	        initialize : function(options) {
	        	this.collection = options.collection;
	        	this.collection.off('add remove change');
	        },
			
			handlePrintClick : function(e) {
	    		e.preventDefault();
	    		if (this.isValidForm()) {
	    			Gloria.WarehouseApp.on('DeliveryNoteLine:printTL:printed', function(flag) {
	    				this.$el.modal('hide');
					}, this);
	    			Gloria.WarehouseApp.trigger('DeliveryNoteLine:printTL:print', this.$el.find('#view-select').val());
	    		};
			},
	        
			validator : function() {
				var that = this;				
				return this.$el.find('form').validate({
					ignore: '',
					rules: {						
						'view-select': {
							required: true
						}
					},
					messages: {						
						'view-select': {
							required: i18n.t('errormessages:errors.GLO_ERR_045')
						}
					},
					showErrors: function (errorMap, errorList) {
			        	that.showErrors(errorList);
			        }
				});
			},
			
			isValidForm : function() {
				return this.validator().form();
			},

			showErrors : function(errorList) {
				this.hideErrors();
				Gloria.trigger('showAppMessageView', {
					type : 'error',
					modal : true,
					title : i18n.t('errormessages:general.title'),
					message : errorList
				});
			},

			hideErrors : function() {
				Gloria.trigger('hideAppMessageView');
			},
			
	        handleCancelClick : function(e) {
	        	this.$el.modal('hide');
	        },
	        
	        renderPrintTLSelector : function(options) {
	        	var ret = "<option value='' " + ">" + Handlebars.helpers.t('Gloria.i18n.warehouse.receive.print.tlSelect') + "</option>";
        		$.each(options, function(i, item) {
        			ret += "<option value='" + item.id + "'>" + item.code + "</option>";
				});
                return ret;
            },
	        
	        render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					items : that.collection ? that.collection.toJSON() : [],
					'renderPrintTLSelector' : that.renderPrintTLSelector
				}));
				this.$el.modal({
					show : false
				});
				this.$el.on('hidden.bs.modal', function() {
					that.trigger('hide');
					Gloria.trigger('reset:modellayout');
				});
				return this;
			},
			
			onShow : function() {
				this.$el.modal('show');
				this.collection.on('add remove change', this.render);
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.WarehouseApp.off(null, null, this);
			}
	    });
	});
	
    return Gloria.WarehouseApp.View.PrintTLModalView;
});
