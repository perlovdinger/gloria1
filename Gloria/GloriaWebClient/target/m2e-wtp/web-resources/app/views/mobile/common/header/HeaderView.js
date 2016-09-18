define(['app',
        'jquery',
        'underscore',
	    'handlebars',
	    'backbone',
	    'marionette',
	    'i18next',
	    'utils/UserHelper',
	    'hbs!views/mobile/common/header/header'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, compiledTemplate) {
	
	var HeaderView = Marionette.LayoutView.extend({

		initialize : function(options) {			
			this.listenTo(Gloria, 'change:title', this.updateTitle);
			this.listenTo(Gloria, 'Warehouse:select', this.updateWarehouse);
		},
		
		events : {
			'touchstart #home' : 'home'
		},
		
		home : function(e) {
			e.preventDefault();
			Gloria.trigger('change:title', 'Gloria.i18n.gloria');
			Gloria.trigger('showHomePage');
		},
		
		updateTitle : function(title) {
			this.$el.find('#title').text(i18n.t(title) || i18n.t('Gloria.i18n.gloria'));
		},
		
		updateWarehouse : function(warehouseId) {
			this.$el.find('#warehouse').text(warehouseId == 'null' ? '' : warehouseId);
		},
		
		render : function() {
			if(UserHelper.getInstance().isUserLoggedIn()) {
				this.$el.html(compiledTemplate({	
					headerKey : 'Gloria.i18n.gloria'
				}));
			}
			return this;
		}
	});

    return HeaderView;
});
