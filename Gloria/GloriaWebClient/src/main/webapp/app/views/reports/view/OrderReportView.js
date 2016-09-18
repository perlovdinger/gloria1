/**
 * Order Report View
 */
define(['app',
        'jquery',
        'i18next',
        'underscore',
        'handlebars',
        'marionette',
        'backbone.syphon',
		'hbs!views/reports/view/order-report',
		'views/reports/components/CompanyCodeSelector',
		'views/reports/components/SuffixSelector',
		'views/reports/components/ProjectSelector',
		'views/reports/components/BuildSeriesSelector',
		'views/reports/components/TestObjectSelector',
		'views/reports/components/SupplierParmaIdSelector',
		'views/reports/components/SupplierNameSelector',
		'views/reports/components/ReferenceSelector',
		'views/reports/components/MtrIdSelector',
		'views/reports/components/DeliveryControllerIdSelector',
		'views/reports/components/DeliveryControllerNameSelector',
		'views/reports/components/OrderStatusSelector',
		'utils/dialog/dialog'
], function(Gloria, $, i18n, _, Handlebars, Marionette, Syphon, compiledTemplate, CompanyCodeSelector, SuffixSelector,
		ProjectSelector, BuildSeriesSelector, TestObjectSelector, SupplierParmaIdSelector, SupplierNameSelector,
		ReferenceSelector, MtrIdSelector, DeliveryControllerIdSelector, DeliveryControllerNameSelector, OrderStatusSelector, Dialog) {

	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.OrderReportView = Backbone.Marionette.LayoutView.extend({

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
				supplierParmaIdSelectorContainer : 'div#supplierParmaIdSelectorContainer',
				supplierNameSelectorContainer : 'div#supplierNameSelectorContainer',
				referenceSelectorContainer : 'div#referenceSelectorContainer',
				mtrIdSelectorContainer : 'div#mtrIdSelectorContainer',
				deliveryControllerIdSelectorContainer : 'div#deliveryControllerIdSelectorContainer',
				deliveryControllerNameSelectorContainer : 'div#deliveryControllerNameSelectorContainer',
				orderStatusSelectorContainer : 'div#orderStatusSelectorContainer'
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
			
			// Supplier Parma Id Selector
			supplierParmaIdSelector : function() {
				this.supplierParmaIdSelectorContainer.show(new SupplierParmaIdSelector({
					el : this.$('#supplierParmaId')
				}));
			},
			
			// Supplier Parma Name Selector
			supplierParmaName : function() {
				this.supplierNameSelectorContainer.show(new SupplierNameSelector({
					el : this.$('#supplierParmaName')
				}));
			},
			
			// Reference Selector
			referenceSelector : function() {
				this.referenceSelectorContainer.show(new ReferenceSelector({
					el : this.$('#reference')
				}));
			},
			
			// MTR Id Selector
			mtrIdSelector : function() {
				this.mtrIdSelectorContainer.show(new MtrIdSelector({
					el : this.$('#mtrId')
				}));
			},
			
			// Delivery Controller Id Selector
			deliveryControllerIdSelector : function() {
				var formData = Backbone.Syphon.serialize(this);
				this.deliveryControllerIdSelectorContainer.show(new DeliveryControllerIdSelector({
					el : this.$('#deliveryControllerId'),
					companyCode : formData.dropdown.companyCode,
					suffix : formData.dropdown.suffix
				}));
			},
			
			// Delivery Controller Name Selector
			deliveryControllerNameSelector : function() {
				this.deliveryControllerNameSelectorContainer.show(new DeliveryControllerNameSelector({
					el : this.$('#deliveryControllerName')
				}));
			},
			
			// Order Status Selector
			orderStatusSelector : function() {
				this.orderStatusSelectorContainer.show(new OrderStatusSelector({
					el : this.$('#orderStatus')
				}));
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module,
					isFav : !!this.model.id,
					data : this.model.toJSON()
				}));
				return this;
			},
			
			populate : function() {
				this.companyCodeSelector();
				this.suffixSelector();
				this.projectSelector();
				this.buildSeriesSelector();
				this.testObjectSelector();
				this.supplierParmaIdSelector();
				this.supplierParmaName();
				this.referenceSelector();
				this.mtrIdSelector();
				this.deliveryControllerIdSelector();
				this.deliveryControllerNameSelector();
				this.orderStatusSelector();
			},
			
			onShow : function() {
				this.populate();
			},
			
			onDestroy : function() {

			}
		});
	});

	return Gloria.ReportsApp.View.OrderReportView;
});
