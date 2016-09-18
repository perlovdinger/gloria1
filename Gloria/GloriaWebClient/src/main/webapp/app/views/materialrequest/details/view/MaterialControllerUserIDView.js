define(['app',
       'jquery',
       'i18next',
       'underscore',
       'handlebars',
       'marionette',
       'models/LDAPUserDTOModel',
       'utils/UserHelper',
       'hbs!views/materialrequest/details/view/material-controller-user-id'
   ], function(Gloria, $, i18n, _, Handlebars, Marionette, LDAPUserDTOModel, UserHelper, compiledTemplate) {
    
    // This method will be called whenever the validation is false. 
    var triggerErrors = function (errorList) {
        Gloria.trigger('showAppMessageView', {
            type : 'error',
            title : i18n.t('errormessages:general.title'),
            message : errorList
        });
    };
    
        Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
                   
           View.materialControllerUserIdView = Marionette.LayoutView.extend({
               
               events: {                   
                   'change #materialControllerUserId': 'userLookup'
               },               
                              
               userLookup: function(e) {
                   e && e.preventDefault();                   
                   var userId = this.$('#materialControllerUserId').val();
                   if(userId && userId.length === 7) {
                       this.LDAPUserDTOModel.set('userId', userId);
                       this.LDAPUserDTOModel.fetch({async: false}).then(_.bind(function() {
                           Gloria.MaterialRequestApp.trigger('load:LDAPUserDTOModel:MaterialController', true, this.LDAPUserDTOModel);
                           this.$('#materialControllerUserId').data('isvalid', true);
                       }, this), _.bind(function() {
                           Gloria.MaterialRequestApp.trigger('load:LDAPUserDTOModel:MaterialController', false, this.LDAPUserDTOModel);
                           this.$('#materialControllerUserId').data('isvalid', false);
                           triggerErrors([{
                               element: this.$el,
                               message: i18n.t('errormessages:errors.GLO_ERR_078')
                           }]);                        
                       }, this));
                   } else if(userId.length === 0) {
                       // Reset the related text fields, set data-isvalid to true
                       Gloria.MaterialRequestApp.trigger('load:LDAPUserDTOModel:MaterialController', false, this.LDAPUserDTOModel);
                       this.$('#materialControllerUserId').data('isvalid', true);
                   }
               },
                       
               initialize: function(options) {                    
                   options || (options = {});                   
                   this.materialControllerUserId = options.materialControllerUserId || UserHelper.getInstance().getUserId();
                   this.htmlName = options.htmlName || 'materialControllerUserId';
                   this.LDAPUserDTOModel = new LDAPUserDTOModel({
                       userId: this.materialControllerUserId
                   });
               },
                       
               render: function() {
                    this.$el.html(compiledTemplate({
                        htmlName: this.htmlName
                    }));                                        
                    return this;
               },
               
               onShow: function() {
                   if(this.materialControllerUserId) {
                       this.$('input#materialControllerUserId').val(this.materialControllerUserId);                       
                   }
                   this.$('#materialControllerUserId').trigger('change'); // had to trigger manually to fetch MC name 
               }
           });
       });
       
       return Gloria.MaterialRequestApp.View.materialControllerUserIdView;
});