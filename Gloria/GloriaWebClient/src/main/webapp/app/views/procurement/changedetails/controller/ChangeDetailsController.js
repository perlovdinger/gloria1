define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
        'i18next'
],function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n) {

    Gloria.module('ProcurementApp.ChangeDetails.Controller', function(Controller, Gloria, Backbone, Marionette, $, _) {
        
        var changeDetailsId;
        var changeIdModel;
        var requestLineCollection;

        var prepareChangeIdInformation = function() {           
            require(['models/ChangeIdModel',
                     'collections/RequestLineCollection',
                     'views/procurement/changedetails/view/PageLayout',                                          
                     'views/procurement/changedetails/view/RelatedPartGrid' ],
            function(ChangeIdModel, RequestLineCollection, PageLayout, RelatedPartGrid) {
                
                if(changeIdModel) changeIdModel.off();
                changeIdModel = new ChangeIdModel({
                    id: changeDetailsId
                });                

                if (requestLineCollection) {
                    requestLineCollection.off();
                }
                requestLineCollection = new RequestLineCollection([], {
                    mode : 'client'
                });
                requestLineCollection.comparator = function(model) {
                    // To be sorted in descending order
                    return -model.get('procureLineId');
                };
                requestLineCollection.changeIdModel = changeIdModel;
                
                // Instantiate Layout
                var pageLayout = new PageLayout({
                    model: changeIdModel,
                    collection : requestLineCollection
                });
                // Add Layout to the main content region.
                Gloria.basicLayout.content.show(pageLayout);
                
                changeIdModel.fetch({                    
                    success: function(model) {
                        showGeneralInformation(pageLayout, model);
                        // Instantiate the RelatedPartGrid
                        var relatedPartGrid = new RelatedPartGrid({
                        	changeIdModel : model,
                            collection : requestLineCollection
                        });
                        pageLayout.gridPane.show(relatedPartGrid);
                        fetchGridData();

                    }
                });
                
            });
        };

        // This method will be called for fetching the material information, also after Add and Remove functions.  
        var fetchGridData = function() {            
            requestLineCollection.url = '/procurement/v1/changeids/' + changeDetailsId + '/materials';
            requestLineCollection.fetch({
                reset : true,
                success : function(collection) {
                    collection.setPageSize(collection.fullCollection.length || 1);
                }
            });            
        };
        
        var showGeneralInformation = function(layout, model) {
            require(['views/procurement/changedetails/view/GeneralInformation'], function(GeneralInformation) {
                Gloria.trigger('showBreadcrumbView',  {CRID : model.get('changeId')}); 
                // Instantiate the General Information
                var generalInformation = new GeneralInformation({
                    model : model
                });
                layout.generalInformation.show(generalInformation);
            });
        };

        /* Reset the main layouts of the page, update the breadcrumb and 
         * call the page preparation method.
         */ 
        var changeIdInformation = function() {
            Gloria.basicModalLayout.closeAndReset();
            Gloria.basicLayout.content.reset();
            Gloria.trigger('showBreadcrumbView', {CRID: ''});
            prepareChangeIdInformation.call(this);
        };
        
        var isUpdateOrderChecked = function(){
        	requestLineCollection.each(function(model){
        		var currentModel = model;
        		var currentModelId= model.id;
        		var isChecked = $('#updateOrderChkBox_'+currentModelId).prop("checked");
        		if(currentModel.get('mark') === 'REMOVED' && isChecked){
        			model.attributes.updateOrder= true;
        		}
        	});
        	
        };
        
        var acceptChangeId = function(){
        	isUpdateOrderChecked();
        	Backbone.sync('update', requestLineCollection, {
                url : '/procurement/v1/changeids/' + changeDetailsId+ '/materials?action=accept',
                success : function(model) {	
                	Gloria.trigger('goToPreviousRoute');
                },
            	validationError : function(errorMessage, errors) {
					Gloria.trigger('showAppMessageView', {
						type : 'error',
						title : i18n.t('errormessages:general.title'),
						message : errorMessage
					});
				}
	    	});
        };
        
        var rejectChangeId = function(){
        	isUpdateOrderChecked();
        	Backbone.sync('update', requestLineCollection, {
                url : '/procurement/v1/changeids/' + changeDetailsId+ '/materials?action=reject',
                success : function(model) {	
                	Gloria.trigger('goToPreviousRoute');
                }
	    	});
        };
        
        Controller.ChangeDetailsController = Marionette.Controller.extend({

			initialize : function() {
				this.initializeListeners();
			},

			initializeListeners : function() {				
			    this.listenTo(Gloria.ProcurementApp, 'changeID:accept', acceptChangeId);
			    this.listenTo(Gloria.ProcurementApp, 'changeID:reject', rejectChangeId);
			},

            control : function(id) {   
                changeDetailsId = id;
                changeIdInformation.call(this);                 
            },
            
            onDestroy: function() {
                changeDetailsId = null;
                changeIdModel = null;
                requestLineCollection = null;
            }
        });
    });

    return Gloria.ProcurementApp.ChangeDetails.Controller.ChangeDetailsController;
});
