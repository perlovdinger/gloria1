define(['underscore', 'app'], function(_, Gloria) {
    
    var ZoomHelper = function() {        
        this.initialize();        
        Gloria.trigger('change:zoom', { 
            initialZoom: 1,
            previousZoom: 1, 
            newZoom: this.getScale()                    
        });
    };
    
    _.extend(ZoomHelper.prototype, {
        
        initialize: function() {
            this.initialZoom = this.getScale();
            this.previousZoom = this.initialZoom;
            $(window).on('resize', _.bind(this.zoomChanged, this));            
        },
        
        getScale: function() {
            //return document.body.clientWidth / window.innerWidth;
            return window.screen.width / window.innerWidth;
        },
        
        zoomChanged: function() {            
            var previousZoom = this.previousZoom;           
            var newZoom = this.getScale();
            if (this.previousZoom != newZoom) {
                // zoom has changed                
                this.previousZoom = newZoom; 
                
                Gloria.trigger('change:zoom', {
                    initialZoom: this.initialZoom,
                    previousZoom: previousZoom, 
                    newZoom: newZoom                    
                });
            }
        },
        
        remove: function() {
            $(window).off('resize');            
        }
    });
    
    //Making this Module as Singleton 
    var instance;
    var getInstance = function(options) {
        if (!instance) {
            instance = new ZoomHelper(options);
        }
        return instance;
    };

    return {
        getInstance : getInstance
    };
});