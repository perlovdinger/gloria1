define(['handlebars',
        'marionette',
        'app',
        'views/common/layout/mobile/FooterRegion',
        'hbs!views/common/layout/mobile/basic-layout'
], function(Handlebars, Marionette, Gloria, FooterRegion, compiledTemplate) {

	var BasicLayout = Marionette.LayoutView.extend({
	    
	    className: "container mobile-container",

		regions : {
			header : "#pageHeader",			
			appMessage : "#appMessage",
			content : "#pageContent",
			footer : {
				regionClass: FooterRegion,
			    selector: '#pageFooter'
			}
		},

		getTemplate : function() {
			return compiledTemplate;
		},
		
		layoutReset: function() {
            if(this.regionManager) {
                this.regionManager.each(function(region){
                    region && region.reset && region.reset();
                });
            }
        }
		
	});

	return BasicLayout;
});
