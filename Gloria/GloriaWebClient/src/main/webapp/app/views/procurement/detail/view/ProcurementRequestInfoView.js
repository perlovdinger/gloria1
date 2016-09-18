define([ 'app',
         'jquery',
         'i18next',
         'handlebars',
         'backbone',
         'marionette',
         'bootstrap',         
         'views/procurement/detail/view/TestObjectView',
         'utils/dialog/dialog',
         'hbs!views/procurement/detail/view/procurement-request-info'
], function(Gloria, $, i18n, Handlebars, Backbone, Marionette, BootStrap, TestObjectView, Dialog, compiledTemplate) {
	
	Gloria.module('ProcurementApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.ProcurementRequestInfoView = Backbone.Marionette.CompositeView.extend({
			
			id: "procureRequestInfoList",
			
			childView: TestObjectView,
			
			initialize: function(options) {
				this.status = options.status;
				this.procureLineModel = options.procureLineModel;
				this.listenTo(Gloria.ProcurementApp, 'MultipleUpdate:update', this.populateFinalWarehouse);				
			},			
			
			events : {    
				'click #remove' : 'handleRemoveClickEvent',
				'click #view-menu li a' : 'handleViewClick',
				'click #multipleUpdate' : 'handleMultipleUpdate'
			},
			
			childViewOptions : function() {
				return {
					status : this.status,
					procureLineModel : this.procureLineModel
				};
			},
			
			collectionEvents: {
				'add remove': 'onChildviewSelect'
			},
			
			onChildviewSelect : _.debounce(function() {				
				var selectedViews = this.children.filter(function(view) {
					return (view.selected === true);
				});
				if(selectedViews && selectedViews.length > 0) {
					this.$('#remove').removeAttr('disabled');
					this.$('#multipleUpdate').removeAttr('disabled');
				} else {
					this.$('#remove').attr({'disabled' : 'true'});
					this.$('#multipleUpdate').attr({'disabled' : 'true'});
				}
			}, 200),
			
			handleMultipleUpdate : function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				var selectedTOs = [];
                this.children.each(function(view) {
                    if(view.selected) {
                    	selectedTOs.push(view.model);
                    }
                });
				Gloria.ProcurementApp.trigger('procurelineDetails:MultipleUpdate', selectedTOs);
			},
			
			populateFinalWarehouse : function(selectedTO, warehouseId) {
	        	_.each(selectedTO, function(to) {
		        	//to.set('finalWhSiteId', warehouseId);
	        		$('select[id^="warehouseId_' + to.id + '"]').val(warehouseId);
				});
			},
			
			handleRemoveClickEvent : function() {
			    Dialog.show({                    
                    message: i18n.t('Gloria.i18n.procurement.procureDetail.text.deleteTestObjConfirmation')                    
                }).dialog.on('ok', function(e){
                    var toRemoveItems = [];
                    this.children.each(function(view) {
                        if(view.selected) {
                            toRemoveItems.push(view.model);
                        }
                    });
                    Gloria.ProcurementApp.trigger('procurelineDetails:remove', toRemoveItems);
                }, this);			    
			},
			
			handleViewClick : function(e) {
			    var that = this;
	                e.preventDefault();
	                switch (e.currentTarget.id) {
	                case 'selectAll':
	                    //checkbox - checked property is set
	                    this.$el.find('.js-testObject').prop('checked',true);
	                    //Itemview selected property is set
	                    this.children.each(function(view) {
	                        view.selected = true;
	                    });
	                   
	                    this.onChildviewSelect();
	                    break;
	                case 'deselectAll':
	                    this.$el.find('.js-testObject').prop('checked',false);
	                    this.children.each(function(view) {
                            view.selected = false;
                        });
	                    this.onChildviewSelect();
                        break;   
	                case 'expandAll':
                        this.$el.find('[id^=generalInfo]').addClass('in');
                        break;  
	                case 'colapseAll':
	                    this.$el.find('[id^=generalInfo]').removeClass('in');
                        break;     
	                default:
	                    break;
	                };
	            },
			
			getTemplate: function() {
				return compiledTemplate;
			},
			
			attachHtml: function(collectionView, itemView, index){
				collectionView.$('#testObjectContainer').append(itemView.el);
			},
			
			onShow : function() {
				if(this.status === 'PROCURED' || this.status === 'PLACED' || this.status === 'RECEIVED' || this.status === 'RECEIVED_PARTLY') {
					$('#remove').hide();
					$('#multipleUpdate').hide();
				} else {
					$('#remove').show();
					$('#multipleUpdate').show();
				} 
				
			}
		});
	});
    
    return Gloria.ProcurementApp.View.ProcurementRequestInfoView;
});
