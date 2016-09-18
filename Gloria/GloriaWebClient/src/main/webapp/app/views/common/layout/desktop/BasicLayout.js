define(['handlebars',
        'marionette',
        'views/common/layout/desktop/HeaderRegion',
        'hbs!views/common/layout/desktop/basic-layout'
], function(Handlebars, Marionette, HeaderRegion, compiledTemplate) {

	var BasicLayout = Marionette.LayoutView.extend({
	    
	    className: "container-fluid",

		regions : {
		    header : {
                regionClass: HeaderRegion,
                selector: '#pageHeader'
            },			
			breadcrumb : "#breadcrumb",
			appMessage : "#appMessage",
			content : "#pageContent",
			footer : "#pageFooter",
			printer : "#printer"
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
