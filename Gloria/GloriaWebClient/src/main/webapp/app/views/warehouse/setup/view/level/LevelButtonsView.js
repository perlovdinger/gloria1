define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'utils/dialog/dialog',
		'hbs!views/warehouse/setup/view/level/level-buttons'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n,Dialog, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.LevelButtonsView = Marionette.ItemView.extend({

			initialize : function(options) {
				this.options = options;
				Gloria.WarehouseApp.on('LevelGridView:select', this.gridSelectHandler, this);
			},
			
			events : {				
				'change #levelStorageRoom' : 'handleLevelStorageRoom',
				'change #levelZone' : 'handleLevelZone',
				'change #levelAisle' : 'handleLevelAisle',
				'click #levelAddNew' : 'addNewLevel',
                'click #levelDelete' : 'confirmDelete'
			},
			
			gridSelectHandler: function(selectedRows) {
                var disabled = 'disabled';
                if(selectedRows) {
                    this.selectedRows = selectedRows;
                    disabled = selectedRows.length == 1 ? false : 'disabled';
                }
                this.$('#levelDelete').attr('disabled', disabled);
            },
			
			handleLevelStorageRoom : function(e) {
				e.preventDefault();
				this.levelStorageRoomId = e.currentTarget.value;
				this.levelZoneId = null;
				this.levelAisleId= null;
				Gloria.WarehouseApp.trigger('LevelStorageRoom:change', this.levelStorageRoomId);
				this.enableDisableButtons();
			},
			
			handleLevelZone : function(e) {
				e.preventDefault();
				this.levelZoneId = e.currentTarget.value;
				this.levelAisleId = null;
				Gloria.WarehouseApp.trigger('LevelZone:change', this.levelStorageRoomId, this.levelZoneId);
				this.enableDisableButtons();
			},
			
			handleLevelAisle : function(e) {
				e.preventDefault();				
				this.levelAisleId =  e.currentTarget.value;
				Gloria.WarehouseApp.trigger('LevelAisle:change', this.levelStorageRoomId, this.levelZoneId, this.levelAisleId);
				this.enableDisableButtons();
			},
			
			enableDisableButtons : function() {
                if(this.levelStorageRoomId && this.levelZoneId && this.levelAisleId) {
                    $('#levelAddNew').removeAttr('disabled');
                } else {
                    $('#levelAddNew').attr('disabled', 'true');
                }
            },
			
			addNewLevel : function(e) {
                Gloria.WarehouseApp.trigger('baySetting:add', this.levelStorageRoomId, this.levelZoneId, this.levelAisleId);
            },
            
            confirmDelete : function(e) {
                if(this.selectedRows) {                    
                    Dialog.show({
                        title: i18n.t('Gloria.i18n.warehouse.setup.header.levelConfirmation.deleteTitle'),
                        message: i18n.t('Gloria.i18n.warehouse.setup.header.levelConfirmation.deleteMessage')
                    }).dialog.on('ok', this.deleteLevel, this);                    
                }
            },
            
            deleteLevel: function(e) {
                var storageRoomId = this.$('#levelStorageRoom').val();
                var zoneId = this.$('#levelZone').val();
                var aisleId = this.$('#levelAisle').val();
                Gloria.WarehouseApp.trigger('baySetting:delete', _.first(this.selectedRows), storageRoomId, zoneId, aisleId);
            },
			
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.LevelButtonsView;
});
