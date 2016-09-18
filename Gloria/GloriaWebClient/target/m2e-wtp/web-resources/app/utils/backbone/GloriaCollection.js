/**
 * This file contains Gloria's additions to backbone collection. This should always be referenced
 * instead of backbone in collections.
 */
define(['backbone' 
    ], function(Backbone) {
    
    var GloriaCollection = Backbone.Collection.extend({
       
        initialize : function(args) {
            return Backbone.Collection.prototype.initialize.call(this, args);
        },
        
        fetch : function(options) {
            var myOptions = options || {};
            myOptions.data = myOptions.data || {};
            return Backbone.Collection.prototype.fetch.call(this, myOptions);
        },
        
        save: function (options) {
            this.sync("update", this, options);
        },
        
        deepClone: function() {
            return new this.constructor(this.invoke('toJSON'));
        }
    });
    
    return GloriaCollection;
});