define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'backbone',
        'marionette',
		'i18next',
		'bootstrap',
		'jquery-validation',
		'backbone.syphon',
		'utils/dialog/dialog',
		'hbs!views/warehouse/qualityinspection/details/view/qualityinspection-details'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, i18n, Bootstrap, Validation, Syphon, Dialog, compiledTemplate) {

	Gloria.module('WarehouseApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.QualityInspectionDetailsView = Marionette.LayoutView.extend({

			initialize : function(options) {

			},
			
			regions : {
				partInfoContent : '#partInfoContent',			
				documentsInfoContent : '#documentsInfoContent',
				problemDescAndAttachmentInfoContent : '#problemDescAndAttachmentInfoContent',
				inspectionInfoContent : '#inspectionInfoContent'
			},

			events : {			
				'click #cancel' : 'handleCancelClick',
				'click #save' : 'handleSaveClick'
			},

			handleCancelClick : function() {
				Dialog.show({
					title: i18n.t('Gloria.i18n.warehouse.qualityInspection.details.text.cancel.headerQualityInspectionDetails'),
					message: '<b>' + i18n.t('Gloria.i18n.warehouse.qualityInspection.details.text.cancel.mainTextQualityInspectionDetails') + '</b>'
								+ '<br/><br/>' + i18n.t('Gloria.i18n.warehouse.qualityInspection.details.text.cancel.subTextQualityInspectionDetails'),
					buttons: {
		                yes: {
		                    label: i18n.t('Gloria.i18n.buttons.yes'),
		                    className: "btn btn-primary",
		                    callback: function(e) {
		                        e.preventDefault();
		                        window.history.back();
		                        return true;
		                    }
		                },
		                no: {
		                    label: i18n.t('Gloria.i18n.buttons.no'),
		                    className: "btn btn-default",
		                    callback: function(e) {
		                        e.preventDefault();
		                        return false;
		                    }
		                }
		            }
				});
			},
			
			handleSaveClick : function(e) {
				e.preventDefault();				
				Gloria.WarehouseApp.on('QualityInspectionDetails:saved', function(resp) {
					window.history.back();
				});
				var formData = Backbone.Syphon.serialize(this);
				Gloria.WarehouseApp.trigger('QualityInspectionDetails:save', formData.deliveryNoteLine);
				
			},	
			
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}
		});
	});

	return Gloria.WarehouseApp.View.QualityInspectionDetailsView;
});
