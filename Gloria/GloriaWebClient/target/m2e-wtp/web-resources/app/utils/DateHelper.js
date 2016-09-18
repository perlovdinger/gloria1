/**
 * Helper functions to handle date strings in Gloria
 * 
 * NOTE: dateFormat needs to be picked up at every function call, because it may be changed during runtime
 */

define(['moment',
        'i18next'
], function (moment, i18n) {


/**********************************************************************************************************************************/
	
	/*isValidDate() ,isValidDateWithTZ() : Used to check if the given date is to check if input is of valid date format
	 * - Returns Boolean value
	 *  - isValidDateWithTZ() is redundant , but ... */		
	
	var isValidDate = function(dateString){				
		var dateFormat = i18n.t('Gloria.i18n.dateformat');
		if (dateString) {
			return moment.utc(dateString, dateFormat, true).isValid();
		} else {
			return false;
		}
	};
	
	var isValidDateWithTZ = function(dateString){				
		var dateFormat = i18n.t('Gloria.i18n.dateformat');
		if (dateString) {
			return moment(dateString, dateFormat, true).isValid();
		} else {
			return false;
		}
	};

/**********************************************************************************************************************************/
	
	/* formatDate : Converts in to UTC for given dateformat
	 *  formatDatewithDefault : will apply Locale specific Value given dateformat
	 *  INPUT : milisec
	 *  */		
	
	var formatDate = function(date, dateFormatKey){				
		dateFormatKey = dateFormatKey || i18n.t('Gloria.i18n.dateformat');
		if (date) {
			return moment.utc(date).format(dateFormatKey);
		} else {
			return date;
		}
	};
	
	/*this method is needed to do Locale specific date validation in the GUI
	 * Ex: Current date can be 2nd in India , 1st in Sweden , 30th in Huwawei . To enable/disable client specific calender  */
	
	/*Handlebar helper to display Locale specific date in html , if needed */
	
	var formatDatewithoutUTC = function(date, dateFormatKey){				
		dateFormatKey = dateFormatKey || i18n.t('Gloria.i18n.dateformat');
		if (date) {
			return moment(date).format(dateFormatKey);
		} else {
			return date;
		}
	};

		/* defalut format with Locale applied*/
	var formatDatewithDefault = function(date){				
		if (date) {
			return moment(date).format();
		} else {
			return date;
		}
	};
	
	//TODO ::

/**********************************************************************************************************************************/
	/* will convert date to Milisec*/
	
	var valueOf = function(date){				
		if (date) {
			return moment(date).valueOf();
		} else {
			return date;
		}
	};
	
	var valueOfWithTZ = function(date){				
		if (date) {
			return moment.utc(date).valueOf();
		} else {
			return date;
		}
	};


/**********************************************************************************************************************************/
	/*Format date with Time*/

	var formatDateTime = function(dateString, dateTimeFormatKey) {
        var dateTimeFormat = 'Gloria.i18n.datetimeformat'; // Default
        dateString = moment.utc(dateString).valueOf();
        if (typeof(dateTimeFormatKey) === 'string' || dateTimeFormatKey instanceof String) {
            dateTimeFormat = dateTimeFormatKey;
        }
        dateTimeFormat = i18n.t(dateTimeFormat);
        if(dateString) {
            return moment(dateString).format(dateTimeFormat);
        } else {
            return dateString;
        }
    };
    
	var formatDateTimewithUTC = function(dateString, dateTimeFormatKey) {
        var dateTimeFormat = 'Gloria.i18n.datetimeformat'; // Default
        if (typeof(dateTimeFormatKey) === 'string' || dateTimeFormatKey instanceof String) {
            dateTimeFormat = dateTimeFormatKey;
        }
        dateTimeFormat = i18n.t(dateTimeFormat);
        if(dateString) {
            return moment.utc(dateString).format(dateTimeFormat);
        } else {
            return dateString;
        }
    };

    
/**********************************************************************************************************************************/
    
    
	/**
	 * Parses a date from a localized string value to backbone format
	 * 
	 * NOTE: Only Date is handled. If we later need a function for dateTime, this needs
	 * to be implemented separately! /Daniel
	 * 
	 * @param dateString Date to parse
	 * @dateOnly Set to true to only return date part of the datetime string
	 * 
	 * @returns A date object
	 */
	var parseDate = function(dateString, dateOnly){				
		var dateFormat = i18n.t('Gloria.i18n.dateformat');
		var emptyString = "";
		if (dateString) {
			var isoString = moment.utc(dateString, dateFormat).toISOString();
			if (dateOnly) {
				return isoString.split('T')[0];
			} else {
				return isoString;
			}
		} else {
			return emptyString;
		}
	};
	
	/*not used currnetly*/
	var parseDatewithoutUTC = function(dateString, dateOnly){				
		var dateFormat = i18n.t('Gloria.i18n.dateformat');
		var emptyString = "";
		if (dateString) {
			var isoString = moment(dateString, dateFormat).toISOString();
			if (dateOnly) {
				return isoString.split('T')[0];
			} else {
				return isoString;
			}
		} else {
			return emptyString;
		}
	};

/**********************************************************************************************************************************/
	/* Utilitiy methods*/
	
	var isDateInThePast = function(date) {
	    var isPreviousDate = true;
	    var selectedDate = new Date(parseDatewithoutUTC(date));
        var currentDate = new Date();
        //Skips time
        if( currentDate.setHours(0, 0, 0, 0) > selectedDate.setHours(0, 0, 0, 0) ){
            isPreviousDate = false;
          }
        return isPreviousDate;
	};
	
	var isDateInTheFuture = function(date) {
	    var isFutureDate = false;
	    var selectedDate = new Date(parseDatewithoutUTC(date));
        var currentDate = new Date();
        //Skips time
        if( currentDate.setHours(0, 0, 0, 0) < selectedDate.setHours(0, 0, 0, 0) ){
            isFutureDate = true;
          }
        return isFutureDate;
	};
	
	var getConvertedSystemDate = function(date) {
		var offset = -1*(moment.parseZone(selectedDate).utcOffset());
		var selectedDate = moment(new Date(parseDatewithoutUTC(date)));
		selectedDate.add(offset,'minutes');
        return selectedDate._d; // TODO: this needs refactoring
	};
	
	var getCurrentLocalizedDate = function () {
		/*var nowDate = new Date();
		var today = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate(), 0, 0, 0, 0);*/
		var dateFormat = i18n.t('Gloria.i18n.dateformat');
		return moment().format(dateFormat);
	};
	
	var getNextLocalizedDate = function () {
		var dateFormat = i18n.t('Gloria.i18n.dateformat');
		return moment().add(24, 'hours').format(dateFormat);
	};
	
/**********************************************************************************************************************************/	

	// Exposed public methods goes here
    return {
    	isValidDate : isValidDate,
    	formatDate : formatDate,
    	formatDateTime : formatDateTime,
    	parseDate : parseDate,
    	isDateInThePast :isDateInThePast,
    	isDateInTheFuture : isDateInTheFuture,
    	formatDatewithoutUTC : formatDatewithoutUTC,
    	formatDateTimewithUTC : formatDateTimewithUTC,
    	formatDatewithDefault :formatDatewithDefault,
    	parseDatewithoutUTC : parseDatewithoutUTC,
    	getConvertedSystemDate :getConvertedSystemDate,
    	valueOf: valueOf,
    	getCurrentLocalizedDate : getCurrentLocalizedDate,
    	getNextLocalizedDate :getNextLocalizedDate
    };

});


