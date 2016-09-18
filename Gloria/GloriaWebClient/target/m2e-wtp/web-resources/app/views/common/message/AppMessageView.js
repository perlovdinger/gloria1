/**
 * This view is responsible for showing all form validation success, error or
 * any other custom user messages.
 * 
 * @author a039911 : Added functionality to show only one global message, but high light the error fields!
 * 
 */
define([ 'app',
         'jquery',
         'underscore',
         'handlebars',
         'marionette',
         'bootstrap',
         'i18next',
         'hbs!views/common/message/app-message'
], function(Gloria, $, _, Handlebars, Marionette, BootStrap, i18n, compiledTemplate) {

	var AppMessageView = Marionette.LayoutView.extend({

		initialize : function(options) {
			if(options) {
				this.type = options.type;
				this.modal = options.modal; // Show message on page or on pop-up?
											// Default on page, modal: true to show on pop-up
				this.title = options.title;
				this.message = options.message;
				this.stick = options.stick; // Used when hiding this; true to hide no matter what, false to hide conditionally.
				this.singleMessage = options.singleMessage;
				this.localize = options.localize || false;
				this.duplicate = options.duplicate;
				this.actions = options.actions;
			}
			if(this.localize) {
			    this.translate();
			}
			this.listenTo(Gloria, 'hideAppMessageView', this.hideAppMessageView);
		},
		
		events : {
			'click .close' : 'removeMessageBlock'
		},
		
		translate: function() {
		    if(this.title) this.title = i18n.t(this.title);
		    if(this.singleMessage) this.singleMessage = i18n.t(this.singleMessage);
		    Backbone.$.each(this.message, function(index, item) {
		        item.message = i18n.t(item.message);
		    });
		},
		
		removeMessageBlock : function(e) {
			if(this.modal) {
				Backbone.$('#appModalMessage').empty();
				Backbone.$('#appModalMessage').removeAttr('style');
			} else {
				Backbone.$('#appMessage').empty();
				Backbone.$('#appMessage').removeAttr('style');
			}
		},
		
		hideAppMessageView : function(options) {
			var $container = this.modal ? $('#appModalMessage') : this.$el;
			if((options && options.unstick) || !($container.data('stick'))) { // Only remove if data-stick is false or not provided.
				try {
					this.removeMessageBlock();
					if (Gloria.basicLayout && Gloria.basicLayout.appMessage) {
					    Gloria.basicLayout.appMessage.empty();
					}
				} catch(e) {
					console.log('Unable to remove message');
				}
			}
		},
		
		fixedToTop : function() {
			var that = this;
			var top = Backbone.$('#appMessage').offset().top;			
			Backbone.$(window).on('scroll.appnotifier', function() {
				if(Backbone.$(window).scrollTop() >= top) {			
				    Backbone.$('#appMessage').css({'height': that.$el.css('height')});
					that.$el.addClass('fixed');
					that.$el.addClass('messsage-fixed-top');	
				} else {
					that.$el.removeClass('fixed');
					that.$el.removeClass('messsage-fixed-top');
				}
			});
			Backbone.$(window).on('resize.appnotifier', function() {
				that.$el.css('width', that.$el.parent().width());
			});
			Backbone.$(window).trigger('scroll');
		},

		render : function() {
			var that = this;
			var errorMessage = '<ul>';
			Backbone.$('.form-group').removeClass('has-error');
			if(this.message) {
				Backbone.$.each(this.message, function(index, item) {
					if(item && item.element) {
						that.modal ? $('#appModalRegion').find(item.element).closest('.form-group').removeClass('has-success').addClass('has-error')
							: (Backbone.$(item.element).closest('.form-group').removeClass('has-success').addClass('has-error')
									&& Backbone.$(item.element).removeClass('success').addClass('error'));
					}
					if(item && item.message && !that.singleMessage) {
						var thisMsg = '<li>' + item.message + '</li>';
						if(that.duplicate == false) {
							if(errorMessage.indexOf(thisMsg) == -1) { // No Duplicate message
								errorMessage += thisMsg;
							}
						} else {
							errorMessage += thisMsg;
						}
					}
				});
			}
			if(this.singleMessage) {
				errorMessage += '<li>' + this.singleMessage + '</li>';
			}
			errorMessage += '</ul>';
			var $container = this.modal ? $('#appModalMessage') : this.$el;
			if(this.stick) {
				$container.data('stick', this.stick); // Set data-stick
			}
			if ((this.message && this.message.length != 0) || this.singleMessage) {
				$container.html(compiledTemplate({
					type : this.type == 'error' ? 'danger' : this.type,
					title : this.title,
					text : errorMessage,
					buttons : _.map(this.actions, function(value, key) {
						var val = _.clone(value);
						val['id'] = key;
						return val;
					})
				}));
				!that.modal ? this.fixedToTop() : '';
			} else {
				$container.empty();
			}
			return this;
		},
		
		attachButtons : function(actions) {
			this.undelegateEvents();
			var that = this;
			_.each(actions, function(value, key) {
				this.events['click #' + key] = function(e) {
					value.callback && value.callback.call(that, e);
				};
			}, this);
			this.delegateEvents();
		},
		
		onShow: function() {
		    this.$el.css("width", Backbone.$('#appMessage').width());
		    this.attachButtons(this.actions);
		},
		
		onDestroy: function() {
		    Backbone.$(window).off(".appnotifier");
		    Backbone.$('#appMessage').css({'height': ''});
        }
	});

	return AppMessageView;
});