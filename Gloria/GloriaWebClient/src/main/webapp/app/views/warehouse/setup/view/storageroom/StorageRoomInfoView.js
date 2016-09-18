define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'hbs!views/warehouse/setup/view/storageroom/storage-room-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.StorageRoomInfoView = Marionette.LayoutView.extend({

			regions : {
				controlButtons : '#controlButtons',
				gridModule : '#gridModule'
			},

			events : {

			},

			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.StorageRoomInfoView;
});
