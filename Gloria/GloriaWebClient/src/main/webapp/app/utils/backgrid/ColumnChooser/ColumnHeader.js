define(['underscore',
        'backbone',
        'hbs!utils/backgrid/ColumnChooser/ColumnHeader'
], function(_, Backbone, ColumnHeaderTemplate) {
	
	var ColumnHeader = Backbone.View.extend({
		tagName: 'li',
		className: 'list-group-item sortable-item',
		initialize: function(options) {
			var attrs = ['getTemplate'];
			_.extend(this, _.pick((options || {}), attrs));
			this.listenTo(this.model, 'change', this.render);
		},
		types: {			
			visible: 'glyphicon glyphicon-minus',
			hidden: 'glyphicon glyphicon-plus'
		},
		getTemplate: function(view, model) {
			this.icon = this.types.visible;
			this.action = 'hide';
			if(!model.get("renderable")) {
				this.icon = this.types.hidden;
				this.action = 'show';
			}
			
			this.hideable = true;
			var columnChooser = model.get("columnChooser");
			if((columnChooser && columnChooser.hideable === false) || !model.get('name')) {
				this.hideable = false;
			}
			return ColumnHeaderTemplate(this);
		},
		render: function() {
			var template = this.getTemplate(this, this.model);
			this.$el.data('columnName', this.model.get('name'));
			this.$el.html(template);
			
			return this;
		}
	});
	
	return ColumnHeader;
});