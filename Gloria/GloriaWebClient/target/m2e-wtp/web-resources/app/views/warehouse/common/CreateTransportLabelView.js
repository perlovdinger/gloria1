define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'jquery-validation',
        'backbone.syphon',
		'hbs!views/warehouse/common/create-transportlabel'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.CreateTransportLabelView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'createTransportLabel',

			events : {
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'hideModal'
			},
			
			initialize : function(options){
				Gloria.WarehouseApp.on('DeliveryNoteLine:TransLabelcreated', this.hideModal, this);				
			},
			
			handleSaveClick : function(e) {
				e.preventDefault();
				if (this.isValidForm()) {	
					Gloria.WarehouseApp.trigger('CreateNumberOfTransportLabel', this.$el.find('#transportLabelNumber').val());
					this.hideModal(e);					
				}
			},
			
			hideModal : function(e){
				this.$el.modal('hide');				
			},
			
			validator : function() {
				var that = this;
			
				return this.$el.find('form').validate({
					rules: {						
						'transportLabelNumber': {
							required : true,
							digits: true
						}
					},
					messages: {						
						'transportLabelNumber': {
							required: i18n.t('Gloria.i18n.validInput'),
							digits: i18n.t('Gloria.i18n.validInput')
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
				Gloria.trigger('showAppMessageView', {
					modal : true,
					type : 'error',
					message : errorList
				});
			},			
			
			
			render : function() {
				var that = this;
				this.$el.html(compiledTemplate());
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
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.View.CreateTransportLabelView;
});
