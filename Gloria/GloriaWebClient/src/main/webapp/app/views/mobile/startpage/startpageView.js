define(['app',
		'jquery',
		'underscore',
		'handlebars',
		'backbone',
		'marionette',
		'utils/UserHelper',
        'hbs!views/mobile/startpage/startpage'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, UserHelper, compiledTemplate) {

	var StartPageView = Marionette.LayoutView.extend({

		render : function() {
			this.$el.html(compiledTemplate({
				showModules : UserHelper.getInstance().hasUserRole('WH_DEFAULT')
			}));
			return this;
		},

		onShow : function() {
			this.$('#startPagePanel a:first').tab('show');
			if (this.$('#startPagePanel li').length <= 1) {
				this.$('#startPagePanel').css({
					display : 'none'
				});
			};
		}
	});
    
    return StartPageView;
});
