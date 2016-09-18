/**
 * This is the wait spinner overlay, which is shown during ajax requests.
 */
define(['underscore',
        'marionette',        
        'views/common/ajaxoverlay/LoadingView'
], function(_, Marionette, LoadingView) {

    var AjaxOverlayView = Marionette.LayoutView.extend({

        tagName : 'div',
        
        className: 'overlay',
        
        regions: {
        	loadBar: '#loadBar'
        },
        
        template: function() {
        	return '<div id="loadBar" class="loadBar text-center"></div>';
        },
        
        initialize: function() {
        	this.loadingView = new LoadingView();
        },
        
        render: function() {
        	this.$el.html(this.template());
        	this.loadBar.show(this.loadingView);            
        	return this;
        },
        
        show: _.debounce(function(flag) {
        	if(flag) {
        		this.$el.show();
        	} else {
        		this.$el.hide();
        	}
        }, 100),
        
        showLoading : function() {        
            this.show(true);
        },
        
        hideLoading : function() {        	            	
        	this.show(false);      	
        }        
    });

    return AjaxOverlayView;
});
