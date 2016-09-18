/**
 * An extended date cell for formatter
 */
define([ 'backgrid', 'utils/DateHelper' ], function(BackgridRef, DateHelper) {

	var DateFormatter = function() {
		Backgrid.DatetimeFormatter.call(this);
	};
	DateFormatter.prototype = Object.create(Backgrid.DatetimeFormatter.prototype);
	DateFormatter.prototype.fromRaw = function(date) {
		if (date) {
			return DateHelper.formatDate(date);
		} else {
			return '';
		}
	};
	
	DateFormatter.prototype.toRaw = function(date) {
        if (date) {
            return DateHelper.parseDate(date);
        } else {
            return '';
        }
    };

	return DateFormatter;
});