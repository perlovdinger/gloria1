define(['app',
        'jquery',
        'underscore',
        'handlebars', 
        'marionette',
        'hbs!views/warehouse/pick/view/pickship-layout'], 
function(Gloria, $, _, Handlebars, Marionette, compiledTemplate) {
    
    Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        View.PickShipLayout = Marionette.LayoutView.extend({
            
            regions: {                
                button : '#button',
                grid: '#grid'
            },

            initialize: function(options) {
                this.module = options.module;            
				this.listenTo(Gloria.WarehouseApp, 'printDispatchNote', this.printDispatchNote);    		
            },

            /**
			 * Print Document
			 */
			printDispatchNote : function(dispatchNoteModel, requestListModel, requestGroupCollection) {
				require(['hbs!views/warehouse/pick/view/print-dispatchNote'], function(Template) {
					var doc = document.getElementById('printInfo').contentWindow;
					doc.document.open('text/html', 'replace');
					doc.document.onreadystatechange = function () {
		                if (doc.document.readyState === 'complete') {
		                	doc.document.body.onafterprint = function () {
		                		doc.document.removeChild(doc.document.documentElement);
		                    };		                
		                    doc.document.execCommand('print', false, null);
		                };
		            };
					doc.document.write(Template({
						dispatchNoteModel : dispatchNoteModel ? dispatchNoteModel.toJSON() : {},
						requestListModel : requestListModel ? requestListModel.toJSON() : {},
						data : requestGroupCollection ? requestGroupCollection.toJSON() : []
					}));
					doc.document.close();
				});
			},
            render: function() {
                this.$el.html(compiledTemplate());
                return this;
            },
            
            onShow: function() {                           
                
            }
        });
    });
    
    return Gloria.WarehouseApp.View.PickShipLayout;    
});