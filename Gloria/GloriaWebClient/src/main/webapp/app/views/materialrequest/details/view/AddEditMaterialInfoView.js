define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'bootstrap',
        'backbone.syphon',
        'utils/handlebars/UnitsOfMeasureSelectorHelper',
        'utils/handlebars/PartAffiliationSelectorHelper',
        'hbs!views/materialrequest/details/view/add-edit-material-info'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Bootstrap, Syphon, 
        unitsOfMeasureSelectorHelper, partAffiliationSelectorHelper, compiledTemplate) {
    
    Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.AddEditMaterialInfoView = Marionette.View.extend({
            
            className: 'modal',
            
            id: 'procureRequestLinesModal',
            
            events: {
                'click #save-button' : 'saveMaterialInfo',
                'click #cancel-button' : 'cancelMaterialInfo'
            },
            
            initialize: function(options) {
            	this.materialRequestLineCollection = options.materialRequestLineCollection;
            	this.materialRequestModel = options.materialRequestModel;
            	this.model = options.model;
    			this.addAnother = options.addAnother;    			
    			$.validator.addMethod('positiveNumber', function(value, element) {
					return Number(value) >= 0;
				});
    			$.validator.addMethod('greaterThanZero', function(value, element) {
					return Number(value) > 0;
				});
    			
    			Handlebars.registerHelper('select', function( value, options ){
    		        var $el = $('<select />').html( options.fn(this) );
    		        $el.find('[value=' + value + ']').attr({'selected':'selected'});
    		        return $el.html();
    		    });
    		},
    		
            saveMaterialInfo: function(e) {
            	e.preventDefault();
            	if(this.isValidForm()) {
            	    this.listenTo(Gloria.MaterialRequestApp, 'MaterialRequestDetails:materialInfo:saved', function(flag, errorMessage) {
            	    	if(flag) {
            	    		if(!this.$('#addAnother').prop('checked')) {
                                this.$el.modal('hide');
                                Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:materialInfo:reload');
                            } else {
                                // Reset modal form!
                                this.$('form')[0].reset();
                                this.hideErrors();
                            }
            	    	} else {
            	    		Gloria.trigger('showAppMessageView', {
    							type : 'error',
    							modal : true,
    							message : errorMessage
    						});
            	    	}
					});
            		var formData = Backbone.Syphon.serialize(this);
                    Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:materialInfo:save', this.model, formData.procurerequestlines);
            	}
            },

            validator: function() {
            	var that = this;
				return this.$el.find('#procureRequestLinesForm').validate({
					rules: {
						'procurerequestlines[name]': {
							required: true
						},
						'procurerequestlines[partAffiliation]': {
							required: true
						},
						'procurerequestlines[partNumber]': {
							required: true
						},
						'procurerequestlines[partVersion]': {
							required: true
						},
						'procurerequestlines[partName]': {
							required: true
						},
						'procurerequestlines[quantity]': {
						    required: true,
						    digits: true,
						    greaterThanZero: true
						},
						'procurerequestlines[unitOfMeasure]': {
							required: true
						}
					},
					messages: {					
						'procurerequestlines[name]': {
							required: i18n.t('errormessages:errors.GLO_ERR_065')
						},
						'procurerequestlines[partAffiliation]': {
							required: i18n.t('errormessages:errors.GLO_ERR_008')
						},
						'procurerequestlines[partNumber]': {
							required: i18n.t('errormessages:errors.GLO_ERR_009')
						},
						'procurerequestlines[partVersion]': {
							required: i18n.t('errormessages:errors.GLO_ERR_010')
						},
						'procurerequestlines[partName]': {
							required: i18n.t('errormessages:errors.GLO_ERR_011')
						},
						'procurerequestlines[quantity]': {
							required: i18n.t('errormessages:errors.GLO_ERR_012'),
							greaterThanZero: i18n.t('errormessages:errors.GLO_ERR_012')
						},
						'procurerequestlines[unitOfMeasure]': {
							required: i18n.t('errormessages:errors.GLO_ERR_013')
						}
					},
					showErrors: function (errorMap, errorList) {
			        	that.showErrors(errorList);
			        }
				});
			},
			
			isValidForm: function() {
				return this.validator().form();
			},
            
			showErrors: function (errorList) {
		        Gloria.trigger('showAppMessageView', {
		        	modal : true,
		            type : 'error',
		            message : errorList
		        });
		    },

			hideErrors: function() {
				Gloria.trigger('hideAppMessageView', {
					modal : true
				});
			},
			
            cancelMaterialInfo: function() {
                this.$el.modal('hide');
            },
            
            isFirstVersion: function() {
            	var version = this.materialRequestModel.get('mtrlRequestVersion').split('V')[1];
            	return version == 1;
			},
			
			getTestObjects: function() {
				return _.uniq(this.materialRequestLineCollection.pluck('name')).sort();
			},

            render: function() {
                var that = this;
                var type = this.materialRequestModel.get('type');
                
			    this.$el.html(compiledTemplate({
						data : this.model ? this.model.toJSON() : {},
						addAnother : this.addAnother,
						isMultiple: type && type.toUpperCase() === 'MULTIPLE',
						isFirstVersion : that.isFirstVersion(),
						toOptions : that.getTestObjects(),
						'unitsOfMeasureSelector' : unitsOfMeasureSelectorHelper.handlebarsHelper,
						'partAffiliationSelector' : partAffiliationSelectorHelper.handlebarsHelper,
						unitOfMeasure : (this.model && this.model.get('unitOfMeasure')) || 'PCE'
				}));
                this.$el.modal({                                        
                    show: false
                });
                this.$el.on('hidden.bs.modal', function() {
                    that.trigger('hide');
                });
                return this;
            },
            
            onShow: function() {
                this.$el.modal('show');                
            },
            
            onDestroy: function() {
                this.$el.modal('hide');
                this.$el.off('.modal');                
            }
        });
    });
    
    return Gloria.MaterialRequestApp.View.AddEditMaterialInfoView;
});