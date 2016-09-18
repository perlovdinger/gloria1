define([ 'backgrid' ], function(Backgrid) {

	var ColumnCollection = Backgrid.Columns.extend({
		initialize: function() {
			this.listenTo(this, 'change:position', this.reposition);
			return Backgrid.Columns.prototype.initialize.apply(this, arguments);
		},
		sortKey : "position",
		comparator : function(item) {
			return item.get(this.sortKey) || 999;
		},
		reposition: function(model, newValue, collection, condition) {			
			var anotherModel = _.difference(this.where({position: newValue}), [model]);
			
			if(anotherModel[0]) {
				var anotherModelPosition = anotherModel[0].get('position');
				var position = 0;
				if(_.isUndefined(condition)) 
					condition = (newValue > model.previous('position')); 
				if(condition) {
					position = anotherModelPosition - 1;
				} else {
					position = anotherModelPosition + 1;
				}
				anotherModel[0].set({position: position}, {silent: true});
				this.reposition(anotherModel[0], position, this, condition);
				return;
			}
			this.sort();
			this.setPositions();
		},
		setPositions : function() {
			_.each(this.models, function(model, index) {
				model.set({position: index + 1}, {silent : true});
			});
			return this;
		}
	});
	
	return ColumnCollection;
});