define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'utils/UserHelper',
        'hbs!views/common/pagenotfound/page-not-found'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, UserHelper, compiledTemplate) {

	PageNotFoundView = Marionette.View.extend({
            
            initialize: function(options) {
                options || (options = {});
            },
    
            render: function() {
                this.$el.html(compiledTemplate());
                return this;
            },
            
            onDestroy : function() {
            	// 
            }
        }); 

    return PageNotFoundView;
});
