define([
	'underscore', 
	'i18next', 
	'libs/i18next-xhr-backend/i18nextXHRBackend.min', 
	'libs/i18next-localstorage-cache/i18nextLocalStorageCache.min'
], function(_, i18n, i18nextXHRBackend, i18nextLocalStorageCache) {
	
	var isInitialized = false;
	var variableRegex; 
	
	var getVariableRegex = function(i18n) {
		var interpolationPrefix = i18n.options.interpolation.prefix || '__';
		var interpolationSuffix = i18n.options.interpolation.suffix || '__';
		return new RegExp(interpolationPrefix + '.*' + interpolationSuffix, 'i');
	};
	
	var removeUndefinedVariableNames = {
		name: 'removeUndefinedVariableNames',
		type: 'postProcessor',
		process: function(value, key, options, translator) {
			if(!(translator.interpolator.prefixEscaped || translator.interpolator.suffixEscaped)) {
				value = value.replace ? value.replace(variableRegex, '') : value;
			}
			return value;
		}
	};
	
	var desktopOptions = {        
		backend: {
			loadPath: 'app/resources/locales/__lng__/__ns__.json'			
		},
		interpolation: {
			prefix: '__',
			suffix: '__'
		},
		cache: {			
		    enabled: true,		    
		    prefix: 'i18next_res_',		    
		    expirationTime: 28800000	// In ms - set to once every 8 hrs
		},
        ns: ['messages', 'components', 'errormessages'],          
        defaultNS: ['messages'],        
        fallbackLng: ['en'],        
        postProcess: ['removeUndefinedVariableNames']       
	};
	
	var mobileOptions = _.extend(_.clone(desktopOptions), {
		backend: {
			loadPath: 'app/resources/locales/__lng__/__ns__.mobile.json'			
		},    		
        ns: ['messages', 'components']           
    });
	
	i18n.use(i18nextXHRBackend)
		.use(i18nextLocalStorageCache)		
		.use(removeUndefinedVariableNames);
	
	var initialize = function(mode, i18nOptions, callback) {
		if(!isInitialized) {
			if(!i18nOptions && !callback) callback = mode;
			i18nOptions = _.extend(mode == 'mobile' ? mobileOptions : desktopOptions, i18nOptions);
			i18n.init(i18nOptions, callback);
			variableRegex = getVariableRegex(i18n);
			isInitialized = true;
		}
	};
	
	return {
		initialize: initialize
	};
});