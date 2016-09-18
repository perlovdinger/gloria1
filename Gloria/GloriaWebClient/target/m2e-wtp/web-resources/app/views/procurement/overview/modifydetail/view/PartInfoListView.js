define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
        'bootstrap',         
        'views/procurement/overview/modifydetail/view/PartInfoView',
        'hbs!views/procurement/overview/modifydetail/view/part-info-list'
], function(Gloria, $, _, Handlebars, Marionette, BootStrap, PartInfoView, compiledTemplate) {
	
	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		var id = null;
		var referenceId = null;
		var defaultUnitOfMeasure = 'PCE';
		var readOnly = false;
		var isSamePartInfo = false;
		var procQty = null;
		var notGrouped = null;
		
		View.PartInfoListView = Backbone.Marionette.CompositeView.extend({
			
			id: "procurePartInfoList",
			
			childView: PartInfoView,
			
			initialize: function(options) {
				_.bindAll(this, 'template');
				if(options.collection && options.collection.length < 1) {
					throw new Error('Collection must be supplied!');
				} 
	        	this.collection = options.collection;
	        	readOnly = options.readOnly;
	        	id = options.id;
	        	referenceId = options.referenceId;
	        	procQty = options.procQty;
	        	notGrouped = options.notGrouped;
	        	isSamePartInfo = this.allAreSame(this.collection.pluck('partNumber')) && this.allAreSame(this.collection.pluck('partVersion'))
                && this.allAreSame(this.collection.pluck('partModification')) && this.allAreSame(this.collection.pluck('partAffiliation'));
			},
			
			allAreSame : function (array) {
			    var first = array[0] || '';
			    return array.every(function(element) {
			        element = element || '';
			        return element == first;
			    });
			},
			
			template: function() {
				return compiledTemplate({
					id : id,
					referenceId : referenceId,
					isSamePartInfo : isSamePartInfo,
					unitOfMeasure : defaultUnitOfMeasure,
					readOnly : readOnly,
					procQty : procQty,
					notGrouped: notGrouped,
					models: this.collection.pluck('id').join(',')
				});
			},
			
			attachHtml: function(collectionView, itemView, index){
				collectionView.$('#partContainer').append(itemView.el);
			}
		});
	});
    
    return Gloria.ProcurementApp.View.PartInfoListView;
});
