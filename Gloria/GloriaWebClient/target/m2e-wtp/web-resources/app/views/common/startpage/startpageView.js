define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
        'hbs!views/common/startpage/startpage'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, compiledTemplate) {

    var StartPageView = Marionette.LayoutView.extend({ 
        
        events: {
            'click #startPagePanel a': 'handleTabClick'
        },
        
        handleTabClick: function(e) {
            e.preventDefault();             
            Backbone.history.navigate(e.currentTarget.hash.split("#")[1], {
                trigger: false
            });
        },
        
        initialize: function(options) {
            options || (options = {});
            this.module = options.module;
            this.isIP = options.isIP;
        },

        render : function(){
        	var that = this;
            this.$el.html(compiledTemplate({
            	isIP: that.isIP
            }));            
            
            return this;
        }, 
        
        onShow: function() {
            var tab;
            if(this.module && (tab = this.$('#startPagePanel a[href="#' + this.module + '"]'))) {
                tab.tab('show');
            } else {
                this.$('#startPagePanel a:first').tab('show');
            }
            
            
            if(this.$('#startPagePanel li').length <= 1) {
               this.$('#startPagePanel').css({display: 'none'}); 
            }            
        }
    });
    
    return StartPageView;
});
