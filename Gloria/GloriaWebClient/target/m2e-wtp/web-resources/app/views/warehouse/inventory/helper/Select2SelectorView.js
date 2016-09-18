define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
        'bootstrap',
        'i18next',
        'select2'
], function(Gloria, $, _, Handlebars, Marionette, Bootstrap, i18n, select2) {

	var Select2SelectorView = Marionette.View.extend({

        initialize : function(options) {
            this.element = options.element;
            this.render(options.select2Data,options.select2Options);
        },
        
        render : function(select2Data, select2Options) {
            $(this.element).empty();
            $(this.element).select2({
                multiple: false,
                data : select2Data,
                minimumResultsForSearch : select2Options.minimumResultsForSearch,
                width: '100%'
            }).on('change', function(e) {
                $(this.element).val(e.val);
            });
            return this;
        }
    });

    return Select2SelectorView;
});