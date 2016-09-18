define([
        'underscore',
        'backgrid', 
        'views/warehouse/qualityinspection/overview/view/BinLocationEditor'
], function(_, Backgrid, BinLocationEditor) {
	
	var BinLocationCell	= Backgrid.Cell.extend({
		initialize: function(options) {
			var that = this;
			if(options.className) {
				this.$el.addClass(options.className);
			}
			this.initOptions = options;
			this.editor = function(options) {				
				return new BinLocationEditor(_.defaults(options, that.initOptions));
			};
		    Backgrid.Cell.prototype.initialize.call(this, options);		    
		},
		/**
         * Override. Removed "this.stopListening(this.currentEditor)" 
         * which caused loosing of the current view listeners on the model.
         */
        exitEditMode: function() {
            this.$el.removeClass("error");
            try {
        		this.currentEditor && this.currentEditor.remove();
        	}catch(e){}            
            delete this.currentEditor;
            this.$el.removeClass("editor");
            this.render();
        },
        
        remove: function() {
        	try {
        		this.currentEditor && this.currentEditor.remove();
        	}catch(e){}
        	Backgrid.Cell.prototype.remove.apply(this, arguments);	
        }
	});
	
	return BinLocationCell;
});