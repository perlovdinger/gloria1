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
		'hbs!views/warehouse/qisettings/view/add-edit-part'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AddEditPartView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'addEditPart',

			events : {
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				this.collection = options.collection;
				this.model = options.model;
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:part:saved', this.handleSaveResponse);
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
					Gloria.WarehouseApp.trigger('QISettingsModal:part:save', this.model, formData.part);
				}
			},

			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},
			
			validator : function() {
				var that = this;
				//Check if partNo or partName exist
				$.validator.addMethod('checkIfPartNoOrNameExist', function(partNumber, element) {
					var partName =  that.$el.find('#partName').val();
					var isEitherPartNoOrName = false;
					if (partNumber != '' || partName != '') {
						isEitherPartNoOrName = true;
					}
					return isEitherPartNoOrName;
				});
				//checkIfPartNoAndNameUnique
				$.validator.addMethod('checkIfPartNoAndNameUnique', function(partNumber, element) {
					var enteredPartName  = that.$el.find('#partName').val();
					var isPartNumberAndNameUnique = true;
					that.collection.each(function(model) {
					    var currentPartNumber = model.get('partNumber');
                        var currentPartName = model.get('partName');
						if(partNumber == currentPartNumber && enteredPartName == currentPartName) {
							isPartNumberAndNameUnique = false;
							if(that.model && that.model.get('id') == model.get('id')){
								isPartNumberAndNameUnique = true;
							}
						};
					});
					return  isPartNumberAndNameUnique;
				});
				return this.$el.find('form').validate({
					rules: {						
						'part[partNumber]': {
							checkIfPartNoOrNameExist: true,
							checkIfPartNoAndNameUnique: true
						}
					},
					messages: {
						'part[partNumber]': {
							checkIfPartNoOrNameExist: i18n.t('errormessages:errors.GLO_ERR_056'),
							checkIfPartNoAndNameUnique: i18n.t('errormessages:errors.GLO_ERR_056')
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
						this.$el.find('form#addEditPartForm')[0].reset();
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
					inspectionOptions : this.inspectionOptions,
					data : this.model ? this.model.toJSON() : {},
					mandatory : this.model ? this.model.get('mandatory').toString(): 'true'							
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
				this.$("[data-toggle='tooltip']").tooltip({
				    container: this.$('.modal-body')
				});
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.View.AddEditPartView;
});
