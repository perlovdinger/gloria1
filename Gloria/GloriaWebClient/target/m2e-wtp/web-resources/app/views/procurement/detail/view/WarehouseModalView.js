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
        'views/procurement/detail/view/WarehouseSelectorHelper',
		'hbs!views/procurement/detail/view/warehouse-modal'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, warehouseSelectorHelper, compiledTemplate) {

	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.WarehouseModalView = Marionette.LayoutView.extend({

			initialize : function(options) {
	            this.selectedTOs = options.selectedTOs;
            },
            
			className : 'modal',

			id : 'warehouseModal',

			events : {
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick'
			},

			handleSaveClick : function(e) {
				e.preventDefault();
				 if(this.isValidForm()) {
					 var selectedWarehouse = $('#warehouseId_').val();
					 this.$el.modal('hide');
					 Gloria.ProcurementApp.trigger("MultipleUpdate:update", this.selectedTOs, selectedWarehouse);
				 }
			},
			
			validator : function() {
				var that = this;				
				return this.$el.find('form').validate({
					ignore: '',
					rules: {						
						'finalWhSiteId': {
							required: true
						}
					},
					messages: {
						'finalWhSiteId': {
							required: i18n.t('errormessages:errors.GLO_ERR_060')
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
					title : i18n.t('errormessages:general.title'),
					message : errorList
				});
			},
			
			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					'warehouseSelector' : warehouseSelectorHelper,
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
				Gloria.ProcurementApp.off(null, null, this);
			}
		});
	});

	return Gloria.ProcurementApp.View.WarehouseModalView;
});
