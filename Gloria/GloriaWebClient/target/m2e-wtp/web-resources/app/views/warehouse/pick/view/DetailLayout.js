define(['app',
        'jquery',
        'underscore',
        'handlebars', 
        'marionette',
        'hbs!views/warehouse/pick/view/detailLayout'], 
function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
    Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.DetailLayout = Marionette.LayoutView.extend({
            
            regions: {
            	button : '#button',
				grid : '#grid'
            },

            initialize: function(options) {
                this.listenTo(Gloria.WarehouseApp, 'PickListDetailsGrid:select', this.gridClickHandler);
            },
            
            gridClickHandler : function(selectedModels) {
                this.selectedModels = selectedModels;
                Gloria.WarehouseApp.trigger('Pick:MaterialLineModel:Print', _.first(selectedModels));
                if (selectedModels && selectedModels.length === 1) {
                    this.$('#printPullLabelPart').closest('li').removeClass('disabled');
                } else {
                    this.$('#printPullLabelPart').closest('li').addClass('disabled');
                }
            },
            
            render: function() {
                this.$el.html(compiledTemplate());
                return this;
            }
			
        });
    });
    
    return Gloria.WarehouseApp.View.DetailLayout;    
});