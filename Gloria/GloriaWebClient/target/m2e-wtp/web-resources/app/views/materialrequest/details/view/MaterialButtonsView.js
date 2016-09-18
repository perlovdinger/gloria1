define(['app',
        'jquery',
        'i18next',
        'underscore',
		'handlebars', 
		'marionette',
		'jquery.fileupload',
		'utils/dialog/dialog',		
		'hbs!views/materialrequest/details/view/material-buttons'
], function(Gloria, $, i18n, _, Handlebars, Marionette, FileUpload, Dialog, compiledTemplate) {
    
	Gloria.module('MaterialRequestApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.MaterialButtonsView = Marionette.LayoutView.extend({
			
			initialize: function(options) {
				this.materialRequestModel = this.collection.materialRequest;
	            _.extend(this.defalutPermittedActions, options.permittedActions); 
	            this.listenTo(Gloria.MaterialRequestApp, 'materialInfoGrid:select', this.materialInformationGridSelectHandler);
	            this.listenTo(this.collection, 'sync', this.updateMaterialButtons);
	            this.listenTo(Gloria.MaterialRequestApp, 'MaterialInfo:ResetButtons', this.resetButtons);
            },
			
			events: {
				'click #clear-filter' : 'clearFilter',
				'click #importExcel-button': 'importExcel',
				'click #addNew-button' : 'addMaterialInfo',
				'click #edit-button' : 'editMaterialInfo',			
				'click #remove-button' : 'showRemoveDialog',
                'click #undoRemove-button' : 'undoRemoveMaterialInfo'
			},
			
			clearFilter : function() {
				Gloria.trigger('Grid:Filter:clear');
			},
			
			updateMaterialButtons: function() {
	        	this.permittedActions = _.clone(this.defalutPermittedActions);
	        	if(this.collection && this.collection.length > 0 && this.materialRequestModel && !this.materialRequestModel.isNew()) {
	        		this.permittedActions['exportExcel'] = true;
	        		$('#send').removeAttr('disabled');
	        		$('#exportExcel').removeAttr('disabled');
	        	} else {	        		
	        		delete this.permittedActions['exportExcel'];
	        		$('#send').attr('disabled', true);
	        		$('#exportExcel').attr('disabled', true);
	        	}
	        	this.render();
	        },
	        
			importExcel: function(e) {
				e.preventDefault();
				Gloria.trigger('hideAppMessageView');
				var that = this;
				Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:importExcel:process', function() {
					that.$('#docFile')[0].click();
				});
			},

			hideError : function() {
				Gloria.trigger('hideAppMessageView');
			},
			
			showError : function(errorMessage, errors) {
				Gloria.trigger('showAppMessageView', {
					type : 'error',
					title : '',//i18n.t('Gloria.i18n.materialrequest.validation.title'),
					message : errorMessage
				});
			},
			
			materialRequestLineExcelFileUpload : function() {
				var that = this;
				var materialRequestLineExcelFile = this.$('#docFile');
				materialRequestLineExcelFile.fileupload({
					url: '/documents/v1/materialrequest/' + that.materialRequestModel.id + '/materialrequestlines',
					dataType: 'json',
					singleFileUploads: 'true',
					add: function(e, data) {
						that.hideError();
						data.url = '/documents/v1/materialrequest/' + that.materialRequestModel.id + '/materialrequestlines';
						data.submit();
						materialRequestLineExcelFile.attr('disabled', true);
					},
					progressall: function(e, data) {},
					done: function(e, data) {						
						Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:excel:imported');
						materialRequestLineExcelFile.removeAttr('disabled');
					},
					fail: function(e, data) {
						materialRequestLineExcelFile.removeAttr('disabled');						
					},
					validationError: that.showError
				});
			},
			
			addMaterialInfo: function() {
				Gloria.trigger('hideAppMessageView');
				Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:materialInfo:addOredit', null);			
			},
			
			editMaterialInfo: function() {
				Gloria.trigger('hideAppMessageView');
				Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:materialInfo:addOredit', _.first(this.selectedRows));			
			},
			
			undoRemoveMaterialInfo: function() {
			    var model = _.first(this.selectedRows);
			    Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:materialInfo:undoRemove', model);
			},
			
			showRemoveDialog: function() {
				Gloria.trigger('hideAppMessageView');
				var that = this;
				Dialog.show({
			    	message: i18n.t('Gloria.i18n.materialrequest.details.materialInformation.deleteConfirmation'),
                    buttons: {
		                yes: {
		                    label: i18n.t('Gloria.i18n.buttons.yes'),
		                    className: "btn btn-primary",
		                    callback: function(e) {
		                        e.preventDefault();
		                        that.removeMaterialInfo();
		                        return true;
		                    }
		                },
		                no: {
		                    label: i18n.t('Gloria.i18n.buttons.no'),
		                    className: "btn btn-default",
		                    callback: function(e) {
		                        e.preventDefault();
		                        return true;
		                    }
		                }
                    }
                });
			},
			
			removeMaterialInfo: function() {
			    if(this.selectedRows) {
                    var materialRequestId = _.first(this.selectedRows);
                    Gloria.MaterialRequestApp.trigger('MaterialRequestDetails:materialInfo:remove', this.selectedRows);
                }
			},
			
			defalutPermittedActions: {				
                addNew:true,
                importExcel:true,
                edit: false,
                remove: false,
                undoRemove: false,
                exportExcel: false
            },
            
            resetButtons : function() {
            	this.permittedActions = _.clone(this.defalutPermittedActions);
	        	this.render();
			},
	        
			render: function() {
				this.$el.html(compiledTemplate({
					permittedActions : this.permittedActions || this.defalutPermittedActions,
					materialRequestId: (this.materialRequestModel && this.materialRequestModel.id),
					show: this.materialRequestModel.isNew() || this.materialRequestModel.isEditableOrUpdated(),
					isSaved: !this.materialRequestModel.isNew() && this.materialRequestModel.isEditableOrUpdated() 
				}));
				this.materialRequestLineExcelFileUpload();
				return this;
			},
			
			/*
             * This method evaluates the selected grid rows and based on them 
             * sets the parameters to enable or disable the buttons.
             */
			materialInformationGridSelectHandler: function(selectedRows) {
			    this.selectedRows = null;
			    this.permittedActions = _.clone(this.defalutPermittedActions);
				if(selectedRows) {					
					this.selectedRows = selectedRows;					
					if(selectedRows.length == 1) {
					    var model = _.first(this.selectedRows);
					    if(model.get('removeMarked')) {
					        this.permittedActions['undoRemove'] = true;
					    } else {
					        this.permittedActions['edit'] = true;
					    }
                    } 
					if(selectedRows.length >= 1) {						
						this.permittedActions['remove'] = true;
					}
				}
				if(this.collection && this.collection.length > 0 && this.materialRequestModel && !this.materialRequestModel.isNew()) {
	        		this.permittedActions['exportExcel'] = true;
	        	} else {	        		
	        		delete this.permittedActions['exportExcel'];
	        	}
				this.render();
			},
			
			onDestroy : function() {

			}
		});
	});
	
	return Gloria.MaterialRequestApp.View.MaterialButtonsView;
});
