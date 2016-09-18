define(['jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'i18next',
		'hbs!views/common/paginator/search-result'
], function($, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	var SearchResultView = Marionette.LayoutView.extend({
		
		initialize : function(options) {
			if(!options.collection) {
				throw new TypeError('collection must be supplied');
			}
			this.collection = options.collection;
			this.listenTo(this.collection, 'sync', this.render);
		},		

		render : function() {
			this.$el.html(compiledTemplate({
				startCount: Math.min((this.collection.state.currentPage - 1) * this.collection.state.pageSize + 1, this.collection.state.totalRecords),
				endCount: Math.min(this.collection.state.currentPage * this.collection.state.pageSize, this.collection.state.totalRecords),
				totalCount: this.collection.state.totalRecords
			}));
			return this;
		},
	});
	
	return SearchResultView;
});