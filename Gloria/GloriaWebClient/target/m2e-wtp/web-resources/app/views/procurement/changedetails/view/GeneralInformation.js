define(['app',
        'jquery',
        'underscore',
        'handlebars', 
        'marionette',
        'hbs!views/procurement/changedetails/view/general-information'
], function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
    Gloria.module('ProcurementApp.ChangeDetails.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.GeneralInformationView = Marionette.View.extend({
            
            template: compiledTemplate,

            render : function() {
                this.$el.html(this.template({
                    data: this.model ? this.model.toJSON() : {},
                    status: this.model && this.model.hasStatus() ? 'Gloria.i18n.procurement.changeDetails.changeIDStates.' + this.model.get('status').toUpperCase() : ''
                }));
                return this;
            }
        });
    });
    
    return Gloria.ProcurementApp.ChangeDetails.View.GeneralInformationView;
});