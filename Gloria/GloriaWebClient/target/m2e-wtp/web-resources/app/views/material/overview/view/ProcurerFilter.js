define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'utils/UserHelper',
        'utils/backbone/GloriaCollection',
        'hbs!views/material/overview/view/procurer-filter' 
], function(Gloria, $, _, Handlebars, Backbone, Marionette, UserHelper, GloriaCollection, Template) {
        
    Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.ProcurerFilter = Marionette.View.extend({
            
            tagName: 'span',
            
            template: Template,
            
            events : {
                'change select[id^="procurerFilter"]' : 'filterProcurer'
            },
            
            filterProcurer: _.debounce(function(e) {
                var selectList = this.$('select[id^="procurerFilter"]');
                var value = selectList.val() || null;
                var filterType = selectList.find(':selected').data('filter') || null;
                Gloria.MaterialApp.trigger('filter:materialRequestList', filterType, value);
            }, 500),
            
            initialize: function(options) {
                this.listenTo(this.collection, 'sync', this.update);              
                this.fetchTeams();
            },
            
            fetchTeams: function() {
                var that = this;
                this.col = new GloriaCollection();
                this.col.url = '/user/v1/users/'+ UserHelper.getInstance().getUserId() +'/teams';
                var thisType = function() {
                    if(UserHelper.getInstance().hasUserRole('IT_SUPPORT')) {
                        return 'MATERIAL_CONTROL';
                    }  
                	if(UserHelper.getInstance().hasUserRole('PROCURE')) {
                		return 'MATERIAL_CONTROL';
                	}
                	if(UserHelper.getInstance().hasUserRole('DELIVERY')) {
                		return 'DELIVERY_CONTROL';
                	}
                	return null; // WH_DEFAULT and other roles who have access to this module.
				}();
                this.col.fetch({
                    data: {
                    	type: thisType
                    },                    
                    success: function(collection) {
                        that.render();                        
                    }
                });
            },
            
            render: function() {                
               this.$el.html(this.template({
                   cid: this.cid,
                   teams: this.col.toJSON()
               }));                
               return this;
            },
            
            update : function(e) {
				var filterValue = null;
				if (this.collection.queryParams['procureTeam']) { // List for the selected team
					filterValue = this.collection.queryParams['procureTeam'];
				} else if (this.collection.queryParams['procureUserId']) { // List for the current user
					filterValue = 'user';
				} else {
					filterValue = 'all'; // List all
				}
				this.$('select[id^="procurerFilter"]').val(filterValue);
			}
        });
    });
    
    return Gloria.MaterialApp.View.ProcurerFilter;
});