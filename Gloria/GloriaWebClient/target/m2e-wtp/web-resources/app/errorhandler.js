define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next',
        'utils/DateHelper',
        'utils/UserHelper',
        'views/GloriaConstants'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, DateHelper, UserHelper, GloriaConstants) {
	
	var replaceErrorCodeParam = function(text, errorCodeParam) {
		for(var key in errorCodeParam) {
			var replacer = GloriaConstants.I18_VALUE_REPLACER_LEFT + key + GloriaConstants.I18_VALUE_REPLACER_RIGHT;
			var regEx = new RegExp(replacer, 'g');
			text = text.replace(regEx, errorCodeParam[key]);
		}
		return text;
	};
	
	var isLocalEnvironment = function() {
		return window.location.href.indexOf('localhost') > -1;
	};

	return {
		handleGlobalErrors :  function() {
	        $(document).ajaxError(function(event, jqxhr, settings, exception) {
	        	
	        	// Netwrork is required for TEST, QA, PROD but probably not for local as server is in LOCAL m/c.
				/**if (!isLocalEnvironment() && !navigator.onLine) {
					Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			message : new Array({
		    				message : '[No Network Available] ' + i18n.t('Gloria.i18n.processFailed')
		    			})
		    		});
					return;
				}*/
	        	
                var exceptionMessage = i18n.t('Gloria.i18n.exception.noErrorMessage'); // Default
                var exceptionTimeStamp = new Date().toString(); // Default
	            
				switch (jqxhr.status) {
				// Handle 401 - Unauthorized Error
				case 401:
					try {
						var responseErrorObj = $.parseJSON(jqxhr.responseText);
						exceptionMessage = responseErrorObj.message;
						exceptionTimeStamp = responseErrorObj.timestamp;
                    } catch (e) {
                    	exceptionMessage = i18n.t('Gloria.i18n.errors.GLO_ERR_58');
                    	exceptionTimeStamp = DateHelper.formatDate(Date.now());
                        console.log("JSON Parsing error occurred!");
                    }
                    UserHelper.getInstance().resetUser();
					Gloria.trigger('401');
				break;
				// Handle 400 - Bad Request Error
				case 400:
					try {
						$.each($.parseJSON(jqxhr.responseText).validationErrors, function(i, item) {
							var errorMessage = new Array();
							$.each(item.errors,	function(j,	error) {
								if(error.errorCode == 'GLO_ERR_101') return;
								var item = {
										code: error.errorCode,
										element : error.attribute ? '#' + error.attribute : '',
										message : (error.errorCode == "null") ? (error.errorMessage): (replaceErrorCodeParam(i18n.t('Gloria.i18n.errors.' + error.errorCode), error.errorCodeParam))
									};
								errorMessage.push(item);
							});
							if(settings.validationError) {
								settings.validationError(errorMessage, item.errors);
							} else if(settings.error) {
								settings.error(errorMessage, item.errors);
							}
						});
					} catch (e) {
						console.log("JSON Parsing error occurred!");
					}
					break;
				// Handle 500 - Internal Server Error
				case 500:
					try {
						var responseErrorObj = $.parseJSON(jqxhr.responseMsg || jqxhr.responseText);
						exceptionMessage = responseErrorObj.message;
						exceptionTimeStamp = responseErrorObj.timestamp;
						console.log('Server Error: [Time: ' + exceptionTimeStamp + '], [Error Message: ' + exceptionMessage + ']');
                    } catch (e) {
                        console.log("JSON Parsing error occurred!");
                    }
                    Gloria.trigger('showAppMessageView', {
		    			type : 'error',
		    			stick : settings.type == 'GET' ? true : false, // Message block will be there even if there is any other server calls
		    						  // can be closed by clicking close btn or when Backbone.history changes - Applicable only when type is GET
		    			message : new Array({
		    				message :i18n.t('Gloria.i18n.processFailed') //can include code saying its 500 error.
		    			})
		    		});
					break;
				}
	        });
	    }
	};
});
