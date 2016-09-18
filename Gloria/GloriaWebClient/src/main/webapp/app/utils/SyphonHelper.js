/**
 * This module contains standard InputReaders and OutputWriters for Syphon.
 * 
 * NOTE: Readers and writers that are specific for one view shall be put in that
 * view!
 */
define([ 'backbone.syphon', 'utils/DateHelper' ], function(Syphon, DateHelper) {
	
	var textReader = function($el) {
		if (!$el.val()) {
			return $el.val();
			//return $el.val() || null;
		}
		var value = $el.val();
		if ($el.hasClass('js-float')) {
			value = value.replace(',', '.');
			if (value && !isNaN(value)) {
				return parseFloat(value);
			} else {
				return value;
			}
		} else if ($el.hasClass('js-date')) {
			if (DateHelper.isValidDate(value)) {
				return DateHelper.parseDate(value);
			} else {
				return value;
			}
		} else if($el && $el.attr('type') && $el.attr('type').toLowerCase() == 'number') {
		    if (!isNaN(value)) {
		        return new Number(value).valueOf();
            } else {
                return value;
            }		    
        } else if ($el.hasClass('js-list')) {
			if (value) {
				return value.split(',');
			} else {
				return value;
			}
		} else {		    
			return value;
		}
	};
	
	var selectReader = function($el){
	  var value = $el.val();
	  
	  if($el.hasClass('js-number')) {
		  value = new Number(value).valueOf();
	  }

	  return value;
	};

	var initialize = function() {
		Backbone.Syphon.InputReaders.register('text', textReader);
		Backbone.Syphon.InputReaders.register('number', textReader);	
		Backbone.Syphon.InputReaders.register("select", selectReader);
	};

	return {
		initialize : initialize
	};
});