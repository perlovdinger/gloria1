define(['app',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',		
		'i18next',
		'hbs!views/materialrequest/overview/view/overview-layout'
], function(Gloria, _, Handlebars, Backbone, Marionette, i18n, Template) {

	Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.OverviewLayout = Marionette.LayoutView.extend({
			
			regions : {
				'buttonPane' : '#moduleInfo [class^=col-]:first',
				'controlPane' : '#moduleInfo',
				'gridPane' : '#gridPane'
			},

			getTemplate : function() {
				return Template;
			}
		});
	});

	return Gloria.MaterialRequestApp.View.OverviewLayout;
});