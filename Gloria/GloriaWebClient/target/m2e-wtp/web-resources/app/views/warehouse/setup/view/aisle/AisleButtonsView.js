define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'utils/dialog/dialog',
		'hbs!views/warehouse/setup/view/aisle/aisle-buttons'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Dialog,compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AisleButtonsView = Marionette.ItemView.extend({

			initialize : function(options) {
			    this.warehouseModel = options.warehouseModel;
			    Gloria.WarehouseApp.on('AisleGridView:select', this.gridSelectHandler, this);
			},
			
			events : {
				'change #aisleStorageRoom' : 'handleStorageRoomChange',
				'change #aisleZone' : 'handleZoneChange',
				'click #aisleAddNew' : 'addNewAisle',
				'click #aisleDelete' : 'confirmDelete'
			},
			
			gridSelectHandler: function(selectedRows) {
			    var disabled = 'disabled';
			    if(selectedRows) {
			        this.selectedRows = selectedRows;
			        disabled = selectedRows.length == 1 ? false : 'disabled';
			    }
			    this.$('#aisleDelete').attr('disabled', disabled);
			},
			
			handleStorageRoomChange : function(e) {
				e.preventDefault();
				this.aisleStorageRoomId = e.currentTarget.value;
				this.aisleZoneId = null;
				Gloria.WarehouseApp.trigger('AisleStorageRoom:change', this.aisleStorageRoomId);
				this.enableDisableButtons();
			},
			
			handleZoneChange : function(e) {
				e.preventDefault();
				this.aisleZoneId = e.currentTarget.value;
				Gloria.WarehouseApp.trigger('AisleZone:change', this.aisleStorageRoomId, this.aisleZoneId);
				this.enableDisableButtons();
			},
			
			enableDisableButtons : function() {
				if(this.aisleStorageRoomId && this.aisleZoneId) {
					$('#aisleAddNew').removeAttr('disabled');
				} else {
					$('#aisleAddNew').attr('disabled', 'true');
				}
			},
			
			addNewAisle : function(e) {
				Gloria.WarehouseApp.trigger('aisleRow:add', this.aisleStorageRoomId, this.aisleZoneId);
			},
			
			confirmDelete : function(e) {
                if(this.selectedRows) {
                    var context = {context: this.warehouseModel.get('setUp').toUpperCase()};
                    Dialog.show({
                        title: i18n.t('Gloria.i18n.warehouse.setup.header.aisleConfirmation.deleteTitle', context),
                        message: i18n.t('Gloria.i18n.warehouse.setup.header.aisleConfirmation.deleteMessage', context)
                    }).dialog.on('ok', this.deleteAisle, this);                    
                }
            },
			
			deleteAisle: function(e) {
			    var storageRoomId = this.$('#aisleStorageRoom').val();
			    var zoneId = this.$('#aisleZone').val();
	            Gloria.WarehouseApp.trigger('aisleRow:delete', _.first(this.selectedRows), storageRoomId, zoneId);
			},
			
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.AisleButtonsView;
});

