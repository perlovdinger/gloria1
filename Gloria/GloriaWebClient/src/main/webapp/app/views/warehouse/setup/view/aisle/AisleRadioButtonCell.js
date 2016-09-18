define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'backgrid',
        'i18next',
		'hbs!views/warehouse/setup/view/aisle/aisle-radio-button-cell'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BackgridRef, i18n, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.AisleRadioButtonCell = Backgrid.BooleanCell.extend({
			
			initialize : function(options) {		
				this.template = compiledTemplate;
				Backgrid.Cell.prototype.initialize.call(this, options);
			},
			
			events : {
				'change input[name^="baySides"]' : 'handleRadioButtonChange'
			},
			
			handleRadioButtonChange : function(e) {
				this.model.set('baySides', e.currentTarget.value); // will trigger model change event!
			},
			
			render : function() {
				var that = this;
		        this.$el.html(this.template({
		        	name :  'baySides_' +  this.model.id,
					value : function() {
						return that.column.attributes.colName ? that.column.attributes.colName.split('baySides')[1]
								: that.column.attributes.name.split('baySides')[1];
					},
					isChecked : function() {
						var whichColumn = that.column.attributes.colName ? that.column.attributes.colName.split('baySides')[1]
						: that.column.attributes.name.split('baySides')[1];
						if(that.model.get('baySides') == whichColumn) {
							return true;
						}
						return false;
					}
		        }));
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.AisleRadioButtonCell;
});
