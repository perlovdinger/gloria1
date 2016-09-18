define(
    ['app', 'jquery', 'i18next', 'handlebars', 'underscore', 'marionette', 'bootstrap',
            'hbs!views/procurement/overview/view/assign-request', 'utils/UserHelper'], function(Gloria, $, i18n,
        Handlebars, _, Marionette, Bootstrap, compiledTemplate, UserHelper) {

    Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

        View.AssignRequestView = Marionette.LayoutView.extend({
            
            regions: {
                userInfo: '#userInfo'
            },
            
            className : 'modal',

            id : 'assignToForm',

            events : {
                'click #save' : 'handleSaveClick',
                'click #cancel' : 'handleCancelClick'
            },

            initialize : function(options) {
                this.models = options.models;
                this.teamCollection = options.teamCollection;
            },

            handleSaveClick : function(e) {
                Gloria.ProcurementApp.trigger('procureRequestAssign:done', this.models, this.collectData());
                this.$el.modal('hide');
            },

            handleCancelClick : function(e) {
                this.$el.modal('hide');
            },

            collectData : function() {
                return {
                    'assignedMaterialControllerId' : this.getAssignMaterialControllerId(),
                    'assignedMaterialControllerTeam' : this.getAssignMaterialControllerTeam()
                };
            },

            getAssignMaterialControllerId : function() {
                var val = this.$('div#userInfo select').val();                
                return val.split(';')[0];
            },
            
            getAssignMaterialControllerTeam: function() {
                var val = this.$('div#userInfo select').val();                
                return val.split(';')[1];
            },
            
            getTemplate: function() {
                return compiledTemplate;
            },

            onRender : function() {            	
                var that = this;
                
                this.$el.modal({
                    show : false
                });
                this.$el.on('hidden.bs.modal', function() {
                    that.trigger('hide');
                });                
            },

            onShow : function() {
                this.$el.modal('show');
            },

            onDestroy : function() {
                this.$el.modal('hide');
                this.$el.off('.modal');
                Gloria.ProcurementApp.off(null, null, this);
            }
        });
    });

    return Gloria.ProcurementApp.View.AssignRequestView;
});
