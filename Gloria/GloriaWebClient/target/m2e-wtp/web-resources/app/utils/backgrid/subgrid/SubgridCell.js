define(['jquery', 'underscore', 'backbone', 'backgrid', 'utils/backgrid/subgrid/SubgridRow'], 
		function($, _, Backbone, Backgrid, SubgridRow) {
	
	require.loadCss('utils/backgrid/subgrid/gloria-subgrid.css');
	
	function requireOptions(options, requireOptionKeys) {
		for ( var i = 0; i < requireOptionKeys.length; i++) {
			var key = requireOptionKeys[i];
			if (_.isUndefined(options[key])) {
				throw new TypeError("'" + key  + "' is required");
			};
		};
	};
	
	var SubgridCell = Backgrid.Cell.extend({

		className : 'subgrid-cell renderable',
		
		icon : function() {
			var iconOptions = '<i class="fa fa-plus-square-o"></i>';
			if (this.state == 'expanded')
				iconOptions = '<i class="fa fa-minus-square-o"></i>';
			return (iconOptions);
		},
				
		initialize : function(options) {
			this.state = 'collapsed';
			requireOptions(options, [ 'model', 'column' ]);
			
			requireOptions(options.column.attributes, [ 'subModule' ]);
			this.subModule = options.column.get('subModule');
			
			requireOptions(options.column.attributes, [ 'optionValues' ]);
			this.model.subcolumns = options.column.get('optionValues');
			this.model.emptyText = options.column.get('emptyText');
			this.column = options.column;
			if (!(this.column instanceof Backgrid.Column)) {
				this.column = new Backgrid.Column(this.column);
			}
			this.listenTo(Backbone, 'backgrid:sort', this.clearSubgrid);
			this.model.bind('remove', this.clearSubgrid, this);
			this.listenTo(this.model, 'subgrid:fetched', this.renderSubgrid);
		},
		/**
		 * Renders a collapsed view.
		 */
		render : function() {
			$(this.el).html(this.icon());
			return this;
		},

		events : {
			'click' : 'stateConverter'
		},
		/**
		 * Checks the current state of the cell, either: appends another row for
		 * the subgrid and appends the grid to the row or removes the row from
		 * the parent grid, and saves the current data the model.
		 */
		stateConverter : function() {
			if (this.state == 'collapsed') {
				this.state = 'expanded';
				Backbone.trigger("subgrid:fetch", this.model, this);
			} else {				
				this.state = 'collapsed';
				this.subrow.remove();
			};
			$(this.el).html(this.icon());
			this.listenTo(Backbone, 'SubgridCell:remove', this.render);
		},
		
		renderSubgrid : function(data) {
			this.clearSubgrid();
			this.model.subcollection = data;
			this.subrow = new SubgridRow({
				columns : this.column.collection,
				model : this.model,
				subModule : this.subModule
			});
			$(this.el).parent('tr').after(this.subrow.render().$el);		
			this.model.subgrid = this.subrow.subgrid;
		},
		
		/**
		 * Binds the remove function with the row when a model is removed.
		 */
		clearSubgrid : function() {
			if(this.subrow) {
				this.subrow.remove();
			}
		},
		
		remove : function() {
			if (this.subrow) {
				this.subrow.remove();
				delete this.subrow;
			}
			return Backbone.View.prototype.remove.apply(this, arguments);
		}
	});
	
	return SubgridCell;
});

