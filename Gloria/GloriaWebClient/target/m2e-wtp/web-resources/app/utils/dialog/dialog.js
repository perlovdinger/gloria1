/**
 * @module This module is about making a facade to create a dialog box 
 * using Backbone Marionette view, JQuery and Bootstrap. 
 */
define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars', 
        'marionette',
        'hbs!utils/dialog/template'], 
        function(Gloria, $, i18n, _, Handlebars, Marionette, Template) {
        
        /**
         * @property attrs: Possible arguments to pass to this view.
         */
        var attrs = ['title', 'message', 'buttons', 'options'];        
        var Dialog = Marionette.LayoutView.extend({
            //The default value to show as title
            title: i18n.t('components:dialog.defaults.title'),
            //The default buttons             
            buttons: {
                ok: {
                    label: i18n.t('components:dialog.defaults.ok'),
                    className: "btn-primary",
                    callback: function(e) {
                        e.preventDefault();                   
                        return true;
                    }
                },
                cancel: {
                    label: i18n.t('components:dialog.defaults.cancel'),
                    className: "btn-default",
                    callback: function(e) {
                        e.preventDefault();
                        return false;
                    }
                }
            },
            
            events: {},
            
            ui: {
              dialog: '.modal',
              header: '.modal-header',
              title: '.modal-title',
              message: '.modal-body',
              footer: '.modal-footer'
            },
            
            triggers: {
                'hidden.bs.modal .modal': 'hidden'
            },
            
            initialize: function(options) {
                options || (options = {});
                _.extend(this, _.pick(options, attrs));
                this.attachButtons(this.buttons);
            },
            /**
             * @method This method get the buttons as param and add 'Click' event handler 
             * to them by using their 'callback' function. It also wrap the 'callback' function
             * and after calling it, triggers an event using the button name.
             */
            attachButtons: function(buttons) {
                this.undelegateEvents();
                _.each(buttons, function(value, key){                    
                    var that = this;
                    this.events['click #' + key] = function(e) {                            
                            if(value.callback) value.callback.call(that, e);                        
                            that.trigger(key, e);
                            that.trigger('hidden');
                        };
                }, this);                
                this.delegateEvents();
            },
            
            render: function() {               
                this.$el.html(Template({                    
                    dialog: this,
                    buttons: _.map(this.buttons, function(value, key) {
                         var val = _.clone(value);   
                         val['id'] = key;
                         return val;
                    })
                    }));
                  
                this.bindUIElements();
                this.ui.dialog.modal(this.options);
                return this;
            },
            
            onDestroy: function() {
                this.$el.off('.modal'); 
            }
        });
        
        /**
         * @public The public interface to expose to other modules
         */
        return {
            /**
             * @method This method intantiates a dialog and make it visible on the screen.
             */
            show: function(options) {
                if(options) this.options = options;
                this.dialog = new Dialog(options);
                this.dialog.on('hidden', this.hide, this);
                Gloria.basicModalLayout.content.show(this.dialog);
                return this;
            },
            /**
             * @method This method hides a dialog currenly is shown on screen and dispose it.
             */
            hide: function() {
                if(this.dialog) {
                    this.dialog.off();
                    this.dialog.ui.dialog.modal('hide');                    
                    Gloria.basicModalLayout.content.reset();
                    this.dialog = null;
                }
            }
        };
       
});