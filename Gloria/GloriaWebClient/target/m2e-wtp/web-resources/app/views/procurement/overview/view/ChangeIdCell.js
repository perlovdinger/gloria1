define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'backgrid',
		'utils/backbone/GloriaCollection'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Backgrid, Collection) {

	var ChangeIdCell = Backgrid.Cell.extend({

		initialize : function(options) {
			Backgrid.Cell.prototype.initialize.call(this, options);
			this.procurelineId = options.model.get('id');
		},
		
		constructCellHtml : function(jsonData) {
			var linkString = '';
			_.each(jsonData, function(item) {
				var link = '<a href="' + item.id + '">' + item.changeId + '</a>&nbsp;';
				linkString += link;
			});
			return linkString;
		},
		
		render : function() {
            var collection = new Collection();
            collection.url = '/procurement/v1/procurelines/' + this.procurelineId + '/changeids';
            collection.fetch({
            	cache : false,
                async : false,
                success : function(data) {
                	this.$el.html(this.constructCellHtml(data.toJSON()));
                }
            });
			return this;
		},
		
		remove: function() {
            return Backgrid.Cell.prototype.remove.apply(this, arguments);
        }
	});

	return ChangeIdCell;
});
