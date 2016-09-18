/**
 * This file contains Gloria's additions to backbone model. This should always be referenced
 * instead of backbone in model.
 */
define(['backbone' 
    ], function(Backbone) {
    
    var GloriaModel = Backbone.Model.extend({
       
        initialize : function(args) {
            return Backbone.Model.prototype.initialize.call(this, args);
        },
        
        fetch : function(options) {
            var myOptions = options || {};            
            myOptions.data = myOptions.data || {};
            return Backbone.Model.prototype.fetch.call(this, myOptions);
        }
    });
    
    return GloriaModel;
});