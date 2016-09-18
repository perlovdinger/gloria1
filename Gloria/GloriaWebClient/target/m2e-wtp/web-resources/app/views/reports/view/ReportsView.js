/**
 * Reports View
 */
define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
        'i18next',
        'backbone.syphon',
        'jquery-validation',
		'hbs!views/reports/view/reports'
], function(Gloria, $, _, Handlebars, Marionette, i18n, Syphon, Validation, compiledTemplate) {

	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.ReportsView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
				this.listenTo(Gloria.ReportsApp, 'Report:Favorite:add', this.addFavoriteFilter);
				this.listenTo(Gloria, 'Excel:download:completed', this.handleDownloadComplete);
			},
			
			regions : {
				moduleInfo : '#moduleInfo',
				basicInfo : '#basicInfo',
				possibleInfo : '#possibleInfo',
			},
			
			events : {
				'click #exportExcel' : 'handleExportExcelClick',
				'click #cancel' : 'handleCancelClick'
			},
			
			handleExportExcelClick : function(e) {
				if(this.module == 'performance') {
					if (this.isValidForPerformanceForm()) {
						this.exportExcel();
					}
				} else {
					this.exportExcel();
				}
			},
			
			exportExcel : function() {
				this.originalText = $('#exportExcel').html();
				$('#exportExcel').html(i18n.t('Gloria.i18n.general.pleaseWait'));
				$('#exportExcel').attr('disabled', 'disabled');
				var formData = Backbone.Syphon.serialize(this);
				Gloria.ReportsApp.trigger('Report:Excel:export', _.extend(formData.dropdown, formData.checkbox), formData.basicInfo);
			},
			
			handleCancelClick : function(e) {
				Gloria.trigger('showHomePage');
			},
			
			validatorForPerformance : function() {				
				return this.$el.find('form').validate({
					ignore: '',
					rules: {
						'basicInfo[dateType]': {
							required: true
						}
					},
					messages: {
						'basicInfo[dateType]': {
							required: i18n.t('errormessages:errors.GLO_ERR_074')
						}
					},
					showErrors: function (errorMap, errorList) {
						Gloria.trigger('showAppMessageView', {
		        			type : 'error',
		        			title : i18n.t('errormessages:general.title'),
		        			message : errorList
		        		});
			        }
				});
			},
			
			isValidForPerformanceForm : function() {
				return this.validatorForPerformance().form();
			},

			addFavoriteFilter : function(name) {
				var formData = Backbone.Syphon.serialize(this);
				Gloria.ReportsApp.trigger('Report:Favorite:save', name, formData.checkbox);
			},
			
			handleDownloadComplete : function() {
				$('#exportExcel').removeAttr('disabled');
				$('#exportExcel').html(this.originalText);
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					isModule : this.module ? true : false
				}));
				return this;
			}
		});
	});

	return Gloria.ReportsApp.View.ReportsView;
});
