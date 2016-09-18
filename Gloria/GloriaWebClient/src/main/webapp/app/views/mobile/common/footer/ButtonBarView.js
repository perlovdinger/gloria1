define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'hbs!views/mobile/common/footer/button-bar'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Template) {

	/**
	 * @property attrs: Possible arguments to pass to this view.
	 */
	var ButtonBar = Marionette.LayoutView.extend({
		
		groupKey : 'MENU',

		events : {},
		
		id : 'button-bar',
		
		initialize : function(options) {
			options || (options = {});
			this.buttons = options.buttons;
			this.attachButtons(this.buttons);
		},
		/**
		 * @method This method get the buttons as param and add 'Click' event
		 *         handler to them by using their 'callback' function. It also
		 *         wrap the 'callback' function and after calling it, triggers
		 *         an event using the button name.
		 */
		attachButtons : function(buttons) {
			this.undelegateEvents();
			_.each(buttons, function(value, key) {				
				if(value.type && value.type.toUpperCase() === this.groupKey) {
					_.each(value.buttons, function(menuValue, menuKey) {
						this.mapEvents(menuValue, menuKey);
					}, this);
				} else {
					this.mapEvents(value, key);
				}				
			}, this);
			this.delegateEvents();
		},
		
		mapEvents: function(value, key) {
			this.events['touchstart #' + key] = function(e) {
				this.$el.find('#' + key).addClass('touchstart');
				if (value.callback) {
					value.callback.call(this, e);
				}
				this.trigger(key, e);
			};
		},

		render : function() {
			this.$el.html(Template({
				buttons : _.map(this.buttons, function(value, key) {					
					var val = _.clone(value);
					val['id'] = key;					
					if(val.type && val.type.toUpperCase() === this.groupKey) { 
						_.each(val.buttons, function(menuValue, menuKey) {
							val.buttons[menuKey]['id'] = menuKey;							
						});
					}										
					return val;
				}, this)
			}));

			return this;
		}
	});

	return ButtonBar;
});
