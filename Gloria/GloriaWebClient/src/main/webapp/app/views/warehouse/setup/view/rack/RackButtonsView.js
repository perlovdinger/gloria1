define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'hbs!views/warehouse/setup/view/rack/rack-buttons'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.RackButtonsView = Marionette.ItemView.extend({

			initialize : function(options) {

			},
			
			events : {
				'change #rackStorageRoom' : 'handleStorageRoomChange',
				'change #rackZone' : 'handleZoneChange',
				'click #rackAddNew' : 'addNewRack'
			},
			
			handleStorageRoomChange : function(e) {
				e.preventDefault();
				this.rackStorageRoomId = e.currentTarget.value;
				this.rackZoneId = null;
				Gloria.WarehouseApp.trigger('RackStorageRoom:change', this.rackStorageRoomId);
				this.enableDisableButtons();
			},
			
			handleZoneChange : function(e) {
				e.preventDefault();
				this.rackZoneId = e.currentTarget.value;
				Gloria.WarehouseApp.trigger('RackZone:change', this.rackStorageRoomId, this.rackZoneId);
				this.enableDisableButtons();
			},
			
			enableDisableButtons : function() {
				if(this.rackStorageRoomId && this.rackZoneId) {
					$('#rackAddNew').removeAttr('disabled');
				} else {
					$('#rackAddNew').attr('disabled', true);
				}
			},
			
			addNewRack : function(e) {
				Gloria.WarehouseApp.trigger('rackRow:add', this.rackStorageRoomId, this.rackZoneId);
			},
			
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.RackButtonsView;
});
