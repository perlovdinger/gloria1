define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'jquery-validation',
		'hbs!views/warehouse/qualityinspection/overview/view/unmark-modal'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.UnmarkModalView = Marionette.View.extend({

			className : 'modal',

			id : 'UnmarkModal',

			events : {
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				this.model = options.model; 
			},
			
			handleSaveClick : function(e) {
				if (this.isValidForm()) {
					var unmarkQty = $('#unmarkQty').val();
					Gloria.WarehouseApp.trigger('QIOverview:optional:unmarkqty', this.model, unmarkQty);
					this.$el.modal('hide');
				}
			},
			
			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},
			
			validator : function() {
				var that = this;				
				return this.$el.find('form').validate({
					rules: {
						'unmarkQty': {
							required : true,
							digits: true,
							min: 0
						}
					},
					messages: {
						'unmarkQty': {
							required: i18n.t('errormessages:errors.GLO_ERR_075'),
							digits: i18n.t('errormessages:errors.GLO_ERR_075'),
							min: i18n.t('errormessages:errors.GLO_ERR_075')
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

			hideErrors : function() {
				Gloria.trigger('hideAppMessageView', {
					modal : true
				});
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					data : this.model.toJSON()
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
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.View.UnmarkModalView;
});
