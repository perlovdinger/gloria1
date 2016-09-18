define([
    'app',    
    'jquery',
    'handlebars',
    'backbone',
    'marionette',
    'bootstrap',
    'hbs!views/common/startpage/about'
], function(Gloria, $, Handlebars, Backbone, Marionette, Bootstrap, compiledTemplate) {
	
	var AboutView = Marionette.View.extend({
	    
	    className : 'modal',
	    
	    events: {
	        'hidden.bs.modal' : 'onHide'
	    },
    	
	    template: compiledTemplate,
	    
	    render: function() {	    		    	
	        this.$el.html(this.template());
	        this.$el.modal({
                keyboard: true,
                show: false
            });
            
	        return this;
	    },
	    
	    onShow: function() {
	        this.$el.modal('show');
	    },
	    
	    onHide: function(e) {
	        Gloria.trigger('reset:modellayout');
	    },
	    
	    onDestroy: function() {
	        this.$el.modal('hide');
	        this.$el.off('.modal');            
	    }
	});
	
	return AboutView;
});