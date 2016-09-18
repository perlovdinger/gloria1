define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',		
		'i18next',
		'hbs!views/materialrequest/details/view/material-layout'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Template) {

	Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.MaterialLayoutView = Marionette.LayoutView.extend({
			
			regions : {				
				'titlePane' : '#titlePane',
				'buttonPane' : '#buttonPane',
				'gridPane' : '#gridPane'
			},

			getTemplate : function() {
				return Template;
			}
		});
	});

	return Gloria.MaterialRequestApp.View.MaterialLayoutView;
});