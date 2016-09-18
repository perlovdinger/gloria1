define(['handlebars',
        'marionette',
        'app',
        'jquery'
], function(Handlebars, Marionette, Gloria, $) {

    var HeaderRegion = Marionette.Region.extend({
        // An attemp for page header to flicker less.
        onShow: function(view) {
            var height = this.$el.height();
            this.$el.css({minHeight: height + 'px'});
        },
       
        setHeaderForApplicationStatus:function(){
            if(this.$el){
      //      this.$el.css({minHeight: "initial"});
            } else {
                $('#pageHeader').css({minHeight: "initial"});
            }
        }
    });

    return HeaderRegion;
});