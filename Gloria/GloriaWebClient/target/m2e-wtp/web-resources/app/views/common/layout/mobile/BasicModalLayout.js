define(['handlebars',
        'marionette',
        'hbs!views/common/layout/mobile/basic-modal-layout'
], function(Handlebars, Marionette, compiledTemplate) {

	var BasicModalLayout = Marionette.LayoutView.extend({

		regions : {
			content : "#modalContent"
		},

		getTemplate : function() {
			return compiledTemplate;
		}
	});

	return BasicModalLayout;
});
