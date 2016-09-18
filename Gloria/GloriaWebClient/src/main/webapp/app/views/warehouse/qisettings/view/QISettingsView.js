define(['app',
		'jquery',
		'i18next',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'hbs!views/warehouse/qisettings/view/qi-settings'
], function(Gloria, $, i18n, _, Handlebars, Backbone, Marionette, compiledTemplate) {
	
	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.QISettingsView = Marionette.LayoutView.extend({
    	
			initialize : function(options) {
				this.module = options.module;
			},
			
			regions: {
                moduleInfo : '#moduleInfo'
            },
			
            events : {
            	'click #qiSettingsTab a' : 'handleQISettingsTabClick'
			},
			
			handleQISettingsTabClick : function(e) {
				e.preventDefault();				
				Backbone.history.navigate('warehouse/qisettings/' + e.currentTarget.hash.split("#")[1], {
				    trigger: true
				});
			},
			
	        render : function() {
	        	this.$el.html(compiledTemplate());
				return this;
			},
			
			onShow : function() {							
			    var tabId = '#qiSettingsTab a[href="#' + this.module + '"]';
			    this.$(tabId).tab('show');
			}
	    });
	 });
    
    return Gloria.WarehouseApp.View.QISettingsView;
});
