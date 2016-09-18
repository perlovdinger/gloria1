// This module is degined to orverride backbone related functions and methods.
// The Backbone version which is used in the application at the moment of writing this module is 1.2.3
define([], function() {
	
	var wrapError = function(model, options) {
		var error = options.error;
		options.error = function(resp) {
			if (error) error.call(options.context, model, resp, options);
			model.trigger('error', model, resp, options);
		};
	};

	var overrideCollectionFetch = function(Backbone) {
		Backbone.Collection.prototype.fetch = function(options) {
			options = _.extend({
				parse : true
			}, options);
			var success = options.success;
			var collection = this;
			options.success = function(resp, status, xhr) {
				// Start override
				if (xhr && xhr.status == 204 && !resp) resp = [];
				// End override
				var method = options.reset ? 'reset' : 'set';
				collection[method](resp, options);
				if (success) success.call(options.context, collection, resp, options);
				collection.trigger('sync', collection, resp, options);
			};
			wrapError(this, options);
			return this.sync('read', this, options);
		};
	};

	var initialize = function(Backbone) {
		overrideCollectionFetch(Backbone);
	};

	return {
		initialize : initialize
	};
});