define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'bootstrap',
        'i18next',
		'utils/dialog/dialog',
		'hbs!views/warehouse/qisettings/view/qi-settings-button'
], function(Gloria, $, _, Handlebars, Marionette, Bootstrap, i18n, Dialog, compiledTemplate) {
    
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.QISettingsButtonView = Marionette.LayoutView.extend({
	        
	        initialize : function(options) {
	        	this.module = options.module;
	            this.permittedActions = {};
	            this.listenTo(Gloria.WarehouseApp, 'QISettings:select', this.gridSelectHandler);
	            this.listenTo(Gloria.WarehouseApp, 'QISettingsButton:ResetButtons', this.resetButtons);
	        },
	        
	        events : {
            	'click #add-button' : 'handleAddButtonClick',
            	'click #edit-button' : 'handleEditButtonClick',
            	'click #remove-button' : 'handleRemoveButtonClick'
			},
			
			gridSelectHandler : function(selectedRows) {
				this.selectedRows = selectedRows;
				if(selectedRows.length != 1) {
				    delete this.permittedActions['edit'];
				    delete this.permittedActions['remove'];
				} else {
				    this.permittedActions['edit'] = true;
				    this.permittedActions['remove'] = true;
				}
				this.render();
			},
			
			resetButtons : function() {
	        	this.permittedActions = {};
	        	this.render();
			},

	        handleAddButtonClick : function() {
	            Gloria.WarehouseApp.trigger('QISettingsModal:' + this.module + ':show');
			},
			
			handleEditButtonClick : function() {
				Gloria.WarehouseApp.trigger('QISettingsModal:' + this.module + ':show', _.first(this.selectedRows));
			},
			
			handleRemoveButtonClick : function() {
				var that = this;
				Dialog.show({                    
                    message: i18n.t('Gloria.i18n.warehouse.qisettings.deleteConfirmation')                    
                }).dialog.on('ok', function() {
                	Gloria.WarehouseApp.trigger('QISettingsModal:' + that.module + ':remove', _.first(that.selectedRows));
				}, this);
			},
			
			render : function() {
				this.$el.html(compiledTemplate({					
						permittedActions : this.permittedActions
					})
				);
				return this;
			},
			
			onDestroy : function() {
				Gloria.WarehouseApp.off(null, null, this);
			}
		});
	});
	
	return Gloria.WarehouseApp.View.QISettingsButtonView;
});
