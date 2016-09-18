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
		'hbs!views/warehouse/qisettings/view/add-edit-project'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, Validation, Syphon, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AddEditProjectView = Marionette.LayoutView.extend({

			className : 'modal',

			id : 'addEditPart',

			events : {
				'click #save' : 'handleSaveClick',
				'click #cancel' : 'handleCancelClick'
			},

			initialize : function(options) {
				this.collection = options.collection;
				this.model = options.model;
				this.listenTo(Gloria.WarehouseApp, 'QISettingsModal:project:saved', this.handleSaveResponse);
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
					Gloria.WarehouseApp.trigger('QISettingsModal:project:save', this.model, formData.project);
				}
			},

			handleCancelClick : function(e) {
				this.$el.modal('hide');
			},
			
			validator : function() {
				var that = this;
				//checkIfProjectUnique
				$.validator.addMethod('checkIfProjectUnique', function(project, element) {
					var isProjectUnique = true;
					that.collection.each(function(model) {
						if(project == model.get('project')) {
							isProjectUnique = false;
							if(that.model && that.model.get('id') == model.get('id')) {
								isProjectUnique = true;
							}						
						}						
					});
					return isProjectUnique;
				});
				return this.$el.find('form').validate({
					rules: {						
						'project[project]': {
							required : true,
							checkIfProjectUnique: true
						}
					},
					messages: {						
						'project[project]': {
							required: i18n.t('errormessages:errors.GLO_ERR_003'),
							checkIfProjectUnique: i18n.t('errormessages:errors.GLO_ERR_003')
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
						this.$el.find('form#addEditProjectForm')[0].reset();
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
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});

	return Gloria.WarehouseApp.View.AddEditProjectView;
});
