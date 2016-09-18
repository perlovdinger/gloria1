define(['app',
        'jquery',
        'marionette',
		'hbs!views/material/overview/view/empty-requestlist-dialog'
], function(Gloria, $, Marionette, compiledTemplate) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.EmptyRequestListDialog = Marionette.LayoutView.extend({

			initialize : function(options) {
			},

			className : 'modal',

			id : 'noRequestList-modal',

			events : {
				'click #close' : 'close'
			},

			close : function() {
				this.$el.modal('hide');
			},

			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			},

			onShow : function() {
				this.$el.modal('show');
			},

			onDestroy : function() {
				this.$el.modal('hide');
				this.$el.off('.modal');
			}
		});
	});

	return Gloria.MaterialApp.View.EmptyRequestListDialog;
});