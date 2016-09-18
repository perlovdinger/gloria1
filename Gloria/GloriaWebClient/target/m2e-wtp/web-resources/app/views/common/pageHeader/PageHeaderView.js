define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'utils/UserHelper',
        'hbs!views/common/pageHeader/pageHeader'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, UserHelper, compiledTemplate) {

        PageHeaderView = Marionette.LayoutView.extend({
            
            initialize: function(options) {
                options || (options = {}); 
                this.user = UserHelper.getInstance().getUser();
                this.moduleName = options.moduleName || 'Gloria.i18n.home';
                this.listenTo(Gloria, 'user:warehouses:loaded', this.showWarehouseSelector);
            },
            
            className: 'page-header',
    
            render: function() {
                this.$el.html(compiledTemplate({
                    moduleName: Gloria[this.moduleName].title,
                    now : Date.now()
                }));
                return this;
            },
            
            events: {
                'click #about' : 'handleAbout',
                'click #logoutButton' : 'hanldeLogout'
            },
            
            regions: {
                warehouseSelectorRegion: '#warehouseSelectorRegion'
            },
            
            handleAbout: function(e) {
                Gloria.trigger('showAbout');
                return false;
            },
            
            hanldeLogout: function(e) {
                e.preventDefault();
                Gloria.trigger('logout');
            },
            
            onShow: function() {                
                UserHelper.getInstance().getUserWarehouses();                 
            },
            
            showWarehouseSelector: function(warehouseCollection) {
                if(!UserHelper.getInstance().getUser()) return;
                Gloria.trigger('showSettings');
                var that = this;
                require(['views/common/warehouseSelector/WarehouseSelectorView'], function(WarehouseSelectorView) {
                    var warehouseSelectorView = new WarehouseSelectorView({
                        collection : warehouseCollection
                    });
                    that.warehouseSelectorRegion.show(warehouseSelectorView);
                });
            }            
        }); 

    return PageHeaderView;
});
