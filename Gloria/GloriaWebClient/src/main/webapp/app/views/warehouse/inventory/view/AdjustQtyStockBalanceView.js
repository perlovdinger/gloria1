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
		'hbs!views/warehouse/inventory/view/adjust-qty-stockbalance'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AdjustQtyStockBalanceView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'adjustQtyStockbalanceModal',

			events : {
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick'
			},
			

			initialize : function(options) {
				this.model = options.model;
                this.listenTo(Gloria.WarehouseApp, 'Inventory:adjusted', this.handleSaveInventoryResponse);
			},

			handleSaveClick : function(e) {
				e.preventDefault();
				if (this.isValidForm()) {
					var formData = Backbone.Syphon.serialize(this);
					var comments = $('#comments').val();
					Gloria.WarehouseApp.trigger('Inventory:adjust', formData.partbalance, comments);
				}
			},
			
			validator : function() {
				var that = this;
				return this.$el.find('#adjustForm').validate({
					rules: {
						'partbalance[quantity]': {
							required: true,
							digits: true,
							min : 0
						},
						'comments': {
							required: true
						},
					},
					messages: {						
						'partbalance[quantity]': {							
							required: i18n.t('errormessages:errors.GLO_ERR_052'),
							digits: i18n.t('errormessages:errors.GLO_ERR_052'),
							min: i18n.t('errormessages:errors.GLO_ERR_052')
						},
						'comments': {
							required: i18n.t('errormessages:errors.GLO_ERR_053')
						},
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
					title : i18n.t('errormessages:general.title'),
					message : errorList
				});
			},
			
			handleSaveInventoryResponse : function(flag) {
			    this.$el.modal('hide');
			    if(flag) {
                    Backbone.history.loadUrl(Backbone.history.fragment);
                } else {
                    // Show error
                }
            },
			
			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {}
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

	return Gloria.WarehouseApp.View.AdjustQtyStockBalanceView;
});
