define(['app',
        'jquery',
        'underscore',
		'handlebars', 
		'marionette',
        'bootstrap',
        'utils/backbone/GloriaCollection',
        'views/procurement/overview/modifydetail/view/PartInfoListView',
        'hbs!views/procurement/overview/modifydetail/view/test-object-list'
], function(Gloria, $, _, Handlebars, Marionette, BootStrap, Collection, PartInfoListView, compiledTemplate) {
	
	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.TestObjectListView = Backbone.Marionette.LayoutView.extend({
			
			initialize: function(options) {
				if(options.collection && options.collection.length < 1) {
					throw new Error('Collection must be supplied!');
				};
				this.collection = options.collection;				
				this.procModel = options.procModel;
				this.listenTo(this.collection, 'add remove', this.render);
			},
			
			events : {
	    		'change input[type=checkbox][id^="testObject_"]' : 'testObjectSelectHandler',
	    		'click #modifyLater' : 'handleModifyLater'
	        },
	        
			testObjectSelectHandler : function(event) {
				var that = this;
				this.referenceIdArr = [];
				var $groups = this.$('input[type=checkbox][id^="testObject_"]');
				$groups.each(function(i, group) {
					if(group.checked) {
						var models = $(group).data('models') + '';						
						that.referenceIdArr = that.referenceIdArr.concat(models.split(','));
					}
				});
				if(this.referenceIdArr.length != 0 && this.referenceIdArr.length < this.collection.length) {
					this.$('#modifyLater').removeAttr('disabled');
				} else {
					this.$('#modifyLater').attr('disabled', true);
				};
			},
			
			handleModifyLater : function(event) {				
				_.each(this.referenceIdArr, function(id, index) {
					var models = this.collection.get(id);
					if(index == this.referenceIdArr.length - 1) {
						this.collection.remove(models);
					} else {
						this.collection.remove(models, {silent: true});
					}
				}, this);
			},		
			
			renderPartInfoListView : function(collection, id, referenceId, readOnly, procQty, notGrouped) {	
	        	var partInfoListView = new PartInfoListView({
	        		collection : collection,
	        		id : id,
	        		referenceId : referenceId,
	        		readOnly : readOnly,
	        		procQty : procQty,
	        		notGrouped: notGrouped
	        	});
	        	this.$('#testObjectContainer').append('<div id=testObject' + id +'></div>');
	        	this.addRegion('testObjectRegion', '#testObject' + id);
	        	this.testObjectRegion.show(partInfoListView);
			},
			
			render : function() {				
				this.$el.html(compiledTemplate({
					readOnly : !!this.procModel
				}));				
				
				this.attachChildren();
				return this;
			},
			
			attachChildren: function() {
				var groupedCollection = this.collection.groupBy('referenceId');				
				_.each(groupedCollection, function (value, key, list) {
					var data = new Collection(value);
					//**********************************************
					// Splits visually materials with the same partAffiliation, partModification, partName, partNumber and partVersion				
					data.each(function(model, index) {
						if(!model) return;
						delete model.notGrouped;
						var notUnique =  data.where({
							partAffiliation: model.get('partAffiliation'),
							partModification: model.get('partModification'),
							partName: model.get('partName'),
							partNumber: model.get('partNumber'),
							partVersion: model.get('partVersion')
						});
						if(notUnique && notUnique.length > 1) {
							var actualModel = this.collection.get(model.id);
							// Use the below attribute when reading the data using Suphone and also in controller 
							// to determine how to send the request to save the data
							actualModel.notGrouped = true;
							var data1 = new Collection([model]);
							this.renderPartInfoListView(data1, data1.at(0).get('id'), data1.at(0).get('referenceId'), !!this.procModel, data1.at(0).get('procurementQty'), true);
						}
					}, this);
					//**********************************************
					data.reset(data.filter(function(model) {
						return model.notGrouped !== true;
					}));
					if(data && data.length) {						
						this.renderPartInfoListView(data, data.at(0).get('id'), data.at(0).get('referenceId'), !!this.procModel, data.at(0).get('procurementQty'));
					}
	 	        }, this);
			}
		});
	});
    
    return Gloria.ProcurementApp.View.TestObjectListView;
});
