/**
 * Material Report View
 */
define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars',
        'marionette',
		'hbs!views/reports/view/material-report',
		'views/reports/components/CompanyCodeSelector',
		'views/reports/components/SuffixSelector',
		'views/reports/components/ProjectSelector',
		'views/reports/components/BuildSeriesSelector',
		'views/reports/components/TestObjectSelector',
		'views/reports/components/MtrIdSelector',
		'views/reports/components/WbsSelector',
		'views/reports/components/MaterialStatusSelector',
		'views/reports/components/MaterialTypeSelector',
		'views/reports/components/MCTeamSelector',
		'views/reports/components/PhaseNameSelector',
		'utils/dialog/dialog'
], function(Gloria, $, i18n, _, Handlebars, Marionette, compiledTemplate, CompanyCodeSelector, SuffixSelector, ProjectSelector, BuildSeriesSelector,
		TestObjectSelector, MtrIdSelector, WbsSelector, MaterialStatusSelector, MaterialTypeSelector, MCTeamSelector, PhaseNameSelector, Dialog) {

	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.MaterialReportView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
				this.model = options.model;
			},
			
			regions : {
				companyCodeSelectorContainer : 'div#companyCodeSelectorContainer',
				suffixSelectorContainer : 'div#suffixSelectorContainer',
				projectSelectorContainer : 'div#projectSelectorContainer',
				buildSeriesSelectorContainer : 'div#buildSeriesSelectorContainer',
				testObjectSelectorContainer : 'div#testObjectSelectorContainer',
				mtrIdSelectorContainer : 'div#mtrIdSelectorContainer',
				wbsSelectorContainer : 'div#wbsSelectorContainer',
				materialStatusSelectorContainer : 'div#materialStatusSelectorContainer',
				materialTypeSelectorContainer : 'div#materialTypeSelectorContainer',
				mcTeamSelectorContainer : 'div#mcTeamSelectorContainer',
				phaseNameSelectorContainer : 'div#phaseNameSelectorContainer'
			},
			
			events : {
				'click .favorite-white' : 'addFavoriteFilter',
				'click .favorite-yellow' : 'deleteFavoriteFilter',
				'change [type="checkbox"]' : 'checkboxChangeHandler'
			},
			
			addFavoriteFilter : function(e) {
				e.preventDefault();
				Gloria.ReportsApp.trigger('Report:Favorite:show');
			},
			
			deleteFavoriteFilter: function(e) {
                e.preventDefault();
                var that = this;
                Dialog.show({
			    	title : i18n.t('Gloria.i18n.reports.text.deleteConfirmationTitle') + ' | ' + i18n.t('Gloria.i18n.gloriaHeader'),
                    message: i18n.t('Gloria.i18n.reports.text.deleteConfirmation'),
                    buttons: {
                        yes: {
                            label: i18n.t('Gloria.i18n.buttons.yes'),
                            className: 'btn btn-primary',
                            callback: function(e) {
                                e.preventDefault();
                                Gloria.ReportsApp.trigger('Report:Favorite:delete', that.model);
                                return true;
                            }
                        },
                        no: {
                            label: i18n.t('Gloria.i18n.buttons.no'),
                            className: 'btn btn-default',
                            callback: function(e) {
                                e.preventDefault();
                                return true;
                            }
                        }
                    }
                });
            },
			
			checkboxChangeHandler : function(e) {
				e.preventDefault();
				if(!this.model.isNew()) {
					var formData = Backbone.Syphon.serialize(this);
					var keys = Object.keys(formData.checkbox);
					var jsonFromModel = _.pick(this.model.toJSON(), keys);
					if(JSON.stringify(jsonFromModel) == JSON.stringify(formData.checkbox)) {
						this.$el.find('#fav').removeClass('favorite-white').addClass('favorite-yellow');
					} else {
						this.$el.find('#fav').removeClass('favorite-yellow').addClass('favorite-white');
					}
				}
			},
			
			// Company Code Selector
			companyCodeSelector : function() {
				this.companyCodeSelectorContainer.show(new CompanyCodeSelector({
					el : this.$('#companyCode')
				}));
			},
			
			// Suffix Selector
			suffixSelector : function() {
				this.suffixSelectorContainer.show(new SuffixSelector({
					el : this.$('#suffix')
				}));
			},
			
			// Project Selector
			projectSelector : function() {
				var formData = Backbone.Syphon.serialize(this);
				this.projectSelectorContainer.show(new ProjectSelector({
					el : this.$('#project'),
					companyCode : formData.dropdown.companyCode
				}));
			},
			
			// Build Series Selector
			buildSeriesSelector : function() {
				var formData = Backbone.Syphon.serialize(this);
				this.buildSeriesSelectorContainer.show(new BuildSeriesSelector({
					el : this.$('#buildSeries'),
					companyCode : formData.dropdown.companyCode
				}));
			},
			
			// Test Object Selector
			testObjectSelector : function() {
				this.testObjectSelectorContainer.show(new TestObjectSelector({
					el : this.$('#testObject')
				}));
			},
			
			 // Phase Name Selector
			phaseNameSelector : function() {
                this.phaseNameSelectorContainer.show(new PhaseNameSelector({
                    el : this.$('#phaseName')
                }));
            },
			
			// MTR Id Selector
			mtrIdSelector : function() {
				this.mtrIdSelectorContainer.show(new MtrIdSelector({
					el : this.$('#mtrId')
				}));
			},
			
			// WBS Selector
			wbsSelector : function() {
				this.wbsSelectorContainer.show(new WbsSelector({
					el : this.$('#wbs')
				}));
			},

			// Material Status Selector
			materialStatusSelector : function() {
				this.materialStatusSelectorContainer.show(new MaterialStatusSelector({
					el : this.$('#materialStatus')
				}));
			},
			
			// Material Type Selector
			materialTypeSelector : function() {
				this.materialTypeSelectorContainer.show(new MaterialTypeSelector({
					el : this.$('#materialType')
				}));
			},

			// Material Controller Team Selector
			mcTeamSelector : function() {
				this.mcTeamSelectorContainer.show(new MCTeamSelector({
					el : this.$('#materialControllerTeam')
				}));
			},

			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					isFav : !!this.model.id,
					data : this.model ? this.model.toJSON() : {}
				}));
				return this;
			},
			
			populate : function() {
				this.companyCodeSelector();
				this.suffixSelector();
				this.projectSelector();
				this.buildSeriesSelector();
				this.testObjectSelector();
				this.phaseNameSelector();
				this.mtrIdSelector();
				this.wbsSelector();
				this.materialStatusSelector();
				this.materialTypeSelector();
				this.mcTeamSelector();
			},
			
			onShow : function() {
				this.populate();
			}
		});
	});

	return Gloria.ReportsApp.View.MaterialReportView;
});
