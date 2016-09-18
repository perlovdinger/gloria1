define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'bootstrap',
		'hbs!views/warehouse/qualityinspection/details/view/qidetail-quality-doc'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Bootstrap, compiledTemplate) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.DocumentView = Marionette.LayoutView.extend({

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

	return Gloria.MaterialApp.View.DocumentView;
});
