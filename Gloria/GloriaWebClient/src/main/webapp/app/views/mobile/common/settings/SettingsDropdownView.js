define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'utils/UserHelper',
		'hbs!views/mobile/common/settings/settings-dropdown'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, UserHelper, compiledTemplate) {

	var SettingsDropdownView = Marionette.CompositeView.extend({

		initialize : function(options) {
			options || (options = {});
		},

		events : {
			'click a[id^="warehouse_"]': 'setDefaultWarehouse',
			'click #logout' : 'logout',
			'touchmove ul#settings' : 'onTouchMoveSettings',
			'touchstart ul.scroll-menu' : 'onTouchStartMenu',
			'touchmove ul.scroll-menu' : 'onTouchMoveMenu'			
		},
		
		onTouchMoveSettings: function(e) {
			e.preventDefault();
		},
		
		onTouchStartMenu: function(e) {
			this.screenY = e.originalEvent.touches[0].screenY;
			this.scrollTop = this.$('ul.scroll-menu').scrollTop();
		},
		
		onTouchMoveMenu: function(e) {
			var scrollTop = e.originalEvent.touches[0].screenY;			
			this.$('ul.scroll-menu').scrollTop(this.scrollTop - (scrollTop - this.screenY) );			
		},

		logout : function(e) {
			e.preventDefault();
			Gloria.trigger('logout');
		},

		setWarehouses : function(warehouseCollection) {
			this.warehouses = warehouseCollection;
			this.render();
		},
		
		setDefaultWarehouse : function(e) {
			e.preventDefault();
        	var warehouseId = e.currentTarget.id.split('warehouse_')[1];
            UserHelper.getInstance().setDefaultWarehouse(warehouseId);
            Gloria.trigger('Warehouse:select', warehouseId);
            this.render();
            Gloria.trigger('reloadPage');
        },

		render : function() {
			var that = this;
			this.$el.html(compiledTemplate({
				user : UserHelper.getInstance().getUser(),
				warehouses : that.warehouses,
				defaultWarehouse : UserHelper.getInstance().getDefaultWarehouse()
			}));
			return this;
		}

	});

	return SettingsDropdownView;
});
