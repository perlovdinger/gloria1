define(['app',
        'jquery',
        'underscore',
        'handlebars', 
        'marionette'
], function(Gloria, $, _, Handlebars, Marionette) {
    
    Gloria.module('Common.View', function(View, Gloria, Backbone, Marionette, $, _) {
        
        var selectedModels = undefined;
        
        View.PrinterView = Marionette.ItemView.extend({
            
            tagName: 'iframe',
            
			id: 'iframePrintInfo',

            events: {
                'load' : 'print'
            },           
            
            attributes: {
                style: 'display:none;'
            },
            
            initialize : function(options) {
                this.content = options && options.content;
                this.printCounter = 0;
            },
            
            template: false,
            
            print: function() {
                if(this.printCounter === 0){
				var doc = document.getElementById('printerViewInfo').contentWindow;
                var val = navigator.userAgent.toLowerCase(); 
                var isFirefox = val.indexOf("firefox") > -1;

                doc.document.open('text/html', 'replace');
                doc.document.onreadystatechange = function () {
                    if (doc.document.readyState === 'complete' ) {
	                	doc.document.body.onafterprint = function () {
	                		doc.document.removeChild(doc.document.documentElement);
	                    };
	                    if(!isFirefox){
	    	                $('#printerViewInfo')[0].focus();
	    	                $('#printerViewInfo')[0].contentWindow.print();
	                    }

                    };
                };
                doc.document.write(this.content); 
                if(isFirefox){
	                $('#printerViewInfo')[0].focus();
	                $('#printerViewInfo')[0].contentWindow.print();
                }
                doc.document.close();
                this.printCounter++;
                }
            }
        });
    });
    
    return Gloria.Common.View.PrinterView;
});