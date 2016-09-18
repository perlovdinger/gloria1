define(['underscore',
        'utils/backbone/GloriaPageableCollection', 
        'models/MaterialLineQIModel'
        ], function(_, PageableCollection, MaterialLineQIModel) {
	
	// Wrap an optional error callback with a fallback error event.
	var wrapError = function (model, options) {
	   var error = options.error;
	   options.error = function(resp) {
	      if (error) error(model, resp, options);
	      model.trigger('error', model, resp, options);
	   };
	};
	  
	var modelsTOJSON = function(models, options) {
		if(!models) {
			return;
		}
		return _.map(models, function(model){ 			
			return model.toJSON(options);				
		});
	};

	var MaterialLineQICollection = PageableCollection.extend({
		model : MaterialLineQIModel,
		url : '/procurement/v1/materiallines/qi',
		isValid: function(options) {
			var isValid = true;
			this.each(function(model) {
				if(!model.isValid(options || {})) { 
					isValid = false;
				}
			});
			return isValid;
		},
		/**
		 * @author a043104
		 * @param {String} [attr=null] The attribute to be checked if it has changed or not.
		 * Only if attribute has been changed the method will be executed. 
		 */
		updateChangedModels : function(attr, options) {			
			if(!options) {
				options = attr;
				attr = null;
			}
			
			var changedModels = this.filter(function(model) {
				return !model.isNew() && model.hasChanged(attr);
			});
						
			if(!changedModels || changedModels.length == 0) {
				return;
			}
			
			var modelsData = JSON.stringify(modelsTOJSON(changedModels));					
			
			options = _.extend((options || {}), {
				contentType: 'application/json',		    
				data: modelsData
			});
						
			options.remove || (options.remove = false);
			if (options.parse === void 0) options.parse = true;
		    var collection = this;
		    var success = options.success;
		    options.success = function(resp) {
		      var method = options.reset ? 'reset' : 'set';
		      collection[method](resp, options);
		      if (success) success(collection, resp, options);
		      collection.trigger('sync', collection, resp, options);
		    };
		    wrapError(this, options);
			
		    return Backbone.sync.call(this, 'update', this, options);
		}
	});

	return MaterialLineQICollection;
});