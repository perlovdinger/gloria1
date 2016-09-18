define(['jquery', 'utils/DateHelper', 'i18next', 'libs/jquery-validation/jquery.validate'], function($, DateHelper, i18n, jqValidate) {
	/**
     * Initialize jQuery validation
     */
    var initializeJqueryValidation = function(dateHelper) {
        $.validator.addMethod("gloriaDate", 
            function(value, element) {
                return dateHelper.isValidDate(value);
            }, 
            i18n.t('Gloria.i18n.errors.invalidDate')
        );
    };
    
    initializeJqueryValidation(DateHelper);
});