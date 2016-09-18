define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'bootstrap',
		'hbs!views/material/overview/view/material-line'
], function(Gloria, $, _, Handlebars, Marionette, Bootstrap, compiledTemplate) {
    
	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.MaterialLineView = Marionette.LayoutView.extend({
			
			regions : {
				moduleInfo : '#moduleInfo',
				messageInfo : '#messageInfo',
				gridInfo : '#gridInfo'
			},
			
			events : {
				'click a[id^="message_"]' : 'handleMessageLinkClick'
			},
	        
	        handleMessageLinkClick : function(e) {
	        	e.preventDefault();
	        	Backbone.history.navigate('material/linesoverview/requests/' + e.currentTarget.id.split('message_')[1], {
					trigger : true
				});
			},
	        
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			},
			
			onDestroy : function() {
				Gloria.MaterialApp.off(null, null, this);
			}
		});
	});
	
	return Gloria.MaterialApp.View.MaterialLineView;
});
