define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'backgrid',
	    'i18next',
	    'views/common/paginator/PaginatorView',
	    'utils/backgrid/stringHeaderCell',
	    'utils/backgrid/clickableRow',
	    'backgrid-select-all',
	    'bootstrap',
	    'grid-util'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, backgrid, i18n, GloriaPaginator,
		StringHeaderCell, ClickableRow, BackgridSelectAll, Bootstrap, GridUtil) {

	Gloria.module('AdminTeamApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.AdminGridView = Marionette.View.extend({
	
			initialize : function(options) {
				this.module = options.module;
				this.collection = options.collection;
				this.listenTo(this.collection, 'backgrid:selected', this.handleSelectRow);
				this.listenTo(Gloria.AdminTeamApp, 'AdminGrid:clearselection', this.clearSelectedModels);
				this.listenTo(this.collection, 'QueryParams:changed', function() {
					this.clearSelectedModels();
					this.handleSelectRow();
				});
				this.listenTo(Gloria, 'Grid:Filter:clear', this.clearFilter);
			},
			
			clearFilter : function() {
				this.gridView.collection.trigger('Grid:Filter:reset', this.gridView);
			},

	        handleSelectRow : _.debounce(function(e) {
	        	var selectedModels = this.gridView.getSelectedModels();
	            Gloria.AdminTeamApp.trigger('AdminGrid:select', selectedModels);
	        }, 200),
	
	        clearSelectedModels: function() {
				this.gridView.clearSelectedModels();
			},
			
	        render : function() {
	        	var that = this;
	            // Initialize the grid
	            this.gridView = new Backgrid.Grid({
	            	id : that.module + 'Grid',
	                row : ClickableRow,
	                collection : this.collection,
	                emptyText: i18n.t('Gloria.i18n.general.noRow'),
	                columns : [{
		                    name: '',
		                    cell: 'select-row',
		                    headerCell: 'select-all',
		                }, {
		    				name : 'id',
		    				label : i18n.t('Gloria.i18n.admin.header.userId'),
		    				cell : 'string',
		    				editable : false,
		    				headerCell : StringHeaderCell
		    			}, {
		    				name : 'userName',
		    				label : i18n.t('Gloria.i18n.admin.header.userName'),
		    				cell : 'string',
		    				editable : false,
		    				headerCell : StringHeaderCell
		    			}, {
		    				name : 'teamNames',
		    				label : i18n.t('Gloria.i18n.admin.header.teamName'),
		    				cell : 'string',
		    				editable : false,
		    				sortable : false,
		    				headerCell : StringHeaderCell
		    			}
		             ]
	            });
	
	            // Render the grid
	            var $gridView = this.gridView.render().$el;
	            this.$el.html($gridView);

	            // Initialize the paginator
	            this.paginator = new GloriaPaginator({
	                collection : this.collection,
	                grid : this.gridView,
					postbackSafe : true
	            });
	
	            // Render the paginator
	            $gridView.after(this.paginator.render().$el);
	
	            return this;
	        },
	        
	        onShow : function() {
                this.gridView.enableGridColumnManipulation({
                    grid : this.gridView,
                    resizable : {
                        postbackSafe : true
                    }
                });
			},
	        
	        onDestroy: function() {
	            this.gridView.remove();
	            this.paginator.remove();
	        }
	    });
	});

    return Gloria.AdminTeamApp.View.AdminGridView;
});
