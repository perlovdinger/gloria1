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
		'hbs!views/warehouse/qisettings/view/add-edit-supplier'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AddEditSupplierView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'addEditPart',

			events : {
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				this.collection = options.collection;
				this.model = options.model;
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:supplier:saved', this.handleSaveResponse);
			},
			
			inspectionOptions : [{
                'true' : 'Gloria.i18n.warehouse.qisettings.inspectionOptions.true'
            }, {
                'false' : 'Gloria.i18n.warehouse.qisettings.inspectionOptions.false'
            }],
			
			handleSaveClick : function(e) {
				e.preventDefault();
				if (this.isValidForm()) {
					var formData = Backbone.Syphon.serialize(this);
					Gloria.WarehouseApp.trigger('QISettingsModal:supplier:save', this.model, formData.supplier);
				}
			},

			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},
			
			validator : function() {
				var that = this;
				//checkIfSupplierUnique
				$.validator.addMethod('checkIfSupplierUnique', function(supplier, element) {
					var isSupplierUnique = true;
					that.collection.each(function(model) {
						if(supplier == model.get('supplier')) {
							isSupplierUnique = false;
							if(that.model && that.model.get('id') == model.get('id')) {
								isSupplierUnique = true;
							}
						};
					});
					return isSupplierUnique;
				});
				return this.$el.find('form').validate({
					rules: {						
						'supplier[supplier]': {
							required : true,
							checkIfSupplierUnique: true
						}
					},
					messages: {						
						'supplier[supplier]': {
							required: i18n.t('errormessages:errors.GLO_ERR_057'),
							checkIfSupplierUnique: i18n.t('errormessages:errors.GLO_ERR_057')
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
			
			/**
			 * Handle Save Response
			 * Hide the pop-up and show error if any!
			 */
			handleSaveResponse : function(flag, errors) {
				if(flag) {
					if (!this.$el.find('#addAnother').prop('checked')) {
						this.$el.modal('hide');
					} else {
						// Reset modal form!
						this.$el.find('form#addEditSupplierForm')[0].reset();
					}
				} else {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						modal : true,
						message : errors
					});
				}
			},
			
			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {},
					mandatory : this.model ? this.model.get('mandatory').toString(): 'true',		
					inspectionOptions : this.inspectionOptions
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

	return Gloria.WarehouseApp.View.AddEditSupplierView;
});
