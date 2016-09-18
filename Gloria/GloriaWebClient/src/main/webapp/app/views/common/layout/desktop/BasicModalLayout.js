define(['handlebars',
        'marionette',
        'hbs!views/common/layout/desktop/basic-modal-layout'
], function(Handlebars, Marionette, compiledTemplate) {

	var BasicModalLayout = Marionette.LayoutView.extend({

		regions : {
			content : "#modalContent"
		},

		getTemplate : function() {
			return compiledTemplate;
		},
		
		closeAndReset: function() {
		    if (this.content && this.content.$el && this.content.$el.length) {
		        this.content.$el.find('.modal').modal('hide').off('.modal');
		        this.content.reset();
		    }
		}
	});

	return BasicModalLayout;
});
