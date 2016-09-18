define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'jquery.fileupload',
		'bootstrap',
		'hbs!views/deliverycontrol/myorderoverview/details/view/problem-doc'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, FileUpload, Bootstrap, compiledTemplate) {

	Gloria.module('DeliveryControlApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ProblemDocView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.collection = options.collection;
				this.collection.off('add remove change');
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					metas : this.collection ? this.collection.toJSON() : []
				}));
				return this;
			},
			
			onShow : function() {
				this.collection.on('add remove change', this.render);
			}
		});
	});

	return Gloria.DeliveryControlApp.View.ProblemDocView;
});
