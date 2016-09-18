define(['app',
        'underscore',
        'backbone',
		'backgrid',
		'htmlSortable',
		'utils/backgrid/ColumnChooser/ColumnHeader'
], function(Gloria, _, Backbone, Backgrid, HTMLSortable, ColumnHeader) {
	
	var ColumnHeaderList = Marionette.CollectionView.extend({
		tagName: 'ul',
		className: 'panel list-group',
		collectionEvents: {
		    'change:renderable': 'onChangeRenderable'
		},
		onChangeRenderable: function() {
			this.render();
		},
		events: {
			'mousedown a': 'onColumnHeaderMouseDown',
			'click a': 'onColumnHeaderClick',
			'sortupdate': 'onSortChange'			
		},
		onColumnHeaderMouseDown: function(e) {
			e.preventDefault();
			e.stopImmediatePropagation();
			e.stopPropagation();
			var target = Backbone.$(e.currentTarget);
			if(!target.hasClass('show-hide')) return; 
			var columnName = target.data('columnName');
			var action = target.data('action');
			Gloria.trigger('columnChooser:hideShow', columnName, action);
		},
		onColumnHeaderClick: function(e) {
			e.preventDefault();
			e.stopImmediatePropagation();
			e.stopPropagation();
		},		
		onSortChange: function(e, ui) {	
			var columnName = ui.item.data('columnName');
			Gloria.trigger('columnChooser:reorder', columnName, ui);
		},
		initialize: function(options) {
			options || (options = {});
			_.extend(this.sortableOptions, (options.sortableOptions || {}));
			this.sortable = options.sortable;
			return Marionette.CollectionView.prototype.initialize.apply(this, arguments);
		},
		childView: ColumnHeader,
		sortableOptions: {
			items: 'li.sortable-item',
			forcePlaceholderSize: true,
			placeholder: '<li>&nbsp;</li>',
			placeholderClass: 'list-group-item background-gray'
		},
		onRender: function(childView) {
			if(this.sortable) {				
				this.$el.sortable('destroy');
				this.$el.sortable(this.sortableOptions);
			}
		},
		onDestroy: function() {
			if(this.sortable) {
				this.$el.sortable('destroy');
			}
		}
	});
	
	return ColumnHeaderList;
});