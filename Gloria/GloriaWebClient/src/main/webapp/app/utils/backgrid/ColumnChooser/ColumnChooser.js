define([ 'app', 'jquery', 'underscore', 'handlebars', 'backbone', 'marionette',
		'bootstrap', 'i18next',
		'utils/UserHelper',
		'utils/backgrid/ColumnChooser/ColumnChooserModal',
		'utils/backgrid/ColumnChooser/ColumnCollection' ], function(Gloria, $,
		_, Handlebars, Backbone, Marionette, Bootstrap, i18n, UserHelper,
		ColumnChooserModal, ColumnCollection) {

	var ColumnChooser = Marionette.ItemView.extend({

		className : 'btn',

		tagName : 'button',

		id : 'columnChooserButton',

		events : {
			'click' : 'onClick'
		},
		
		storageKey: function(gridId) { 
			return 'ColumnChooser.' + gridId + '.' + UserHelper.getInstance().getUserId();
		},

		initialize : function(options) {
			this.grid = options.grid;
			this.gridId = options.gridId;
			this.load();
			this.listenTo(Gloria, 'columnChooser:hideShow', this.hideShow);
			this.listenTo(Gloria, 'columnChooser:reorder', this.reorder);
			this.listenTo(Gloria, 'columnChooser:sync', this.sync);
			return Marionette.LayoutView.prototype.initialize.apply(this, arguments);
		},
		
		hideShow: function(columnName, action) {
			this.columns.findWhere({name: columnName}).set({
				renderable: (action == 'show'),
				position: this.columns.length
			});
		},
		
		reorder: function(columnName, ui) {
			this.columns.findWhere({name: columnName}).set({position: ui.index + 1}, {trigger: true});
		},
		
		sync: function(gridId, columns) {
			columns.sort();
			this.grid.columns.reset(columns.toJSON());
			window.localStorage.setItem(this.storageKey(gridId), JSON.stringify(this.grid.columns.toJSON()));
			Gloria.trigger('reset:modellayout');
		},
		
		load: function() {
			var columnsConfig = window.localStorage.getItem(this.storageKey(this.gridId));
			if(columnsConfig) {
				try {
					columnsConfig = JSON.parse(columnsConfig);
					var comparator = this.grid.columns.comparator;
					this.grid.columns.comparator = 'position';
					this.grid.columns.each(function(model) {
						var config = _.findWhere(columnsConfig, {name: model.get('name')});
						model.set(_.pick(config, 'position', 'renderable'));
					});
					this.grid.columns.sort();
					this.grid.columns.comparator = comparator;
					this.grid.columns.trigger('reset');
				} catch(e) {
					window.localStorage.removeItem(this.storageKey(this.gridId));
				}
			}
		},

		getTemplate : function() {
			return function() {
				return '<i class="fa fa-cog"></i>';
			};
		},

		onClick : function(e) {
			e.preventDefault();
			Gloria.basicModalLayout.closeAndReset();
			Gloria.basicModalLayout.content.show(new ColumnChooserModal({
				grid : this.grid,
				gridId: this.gridId,
				collection : this.makeColumnCollection()
			}));
		},

		makeColumnCollection : function() {
			if(!this.columns) {
				this.columns = new ColumnCollection();
			}
			this.columns.reset(this.grid.columns.toJSON());			
			this.columns.setPositions().sort();
			return this.columns;
		}
	});

	return ColumnChooser;
});