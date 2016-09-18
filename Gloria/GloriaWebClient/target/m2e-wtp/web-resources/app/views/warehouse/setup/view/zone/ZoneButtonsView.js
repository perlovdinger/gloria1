define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'utils/dialog/dialog',
		'hbs!views/warehouse/setup/view/zone/zone-buttons'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Dialog, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ZoneButtonsView = Marionette.ItemView.extend({

			initialize : function(options) {
				this.permittedActions = {};
				Gloria.WarehouseApp.on('ZoneGridView:select', this.gridSelectHandler, this);
				this.initializeGlobalEventListeners();		 	
			},
	        
	        initializeGlobalEventListeners : function() {
                _.bindAll(this, 'disableAddNew', 'enableAddNew', 'disableEdit' , 'enableEdit');
                Gloria.WarehouseApp.on('disableAddNew', this.disableAddNew);
			 	Gloria.WarehouseApp.on('enableAddNew', this.enableAddNew);
			 	Gloria.WarehouseApp.on('disableEdit', this.disableEdit);
			 	Gloria.WarehouseApp.on('enableEdit', this.enableEdit);
	        },
			
			events : {
				'change #zoneStorageRoom' : 'handleStorageRoom',
				'click #zoneAddNew' : 'addNewZone',
				'click #zoneEdit' : 'editZone',
				'click #zoneDelete' : 'confirmDelete'
			},
			
			addNewZone : function() {
				Gloria.trigger('hideAppMessageView');
				Gloria.WarehouseApp.trigger('zone:show:add');			
			},
			
	
			editZone : function() {
				if (this.selectedRows) {
					Gloria.trigger('hideAppMessageView');
					Gloria.WarehouseApp.trigger('zone:show:edit', _.first(this.selectedRows));
				}
			},

			confirmDelete : function(e) {
                if(this.selectedRows) {                    
                    Dialog.show({
                        title: i18n.t('Gloria.i18n.warehouse.setup.header.zoneConfirmation.deleteTitle'),
                        message: i18n.t('Gloria.i18n.warehouse.setup.header.zoneConfirmation.deleteMessage')
                    }).dialog.on('ok', this.deleteZone, this);                    
                }
           },
           
           deleteZone: function() {
               var storageRoomId = this.$('#zoneStorageRoom').val();
               Gloria.WarehouseApp.trigger('zone:delete', _.first(this.selectedRows), storageRoomId);               
           },
			
			handleStorageRoom : function(e) {
			    e.preventDefault();
				var zoneStorageRoomId = e.currentTarget.value;
				if (zoneStorageRoomId) {
					Gloria.WarehouseApp.trigger('enableAddNew');
				} else {
					Gloria.WarehouseApp.trigger('disableAddNew');
					Gloria.WarehouseApp.trigger('disableEdit');
				}
				Gloria.WarehouseApp.trigger('StorageRoom:change', zoneStorageRoomId);
			},
	        
			enableAddNew : function() {
	        	$('#zoneAddNew').removeAttr('disabled');
	        },
	        
	        disableAddNew : function() {
	        	$('#zoneAddNew').attr('disabled', true);
	        },
	        
	    	enableEdit : function() {
	        	$('#zoneEdit').removeAttr('disabled');
	        },
	        
	        disableEdit : function() {
	        	$('#zoneEdit').attr('disabled', true);
	        },
	        
			gridSelectHandler : function(selectedRows) {
				if(selectedRows) {					
					this.selectedRows = selectedRows;	
					if(selectedRows.length == 1){
						Gloria.WarehouseApp.trigger('enableEdit');
						this.$('#zoneDelete').removeAttr('disabled');
					} else {
					    Gloria.WarehouseApp.trigger('disableEdit');
					    this.$('#zoneDelete').attr('disabled', true);
					}
				} else {
					delete this.selectedRows;
				}
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					permittedActions : this.permittedActions
				}));
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.ZoneButtonsView;
});
