define(['jquery', 'underscore', 'backbone', 'backgrid', 'views/procurement/detail/view/highlightRow',], 
		function($, _, Backbone, Backgrid, HighlightableRow) {
	
	function requireOptions(options, requireOptionKeys) {
		for ( var i = 0; i < requireOptionKeys.length; i++) {
			var key = requireOptionKeys[i];
			if (_.isUndefined(options[key])) {
				throw new TypeError("'" + key  + "' is required");
			};
		};
	};
	
	var SubgridRow = Backbone.View.extend({

		tagName : 'tr',

		className : 'backgrid-subgrid-row renderable',

		submodel : Backbone.Model.extend({}),

		initialize : function(options) {
			var GridColumnView = Backbone.View.extend({
				tagName : 'td',
				className : 'renderable subgrid'
			});
			var SubCollection = Backbone.Collection.extend({
				model : this.submodel
			});
			this.gridColumnView = new GridColumnView({});
			this.sideColumnView = new GridColumnView({});

			this.el.id = this.model.get('id');
			requireOptions(options, [ 'columns', 'model' ]);
			this.columns = options.columns;
			this.subModule = options.subModule;
			var subcolumns = this.subcolumns = options.model.subcolumns;
			
			options.model
			
			
			if(this.subModule == 'ship' && options.model.attributes.materialDirectSendType == 'NO' 
				        && _.findWhere(subcolumns,{name:'partInformation'})) {
				subcolumns = this.subcolumns =  _.without(subcolumns,_.findWhere(subcolumns,{name:'partInformation'}));
			}
			
			if(this.subModule == 'ship' && options.model.attributes.materialDirectSendType == 'YES' 
						&& _.findWhere(subcolumns,{name:'pickListCode'})) {
				subcolumns =  this.subcolumns =  _.without(subcolumns, _.findWhere(subcolumns,{name:'pickListCode'}));
			}
			
			if(this.subModule == 'inpick' &&  _.findWhere(subcolumns,{name:'pickListCode'})) {
				subcolumns = this.subcolumns =  _.without(subcolumns,_.findWhere(subcolumns,{name:'partInformation'}));
			}
			
			
			this.emptyText = options.model.emptyText;
			if (!(subcolumns instanceof Backgrid.Columns)) {
				subcolumns = this.subcolumns = this.model.subcolumns = new Backgrid.Columns(subcolumns);
			}
			var subcollection = this.subcollection = options.model.subcollection;
			if (!(subcollection instanceof Backbone.Collection)) {
				subcollection = this.subcollection = this.model.subcollection = new SubCollection(subcollection);
			}
			this.listenTo(Backbone, 'SubgridCell:remove', this.render);
			this.listenTo(this.subcollection, 'add remove change', this.render);
		},
		/**
		 * Renders a row containing a subgrid for this row's model.
		 */
		render : function() {
			var that = this;
			this.$el.empty();
			this.gridColumnView.el.colSpan = (this.columns.length - 1);
			// Appends the first empty column
			$(this.el).append(this.sideColumnView.render().$el);
			// Appends the subgrid column that spans the rest of the table
			$(this.el).append(this.gridColumnView.render().$el);
			this.subgrid = new Backgrid.Grid({
				columns : this.subcolumns,
				emptyText : this.emptyText,
				collection : this.subcollection,
				id:"SubGrid",
				row : HighlightableRow.extend({ /* for highlighting on condition */
                      highlightCssClass : 'background-orange',
                      conditionCallback : function() {
                          if (this.model.get('items') == '0' && that.subModule == 'inpick') {
                              return "background-orange";
                          } else {
                              return "background-white";
                          }
                      },
                      
                  }), 
			});
			// Appends the Subgrid
			this.gridColumnView.$el.append(this.subgrid.render().$el);
			return this;
		},
		remove : function() {
			this.subgrid && this.subgrid.remove();
			Backbone.View.prototype.remove.apply(this, arguments);
		}
	});
	
	return SubgridRow;
});