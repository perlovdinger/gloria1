define(['jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'i18next',
		'jquery-jqpagination',
		'hbs!views/mobile/common/paginator/paginator'
], function($, _, Handlebars, Backbone, Marionette, i18n, Paginator, compiledTemplate) {

	var PaginatorView = Marionette.LayoutView.extend({

		initialize : function(options) {
			if(!options || !options.collection) {
				throw new TypeError('collection must be supplied');
			}
			this.collection = options.collection;
			this.listenTo(this.collection, 'sync', this.render);
		},
		
		tagName : 'caption',
		
		className: 'gloria-paginator',
		
		updatePaginator : function() {
			var that = this;
			this.$el.find('.pagination').jqPagination({
				current_page: this.collection.state.currentPage || 1,
				max_page: this.collection.state.totalPages || 1,
			    paged: function(page) {
			    	that.collection.state.currentPage = page;
			    	that.collection.fetch();
			    }
			});
		},
		
		render : function() {
			this.$el.html(compiledTemplate());
			this.updatePaginator();
			return this;
		}
	});

	return PaginatorView;
});
