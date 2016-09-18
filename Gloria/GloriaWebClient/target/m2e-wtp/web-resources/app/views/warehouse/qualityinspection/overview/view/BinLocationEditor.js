define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'bootstrap',
		'backgrid',
		'i18next',		
		'utils/typeahead/BinLocationTypeaheadView'		
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, Backgrid, i18n, BinLocationTypeaheadView) {

	var BinLocationCell = Backgrid.CellEditor.extend({

		initialize: function(options) {
			Backgrid.CellEditor.prototype.initialize.apply(this, arguments);
			this.defaultSelect = options.defaultSelect;
		    this.binLocationTypeaheadView = new BinLocationTypeaheadView({
		    	select2Options : _.extend({
		    		width: 'element'
				}, options.select2Options),
                model: this.model,
                modelAttrName: this.column.get('name'),
                defaultSelect: this.defaultSelect || (this.model && (this.model.get('binLocation'))) || '',
                disable : options.disabled,
                callback : function() {                	
                	options.model.trigger("backgrid:edited", options.model, options.column, new Backgrid.Command({
	                    keyCode: 13
	                }));
				},
				triggerInitially: options.triggerInitially
            });
		},
		
		events: {
			"select2-close": "onDropDownClose"
        },
	        
        onDropDownClose: function(e) {
            var model = this.model;
            var column = this.column;
	                        
            var previousValue = model.get(this.column.get("name"));
            var val = this.binLocationTypeaheadView.$el.select2('val');
            val = val || val.length > 0 ? Number(val) : 0;
            var isChanged = (previousValue !== val);
            if(!isChanged) {
                e.stopPropagation();
                model.trigger("backgrid:edited", model, column, new Backgrid.Command({
                    keyCode: 27
                }));
            }
        },
		
		render: function() {
			if(!this.column.get('renderable')) return this;		    
		    var binLocationTypeaheadViewEl = this.binLocationTypeaheadView.render().$el;
            this.$el.html(binLocationTypeaheadViewEl);
            this.binLocationTypeaheadView.onShow();
			return this;
		},
		
		postRender: function (model, column) {
            if (column == null || column.get("name") == this.column.get("name")) {               
                this.binLocationTypeaheadView.$el.select2('open');
            }
            return this;
        },
		
		remove: function() {		    
		    this.binLocationTypeaheadView.remove();		    
            return Backgrid.CellEditor.prototype.remove.apply(this, arguments);
        }
	});

	return BinLocationCell;
});
