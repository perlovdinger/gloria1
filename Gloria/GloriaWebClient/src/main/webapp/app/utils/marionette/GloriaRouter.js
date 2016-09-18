define(['backbone',
        'marionette',
        'underscore',
        'app',
        'utils/UserHelper'
], function(Backbone, Marionette, _, Gloria, UserHelper) {

    var GloriaRouter = Marionette.AppRouter.extend({
        historyQueue: [],
        historyQueueMaxLength: 20,
    	// if a router is publicRouter then the routes of that router will be accessible for public users(user which are not logged in).
    	// since most of the routes in Gloria are private routes, the default attribute is set to false.
    	publicRouter: false,
    	// Override
    	// Here it is checked if the router is a public router or the current user has been logged in to the system
    	// then the callback for the route will be called otherwise the call back will be ignored.
    	execute: function(callback, args, name) {    		
    		if(this.publicRouter || UserHelper.getInstance().getUserId()) {
    			if(this.historyQueue.length == 0) {
    				this.pushHistory(_.clone(window.location));
    			}
    			Marionette.AppRouter.prototype.execute.apply(this, arguments);    			
    			Gloria.trigger('route:changed', {previous: _.last(this.historyQueue), current: window.location, currentName: name, args: args});
    			this.pushHistory(_.clone(window.location));
    		}
    	},
    	
    	pushHistory: function(location) {
    	    if(this.historyQueue.length >= this.historyQueueMaxLength) {
    	        this.historyQueue.shift();
    	    }     
	        this.historyQueue.push(location);    	        	    
    	},
    	
    	transformAppRoutes: function(gloriaRoutesObject) {
			var routes = {};
			_.each(gloriaRoutesObject, function(value, key) {
				routes[key] = value['handler'] || value;
			});
			return routes;
		}
    });

    return GloriaRouter;
});
