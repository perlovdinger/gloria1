define(['handlebars',
        'marionette',
        'app'        
], function(Handlebars, Marionette, Gloria) {

    var FooterRegion = Marionette.Region.extend({
        
        empty: function() {            
            Marionette.Region.prototype.empty.apply(this, arguments);
            this.visibleHide();
        },
        
        show: function() {
            Marionette.Region.prototype.show.apply(this, arguments);
            this.visibleHide();
        },
        
        visibleHide: function() {
            this._ensureElement();
            if(this.$el.is(':empty')) {
                this.$el.addClass('hidden');
            } else {
                this.$el.removeClass('hidden');
            }
        }        
    });

    return FooterRegion;
});
