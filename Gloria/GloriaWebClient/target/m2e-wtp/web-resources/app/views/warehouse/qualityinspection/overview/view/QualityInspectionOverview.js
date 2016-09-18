define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'hbs!views/warehouse/qualityinspection/overview/view/qualityinspection-overview'
], function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.QualityInspectionOverview = Marionette.LayoutView.extend({
			
			regions : {
				moduleInfo : '#moduleInfo',
				gridInfo : '#gridInfo'
			},

			events : {
				'click #qualityinspectionTab a' : 'handleQualityInspectionTabClick'
	        },
	        
	        handleQualityInspectionTabClick : function(e) {
				e.preventDefault();				
				Backbone.history.navigate('warehouse/qualityinspection/' + e.currentTarget.hash.split("#")[1], {
				    trigger: true
				});
			},
	        
	        initialize : function(options) {
	        	this.module = options.module;
	        	this.isWHQISupport = options.isWHQISupport;
	        },

			render : function() {
				var that = this;
				this.$el.html(compiledTemplate({
					isWHQISupport : that.isWHQISupport
				}));
				return this;
			},
			
			onShow : function() {							
			    var tabId = this.module ? '#qualityinspectionTab a[href="#' + this.module + '"]' : '#qualityinspectionTab a:first';
			    var tabIdEl = this.$(tabId);
			    if(tabIdEl.length) {
			    	tabIdEl.tab('show');			    	
			    } else {
			    	this.$('#qualityinspectionTab a:first').click();
			    }
			}
		});
	});
	
	return Gloria.WarehouseApp.View.QualityInspectionOverview;
});