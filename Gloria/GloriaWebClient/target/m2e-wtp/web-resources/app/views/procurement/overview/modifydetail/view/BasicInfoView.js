define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'hbs!views/procurement/overview/modifydetail/view/basic-info'
], function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.BasicInfoView = Marionette.ItemView.extend({
	        
	        initialize : function(options) {
	        	this.collection = options.collection;
	        	this.procModel = options.procModel;
	        	if(!this.collection) {
	        		throw new Error('collection must be supplied');
	        	}
	        },

	        allAreSame : function (array) {
			    var first = array[0];
			    return array.every(function(element) {
			        return element && element === first;
			    });
			},
			
			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					allAreSame : that.allAreSame(that.collection.pluck('partNumber')),
					data : that.procModel ? that.procModel.toJSON() : {},
					readOnly : !!that.procModel
				}));
				return this;
			}
		});
	});
	
	return Gloria.ProcurementApp.View.BasicInfoView;
});
