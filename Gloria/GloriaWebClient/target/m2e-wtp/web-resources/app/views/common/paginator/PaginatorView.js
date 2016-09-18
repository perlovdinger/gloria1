define(['jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'i18next',
		'backgrid-paginator',
		'utils/backgrid/ColumnChooser/ColumnChooser',
		'views/common/paginator/SearchResultView',
		'utils/UserHelper',
		'hbs!views/common/paginator/paginator'
], function($, _, Handlebars, Backbone, Marionette, i18n, Paginator,
		ColumnChooser, SearchResultView, UserHelper, compiledTemplate) {

	var PaginatorView = Marionette.LayoutView.extend({

		tagName : 'div',
		
		className: 'gloria-paginator',
		
		events : {
			"change #ItemsPerPage" : "changeItemsPerPage",
			"click a": "changePage"
		},
		
		regions: {
			columnChooser: 'div[id^="columnChooser_"]',
			searchResult: 'div[id="search-result"]'
		},
		
		initialize : function(options) {
			if(!options.collection) {
				throw new TypeError('collection must be supplied');
			}
			this.collection = options.collection;
			this.postbackSafe = options.postbackSafe;
			this.app = 'Gloria';
			this.subapp = Backbone.history.fragment.split('/')[0];
			this.id = options.grid.id;			
			this.showColumnSettings = options.grid ? options.showColumnSettings : false;
			this.grid = options.grid;
		},
		
		changeItemsPerPage : function(e, val) {
			var that = this;
        	if (e.target.value && this.paginator) {
        		that.grid.clearSelectedModels();
        		this.paginator.collection.state.currentPage = 0;
        		this.paginator.collection.setPageSize(Number(e.target.value), {reset: true});
        		if(that.postbackSafe) {
        			try {
        				var storedData = window.localStorage.getItem(that.app + '.' + that.subapp + '.' + that.id + '.' + UserHelper.getInstance().getUserId());
            			var storedDataJSON = JSON.parse(storedData);
            			if(storedDataJSON) {
            				var pageObject = storedDataJSON['pageSize'];
                			if(pageObject) {
                				delete pageObject;
                			}
            			}
            			if(storedDataJSON) {
            				_.extend(storedDataJSON, {pageSize : parseInt(e.target.value)});
            			} else {
            				storedDataJSON = {pageSize : parseInt(e.target.value)};
            			}            			
            			window.localStorage.setItem(that.app + '.' + that.subapp + '.' + that.id + '.' + UserHelper.getInstance().getUserId(), JSON.stringify(storedDataJSON));
					} catch (e) {
						// TODO: handle exception
					}
        		}
        	}
        },
        
        changePage : function(e) {
        	this.grid.clearSelectedModels();
		},

		render : function() {
			this.$el.html(compiledTemplate({
				cid: this.cid
			}));
			this.paginator = new Backgrid.Extension.Paginator({
				collection : this.collection,
				goBackFirstOnSort : false
			});
			this.$el.append(this.paginator.render().$el);
			if(this.collection.state.pageSize) {
			    this.$('#ItemsPerPage').val(this.collection.state.pageSize);
			}
			
			if(this.showColumnSettings) {
				this.addColumnChooser();
			}
			
			this.showSearchResult();
			
			return this;
		},
		
		addColumnChooser: function() {
			this.columnChooserView = new ColumnChooser({
				grid: this.grid,
				gridId: (this.subapp + '.' + this.id)
			});
			this.columnChooser.show(this.columnChooserView);
		},
		
		showSearchResult: function() {
			this.searchResultView = new SearchResultView({
				collection: this.collection
			});
			this.searchResult.show(this.searchResultView);
		},
		
		remove: function() {
			this.columnChooser.empty();
			return Marionette.LayoutView.prototype.remove.apply(this, arguments);
		}
	});

	return PaginatorView;
});
