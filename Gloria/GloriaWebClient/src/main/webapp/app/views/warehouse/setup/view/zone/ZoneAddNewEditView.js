define(['app',
        'backbone',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'bootstrap',
        'backbone.syphon',
        'hbs!views/warehouse/setup/view/zone/zone-addNew-edit'
], function(Gloria, Backbone, $, i18n, _, Handlebars, Marionette, Bootstrap, Syphon, compiledTemplate) {
    
    Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
    	
    	var zoneTypeOptions = [{
			"TO_STORE" : 'Gloria.i18n.zoneType.TO_STORE'
		}, {
			"QI" : 'Gloria.i18n.zoneType.QI'
		}, {
			"SHIPPING" : 'Gloria.i18n.zoneType.SHIPPING'
		}, {
			"QUARANTINE" : 'Gloria.i18n.zoneType.QUARANTINE'
		}, {
			"STORAGE" : 'Gloria.i18n.zoneType.STORAGE'
		}];
	
        
        View.ZoneAddNewEditView = Marionette.View.extend({
            
            className : 'modal',
            
            id: 'zoneModal',
            
            events : { 
            	'click #save-button' : 'saveZoneForm',
                'click #cancel-button' : 'cancelZoneForm'
            },
            
            initialize : function(options) {
    			this.model = options.model;
    			this.addAnother = options.addAnother;
    			
    			Gloria.WarehouseApp.on('zone:show:saved', this.postSave, this);
    			Gloria.WarehouseApp.on('zone:type:error', this.showError, this);    			
    		},
    		
    		showError: function(options) {
    		    this.hideErrors();
    		    this.$('#save-button').removeAttr('disabled');
    		    var errorMessage = options.errorMessage;
    		    this.$('#appModalMessage').find('.help-inline').text(i18n.t(errorMessage)).addClass('has-error');
    		},
    		
    		hideErrors : function() {
                this.$('.form-group').removeClass('has-error');
                this.$('#appModalMessage').find('.help-inline').text('');
                this.$('#name').focus();
            },
    		
    		postSave: function() {    		    
                    this.$('#save-button').removeAttr('disabled');
                    if(!this.$('#addAnother').prop('checked')) {
                        this.$el.modal('hide');                         
                    } else {
                        // Reset modal form!
                        this.$('form#zoneForm')[0].reset();
                        this.hideErrors();
                    }                
    		},
            
			saveZoneForm : function(e) {
				e.preventDefault();            	
            	var that = this;            
            	this.$('#save-button').attr('disabled', true);
            	var zoneStorageRoomId = $('#zoneStorageRoom').val();
            	var formData = Backbone.Syphon.serialize(this);
                Gloria.WarehouseApp.trigger('zone:show:save', formData, zoneStorageRoomId);
			},
			
            cancelZoneForm : function() {
                this.$el.modal('hide');
            },

            render : function() {
                var that = this;
			    this.$el.html(compiledTemplate({
						data : this.model ? this.model.toJSON() : {},
						addAnother : this.addAnother,
					    'zoneTypeOptions': zoneTypeOptions
				}));
                this.$el.modal({                                        
                    show: false                    
                });
                this.$el.on('hidden.bs.modal', function(){                    
                    that.trigger('hide');
                });
                return this;
            },
            
            onShow: function() {
                this.$el.modal('show');                
            },
            
            onDestroy : function() {
                this.$el.modal('hide');
                this.$el.off('.modal');
                Gloria.WarehouseApp.off(null, null, this);
            }
        });
    });
    
    return Gloria.WarehouseApp.View.ZoneAddNewEditView;
});
