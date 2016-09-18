/** 
 * DispatchNoteGeneralInfoView is a Subview in DispatchNote Page in Gloria 
 */
define([ 'app', 'jquery', 'underscore', 'backbone', 'marionette',
		'hbs!views/warehouse/pick/view/dispatchNote-general-info' ], function(
		Gloria, $, _, Backbone, Marionette, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone,
			Marionette, $, _) {

		View.DispatchNoteGeneralInfoView = Marionette.View.extend({

			initialize : function(options) {
				this.dispatchNoteModel = options.model;
				this.requestListModel= options.requestListModel;
				this.isShipped = options.isShipped;
			},

			events : {
				'show .js-date' : 'stopPropagation'
			},

			stopPropagation : function(e) {
				e.stopPropagation();
				e.stopImmediatePropagation();
			},

			render : function() {
				this.$el.html(compiledTemplate({
					'data' : this.dispatchNoteModel.toJSON(),
					'requestListModel': this.requestListModel.toJSON(),
					'isShipped' : this.isShipped
				}));
				this.$('.date').datepicker();
				return this;
			},

			onDestroy : function() {
				this.$('.date').datepicker('remove');
			}
		});

	});

	return Gloria.WarehouseApp.View.DispatchNoteGeneralInfoView;
});
