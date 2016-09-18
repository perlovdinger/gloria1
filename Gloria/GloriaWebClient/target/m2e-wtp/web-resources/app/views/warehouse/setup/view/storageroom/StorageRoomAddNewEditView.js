define(['app',
        'backbone',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'bootstrap',
        'backbone.syphon',
        'hbs!views/warehouse/setup/view/storageroom/storageRoom-addNew-edit'
], function(Gloria, Backbone, $, i18n, _, Handlebars, Marionette, Bootstrap, Syphon, compiledTemplate) {
    
    Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.StorageRoomAddNewEditView = Marionette.View.extend({
            
            className : 'modal',
            
            id: 'storageRoomModal',
            
            events : { 
            	'click #save-button' : 'saveStorageRoomForm',
                'click #cancel-button' : 'cancelStorageRoomForm'
            },
            
            initialize : function(options) {
    			this.model = options.model;
    			this.addAnother = options.addAnother;
    			Gloria.WarehouseApp.on('storageRoomForm:enableSaveButton', this.postSave, this);
    		},
    		
    		postSave: function(e) {
    		    this.$('#save-button').removeAttr('disabled');
    		},           
      
			saveStorageRoomForm : function(e) {
				e.preventDefault();
				var that = this;
				this.$('#save-button').attr('disabled', true);
				var formData = Backbone.Syphon.serialize(this);
				Gloria.WarehouseApp.trigger('storageRoom:show:save', formData);
				e.currentTarget.setAttribute('disabled', true);
				Gloria.WarehouseApp.on('storageRoom:show:saved', function(resp) {
					e.target.removeAttribute('disabled');
					if (!that.$el.find('#addAnother').prop('checked')) {
						that.$el.modal('hide');
					} else {
						// Reset modal form!
						that.$el.find('form#storageRoomForm')[0].reset();
						Gloria.WarehouseApp.trigger('storageRoomForm:enableSaveButton');
					}
				});
			},
			
            cancelStorageRoomForm : function() {
                this.$el.modal('hide');
            },

            render : function() {
                var that = this;
			    this.$el.html(compiledTemplate({
					data : this.model ? this.model.toJSON() : {},
					addAnother : this.addAnother
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
    
    return Gloria.WarehouseApp.View.StorageRoomAddNewEditView;
});
