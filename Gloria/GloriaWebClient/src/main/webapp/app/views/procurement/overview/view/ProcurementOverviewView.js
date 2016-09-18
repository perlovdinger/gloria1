define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
		'hbs!views/procurement/overview/view/procurement-overview'
], function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ProcurementOverviewView = Marionette.LayoutView.extend({
			
			className: 'row',
			
			regions : {
				moduleInfo : '#moduleInfo',
				gridInfo : '#gridInfo',
				userInfo : '#userInfo'
			},

			events : {
				'click #procureTab a' : 'handleProcureTabClick',
				'change #userInfo select' : 'handleUserChange'
	        },
	        
	        handleProcureTabClick : function(e) {
				e.preventDefault();				
				Backbone.history.navigate('procurement/overview/' + e.currentTarget.hash.split("#")[1], {
				    trigger: true
				});
			},
			
			handleUserChange : function(e) {
				e.preventDefault();
				var value = e.currentTarget.value;
				var userId = value.split(';')[0];
				var userTeam = value.split(';')[1];
				Gloria.ProcurementApp.trigger('Procurement:user:change', userId, userTeam);
			},
	        
	        initialize : function(options) {
	        	this.module = options.module;
	        	this.hasRolePI = options.hasRolePI;
	        	this.listenTo(Gloria.ProcurementApp, 'Procurement:ChangeTab:showChangeMark', this.showChangeMark);
	        },
	        
	        showChangeMark : function() {
                $('a[href="#change"]').prepend('<i class="fa fa-exclamation color-orange aria-hidden="true"></i>&nbsp;');
            },

			render : function() {
				this.$el.html(compiledTemplate({
					isIP : this.hasRolePI
				}));
				return this;
			},
			
			onShow : function() {							
			    var tabId = this.module ? '#procureTab a[href="#' + this.module + '"]' : '#procureTab a:first';
			    this.$(tabId).tab('show');
			}
		});
	});
	
	return Gloria.ProcurementApp.View.ProcurementOverviewView;
});