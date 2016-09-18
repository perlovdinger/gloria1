define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'bootstrap',
        'i18next',
        'select2',
        'utils/UserHelper',
        'text!views/common/moduleSelector/module-config.json',
        'hbs!views/common/moduleSelector/moduleSelector'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, Bootstrap, i18n, select2, UserHelper, ModuleConfig, compiledTemplate) {
	
	var ModuleSelector = Backbone.Marionette.View.extend({
        	    
        tagName: 'select',
        
        id: 'view-select',
        
        className: 'form-control',
        
        events : {
            'change' : 'onChange'
        },
        
        readAvailableModules : function() {
        	var viewStateFilterOptions = {};
        	var moduleConfigObj = JSON.parse(ModuleConfig);
        	_.each(moduleConfigObj.modules, function(module, name) {
        		_.each(module.submodules, function(obj, submodule) {
                    if(UserHelper.getInstance().hasPermission('view', obj.mapper)) {
                        viewStateFilterOptions[name + '/' + submodule] = obj.i18nkey;
                    }
            	});
        	});
        	return [viewStateFilterOptions];
		},
		
		getState : function(availableModules) {
			var state = null;
			var currentHash = Backbone.history.fragment;
			_.each(availableModules[0], function(value, key) {
				if(currentHash.indexOf(key) != -1) {
		        	state = key;
				}
			});
			return state;
		},
        
        onChange : function(e) {
            Backbone.history.navigate(e.currentTarget.value, {trigger : true});
        },

        render : function() {
        	var availableModules = this.readAvailableModules();
			this.$el.html(compiledTemplate({
			    'viewStateFilterOptions' : availableModules,
                'state' : this.getState(availableModules)
			}));
			return this;
		}
    });
    
    return ModuleSelector;
}); 
