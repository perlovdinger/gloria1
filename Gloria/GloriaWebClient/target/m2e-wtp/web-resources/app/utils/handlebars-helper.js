/**
 * This module contains functions to be used by handlebarsHelper's
 * It also contains handlebarsHelper's that CAN BE REUSED in different parts of the GUI
 * 
 * NOTE: 
 * - ALWAYS put handlers in the smallest possible scope! Do not register global handlers for handlers that are only in a sub application or even one view!
 * - Important to lazy load dependencies - this helper is referenced at many places
 */
define(['jquery',
        'underscore',
        'handlebars',
        'utils/DateHelper',
        'i18next',
        'utils/UserHelper'
], function ($, _, Handlebars, DateHelper, i18n, UserHelper) {
    
	var now = function() {
		return date(new Date());
	};
	
    var date = function(dateString, dateFormatKey) {
        var dateFormat = 'Gloria.i18n.dateformat'; // Default
        if (typeof(dateFormatKey) === 'string' || dateFormatKey instanceof String) {
            dateFormat = dateFormatKey;
        }
        dateFormat = i18n.t(dateFormat);
        if(dateString) {
            return DateHelper.formatDate(dateString, dateFormat);
        } else {
            return dateString;
        }
    };
    
    /* this is Currently not needed as the */
    var dateLocale = function(dateString, dateFormatKey) {
        var dateFormat = 'Gloria.i18n.dateformat'; // Default
        if (typeof(dateFormatKey) === 'string' || dateFormatKey instanceof String) {
            dateFormat = dateFormatKey;
        }
        dateFormat = i18n.t(dateFormat);
        if(dateString) {
            return DateHelper.formatDatewithoutUTC(dateString, dateFormat);
        } else {
            return dateString;
        }
    };
    
    var dateTime = function(dateString, dateTimeFormatKey) {
    	return DateHelper.formatDateTime(dateString, dateTimeFormatKey);        
    };

    var translateAsSafeString = function(i18n_key) {
        var result = translate(i18n_key);
        return new Handlebars.SafeString(result);
    };
    
    var translate = function(i18n_key, context) {
        context = _.defaults(context || {}, {defaultValue: ' '});
        return i18n.t(i18n_key, context);        
    };
    
    var renderDropDown = function(options, selected, translateRequired) {
        var i, ret = '';
        for (i = 0; i < options.length; i++) {
            $.each(options[i], function(key, value) {
                var translatedValue = translateRequired ? translateAsSafeString(value) : value;
                var selectedVal = (selected == key)? 'selected' : '';
                ret += "<option value='" + key + "' " + selectedVal +">" + translatedValue + "</option>";
            });
        }
        return ret;
    };
    
    var renderDropDownForId = function(options) {
        var ret = '';
           _.each(options,function(item){
        	   ret += "<option value='"+item +"'>"+ item +"</option>";
           });
        return ret;
    };
    
    var renderDropDownMenuButton = function(options, selected, translateRequired) {
        var i, ret = '';
        for (i = 0; i < options.length; i++) {
            $.each(options[i], function(key, value) {
                var translatedValue = translateRequired ? translateAsSafeString(value) : value;
                ret += "<li><a href='#' value='"+translatedValue+"' id='" + key + "'>" + translatedValue + "</a></li> ";
            });
        }
        return ret;
    };
    
    var renderFlagDropDown = function(flagList) {
        var ret = '';
        _.each(_.keys(flagList),function(key) {
        	var flag = flagList[key];
        	var translatedValue = translateAsSafeString(flag.keyTranslate);
        	  ret+= "<li role=\"presentation\" id=\""+key+"\" class=\"flagClass\"><a role=\"menuitem\" href=\"#\" onclick=\"return false\" " +
      		"style=\"text-align: left;\"><i class=\"fa "+flag.className+" font-border></i>\""+translatedValue+"\"</a> </li>";
        	});

        return ret;
    };
    
    var renderFlagStatus = function(flagList, flagStatus) {
        var ret = '';
        if(flagStatus) {
	        _.each(_.keys(flagList),function(key) {
			        	if(key == flagStatus) {
			            	var x = flagList[key];
			            	var translatedValue = translateAsSafeString(x.keyTranslate);
			            	if(x.noBorder) {
			            		ret+= "<i id=\"flagSelect\" class=\"fa "+x.className+"\"></i><span id=\"flagSelectText\">&nbsp;"+translatedValue+"</span>";
			            		} else {
			            			ret+= "<i id=\"flagSelect\" class=\"fa "+x.className+" font-border\"></i><span id=\"flagSelectText\">&nbsp;"+translatedValue+"</span>";
			            		}
			        	}
	        	});
        } else {
        	var translatedValue = translateAsSafeString("Gloria.i18n.deliverycontrol.orderOverview.text.selectFlag");
    		ret = "<i id=\"flagSelect\" class=\"\"></i><span id=\"flagSelectText\">&nbsp;"+translatedValue+"</span>";
        }
        return ret;
    };
    
    /**
     * Takes an option list and a key and shows rthe value for the key
     */
    var showDropdownValue = function(options, selected, translateRequired) {
        var val = "";
        for (var i = 0; i < options.length; i++) {
            $.each(options[i], function(key, value) {
                if (key === selected) {
                    val = translateRequired ? translateAsSafeString(value) : value;
                    return false; // Breaks the loop
                }
            });
        }
        return val;
    };
    
    var checkIfEqual = function(lvalue, rvalue, options) {
        if( lvalue != rvalue ) {
            return options.inverse(this);
        } else {
            return options.fn(this);
        }
    };  
    
    var showUserInfo = function(attr) {
        var user = UserHelper.getInstance().getUser();
        return user ? user[attr] : '';
    };
    
    /**     
     * whenever we use this handlebar helper. In other cases it does not work since 
     * it contains some asynchronous function but as of now handlebars helpers does not support async.
     */    
    var hasPermission = function() {        
        var action = _.initial(arguments);
        var block = _.last(arguments);
        var moduleNames = block.hash;
                            
        if(UserHelper.getInstance().hasPermission(action, moduleNames)) {
            return block.fn(this);
        } else {
            return block.inverse(this);
        }       
    };
        
    var placeholder = function() {
        var result = i18n.t('Gloria.i18n.placeholder');
        return result;
    };
    
    var mathFn = function(lvalue, operator, rvalue, options) {
        lvalue = parseFloat(lvalue);
        rvalue = parseFloat(rvalue);
        return {
            "+": lvalue + rvalue,
            "-": lvalue - rvalue,
            "*": lvalue * rvalue,
            "/": lvalue / rvalue,
            "%": lvalue % rvalue
        }[operator];
    };
    
    var showNAIfNoValue = function(val) {
		if(!val) {
			return translate('Gloria.i18n.general.na');
		}
		return val;
	};
	
	var concat = function(lval, rval, options) {
		return (lval || '') + (rval || '') + ''; 
	};
	
	var obj = function(options) {
		options || (options = {});
		var hash = options.hash || {};
		_.each(hash, function(value, key) {
			if(!value) {
				hash[key] = '';
			}
		}, this);
		return hash;
	};
	
	var isDateInThePast = function (inputDate,options){
	    if(DateHelper.isDateInThePast(date(inputDate))){
	        return options.inverse(this);
	    } else {
	        return options.fn(this);
	    }
	        
	       
	};
    
    var initialize = function() {
        Handlebars.registerHelper('t', translate);                
        Handlebars.registerHelper('renderDropDown', renderDropDown);
        Handlebars.registerHelper('renderDropDownValue', showDropdownValue);
        Handlebars.registerHelper('date', date);
        Handlebars.registerHelper('now', now);
        Handlebars.registerHelper('dateTime', dateTime);
        Handlebars.registerHelper('if_eq', checkIfEqual);
        Handlebars.registerHelper('showUserInfo', showUserInfo);
        Handlebars.registerHelper('hasPermission', hasPermission);
        Handlebars.registerHelper('placeholder', placeholder);
        Handlebars.registerHelper('math', mathFn);
        Handlebars.registerHelper('showNAIfNoValue', showNAIfNoValue);
        Handlebars.registerHelper('concat', concat);
        Handlebars.registerHelper('obj', obj);
        Handlebars.registerHelper('isDateInThePast', isDateInThePast);
        Handlebars.registerHelper('dateLocale', dateLocale);
        Handlebars.registerHelper('renderDropDownMenuButton', renderDropDownMenuButton);
        Handlebars.registerHelper('renderDropDownForId', renderDropDownForId);
        /*Handlebars.registerHelper('renderFlagDropDown', renderFlagDropDown);
        Handlebars.registerHelper('renderFlagStatus', renderFlagStatus);*/
    };

    return {
        initialize : initialize
    };
});