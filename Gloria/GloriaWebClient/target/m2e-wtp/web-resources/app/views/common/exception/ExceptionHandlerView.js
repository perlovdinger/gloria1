/**
 * This file is included in appcache. See cache.manifest for info on how to force refresh
 */
define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
	    'bootstrap',
	    'i18next',
	    'hbs!views/common/exception/exception-handler'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, compiledTemplate) {
    		
	var ExceptionHandlerView = Marionette.LayoutView.extend({
    	
		className : 'modal',

		id : 'exceptionModal',
		
    	events : {
            'click #ok' : 'handleOkClick'
        },
        
        initialize : function(options) {
        	this.message = options.exceptionMessage;
        	this.timeStamp = options.exceptionTimeStamp;
        },

        handleOkClick : function(e) {
			this.$el.modal('hide');
		},
        
		render : function() {
			var that = this;
			this.$el.html(compiledTemplate({
				message: this.message,
	    		timeStamp : this.timeStamp
			}));
			this.$el.modal({
				show : false
			});
			this.$el.on('hidden.bs.modal', function() {
				that.trigger('hide');
			});
			return this;
		},

		onShow : function() {
			this.$el.modal('show');
		},

		onDestroy : function() {
			this.$el.modal('hide');
			this.$el.off('.modal');
		}
    });
	
	return ExceptionHandlerView;
});
