define([], function() {
  'use strict';

   var isPromise = function (obj) {
    if (!obj || typeof obj !== 'object') {
      return false;
    }
    
    if (window.Promise && obj instanceof Promise) {
      return true;
    }
    
    return typeof obj.then === 'function';
  };
  
  return {
    /**
     * @param {String} name This is the name of the desired resource module.
     * @param {Function} req Provides a "require" to load other modules.
     * @param {Function} load Pass the module's result to this function.
     * @param {Object} config Provides the optimizer's configuration.
     */
    load: function (name, parentRequire, onload, config) {
      
    	parentRequire([name], function (result) {
    		
    		var onReject = function () {
    			onload.error.apply(null, arguments);
    		};
    		
    		var onResolve = function () {
    			onload.apply(null, arguments);
    		};
    		
    		if (isPromise(result)) {
    			// If the promise supports "done" (not all do), we want to use that to
    			// terminate the promise chain and expose any exceptions.
    			var complete = result.done || result.then;

    			if (typeof result.fail === 'function') {
    				complete.call(result, onResolve);
    				result.fail(onReject);
    			} else {
    				// native Promises don't have `fail`
    				complete.call(result, onResolve, onReject);
    			}    			
    		} else {
    			onload(result);
    		}
    	});
    }
  	/*,
    write: function () {}, 
    pluginBuilder: function () {}
    */
  };
});