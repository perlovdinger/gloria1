define(['app',
       'jquery',
       'i18next',
       'underscore',
       'handlebars',
       'marionette',
       'models/LDAPUserDTOModel',
       'hbs!views/materialrequest/details/view/contact-person-user-id'
   ], function(Gloria, $, i18n, _, Handlebars, Marionette, LDAPUserDTOModel, compiledTemplate) {
    
    // This method will be called whenever the validation is false. 
    var triggerErrors = function (errorList) {
        Gloria.trigger('showAppMessageView', {
            type : 'error',
            title : i18n.t('errormessages:general.title'),
            message : errorList
        });
    };
    
        Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
                   
           View.ContactPersonUserIdView = Marionette.LayoutView.extend({
               
               events: {                   
                   'change #contactPersonUserId': 'userLookup'
               },               
                              
               userLookup: function(e) {
                   e && e.preventDefault();                   
                   var userId = this.$('#contactPersonUserId').val();
                   if(userId && userId.length === 7) {
                       this.LDAPUserDTOModel.set('userId', userId);
                       this.LDAPUserDTOModel.fetch({async: false}).then(_.bind(function() {
                           Gloria.MaterialRequestApp.trigger('load:LDAPUserDTOModel', true, this.LDAPUserDTOModel);
                           this.$('#contactPersonUserId').data('isvalid', true);
                       }, this), _.bind(function() {
                           Gloria.MaterialRequestApp.trigger('load:LDAPUserDTOModel', false, this.LDAPUserDTOModel);
                           this.$('#contactPersonUserId').data('isvalid', false);
                           triggerErrors([{
                               element: this.$el,
                               message: i18n.t('errormessages:errors.GLO_ERR_064')
                           }]);                        
                       }, this));
                   } else if(userId.length === 0) {
                       // Reset the related text fields, set data-isvalid to true
                       Gloria.MaterialRequestApp.trigger('load:LDAPUserDTOModel', false, this.LDAPUserDTOModel);
                       this.$('#contactPersonUserId').data('isvalid', true);
                   }
               },
                       
               initialize: function(options) {                    
                   options || (options = {});                   
                   this.contactPersonUserId = options.contactPersonUserId;
                   this.htmlName = options.htmlName || 'contactPersonUserId';
                   this.LDAPUserDTOModel = new LDAPUserDTOModel({
                       userId: this.contactPersonUserId
                   });
               },
                       
               render: function() {
                    this.$el.html(compiledTemplate({
                        htmlName: this.htmlName
                    }));                                        
                    return this;
               },
               
               onShow: function() {
                   if(this.contactPersonUserId) {
                       this.$('input#contactPersonUserId').val(this.contactPersonUserId);                       
                   }
               }
           });
       });
       
       return Gloria.MaterialRequestApp.View.ContactPersonUserIdView;
});