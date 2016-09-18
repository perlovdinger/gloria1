define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'hbs!views/common/settings/settings-dropdown'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, compiledTemplate) {

	var SettingsDropdownView = Marionette.CompositeView.extend({
		
		setWarehouses: function(warehouseCollection) {
		    this.dropdownItems = warehouseCollection;
		    this.render();
		},
        
        render: function() {
        	var that = this;
            this.$el.html(compiledTemplate({
            	items : that.dropdownItems
            }));
            return this;
        },
        
        onShow: function() {
			var settingsHTML = this.$el.find('#settings').html().trim();
			if(!settingsHTML) { // If no options are available
				this.$el.find('li.dropdown').empty();
			}
		}
	});

	return SettingsDropdownView;
});
