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

	var BinLocationCell = Backgrid.Cell.extend({

		initialize: function(options) {
			this.defaultSelect = options.defaultSelect;
		    Backgrid.Cell.prototype.initialize.call(this, options);
		    this.binLocationTypeaheadView = new BinLocationTypeaheadView({
		    	select2Options : _.extend({
					width: 'off',
					containerCss: {
					    minWidth: '100%',
					    width: '0px',
					    maxWidth: '100%'
					}
				}, options.select2Options),
                model: this.model,
                modelAttrName: this.column.get('name'),
                defaultSelect: this.defaultSelect || (this.model && (this.model.get('binlocation') || this.model.get('suggestedBinLocationId') || this.model.get('suggestedBinLocation'))) || null,
                disable : options.disabled,
                callback : function() {
                	Gloria.WarehouseApp.trigger('BinLocation:change', this.model);
				},
				triggerInitially: options.triggerInitially
            });
		},
		
		render: function() {
		    if(!this.column.get('renderable')) return this;		    
		    var binLocationTypeaheadViewEl = this.binLocationTypeaheadView.render().$el;
            this.$el.html(binLocationTypeaheadViewEl);
            this.binLocationTypeaheadView.onShow();
			return this;
		},
		
		remove: function() {		    
		    this.binLocationTypeaheadView.remove();		    
            return Backgrid.Cell.prototype.remove.apply(this, arguments);
        }
	});

	return BinLocationCell;
});
