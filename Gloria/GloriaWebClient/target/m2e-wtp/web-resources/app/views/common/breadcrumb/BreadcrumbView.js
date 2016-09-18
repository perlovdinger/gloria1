define([
    'app',   
    'handlebars',
    'backbone',
    'utils/UserHelper',
    'hbs!views/common/breadcrumb/breadcrumb',
    'views/common/moduleSelector/ModuleSelectorView'
], function(Gloria, Handlebars, Backbone, UserHelper, compiledTemplate, ModuleSelectorView) {
   
	var BreadcrumbView = Backbone.Marionette.LayoutView.extend({
        
        initialize : function(options) {			
			this.listenTo(Gloria, 'show:breadcrumb:moduleView', this.moduleSelectorHandler);
        },
        
        regions: {
            moduleSelector: '#breadCrumbModuleSelector'
        },
        
        setOptions: function(options) {
            this.options = options || {};
            this.breadcrumbs = options.breadcrumbs;
            this.i18nOptions = options.i18nOptions || {};            
        },
        
        moduleSelectorHandler: function() {
            this.moduleSelectorView = new ModuleSelectorView();
            if(this.isRendered)
                this.moduleSelector.show(this.moduleSelectorView);                           
        },
        
        className: 'breadcrumb no-padding',

        render : function() { 
            this.i18nOptions.whSiteId = UserHelper.getInstance().getDefaultWarehouse();
     
            if (this.i18nOptions) {
            	var isAnyMissedLink = false;
            	if(this.breadcrumbs) {
            		for(var i = 0; i < this.breadcrumbs.length; i++) {
                		if(this.breadcrumbs[i] && !isAnyMissedLink) {
                            this.breadcrumbs[i].i18nOptions = this.i18nOptions;
                		} else { // If there is an issue in parent link, then do not show successive links as well.
                			isAnyMissedLink = true;
                			this.breadcrumbs.pop(i);
                		}
                    }
            	}
            };
            
            // make sure to disable url for the last that is the current
            if (this.breadcrumbs && this.breadcrumbs.length >= 1){
                var lastIndex = this.breadcrumbs.length - 1;
                if (this.breadcrumbs[lastIndex]) {
                    this.breadcrumbs[lastIndex].url = undefined;                    
                }                
            }
            
            if(this.breadcrumbs.length > 1) this.breadcrumbs.shift();
            
			this.$el.html(compiledTemplate({'breadcrumbs':this.breadcrumbs}));	
			this.isRendered = true;
			return this;
		},
		
		onShow: function() {
		    if(this.moduleSelectorView) {
                this.moduleSelector.show(this.moduleSelectorView);
            }
		}
    });
    
    return BreadcrumbView;
}); 
