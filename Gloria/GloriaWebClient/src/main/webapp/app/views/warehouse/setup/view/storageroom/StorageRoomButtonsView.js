define(['app',
        'jquery',
        'i18next',
        'underscore',
		'handlebars', 
		'marionette',
		'utils/dialog/dialog',
		'hbs!views/warehouse/setup/view/storageroom/storage-room-buttons'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Dialog, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.StorageRoomButtonsView = Marionette.ItemView.extend({
		    
			initialize : function(options) {
				this.permittedActions = {};
				Gloria.WarehouseApp.on('StorageRoomGridView:select', this.gridSelectHandler, this);
			},
			
			events : {
				'click #srAddNew' : 'addNewStorageRoom',
				'click #srEdit' : 'editStorageRoom',			
				'click #srDelete' : 'removeStorageRoom'
			},
			
			addNewStorageRoom : function() {
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('storageRoom:show:add');			
			},
			
			editStorageRoom : function() {
				 if(this.selectedRows) {
					 Gloria.trigger('hideAppMessageView');
	                 Gloria.WarehouseApp.trigger('storageRoom:show:edit', _.first(this.selectedRows));
				 }
			},
			
			removeStorageRoom : function() {
			    Dialog.show({                    
                    message: i18n.t('Gloria.i18n.warehouse.setup.header.storageRoomAddNewEditViewForm.deleteConfirmation')                    
                }).dialog.on('ok', function(e){
                	if(this.selectedRows) {                       
                        Gloria.WarehouseApp.trigger('storageRoom:show:remove', this.selectedRows);
                    }
			    }, this);
			},   

			gridSelectHandler : function(selectedRows) {	
				if(selectedRows) {					
					this.selectedRows = selectedRows;
					if(selectedRows.length == 1) {
                        this.permittedActions['edit'] = true;
                    } else {
                        delete this.permittedActions['edit'];
                    }
					if(selectedRows.length >= 1) {						
						this.permittedActions['delete'] = true;						
					} else {	
						delete this.permittedActions['delete'];
					}
				} else {
					delete this.selectedRows;
				} 
				this.render();
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					permittedActions : this.permittedActions
				}));
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.StorageRoomButtonsView;
});
