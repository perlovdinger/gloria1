/**
 * Part Delivery Precision Report View
 */
define(['app',
        'jquery',
        'underscore',
        'handlebars',
        'marionette',
        'backbone.syphon',
		'views/reports/components/CompanyCodeSelector',
		'views/reports/components/SuffixSelector',
		'views/reports/components/ProjectSelector',
		'views/reports/components/BuyerIdSelector',
		'views/reports/components/SupplierParmaIdSelector',
		'views/reports/components/SupplierNameSelector',
		'views/reports/components/DeliveryControllerIdSelector',
		'views/reports/components/SourceSelector',
		'hbs!views/reports/view/part-delivery-precision-report'
], function(Gloria, $, _, Handlebars, Marionette, Syphon, CompanyCodeSelector, SuffixSelector,
		ProjectSelector, BuyerIdSelector, SupplierParmaIdSelector, SupplierNameSelector, DeliveryControllerIdSelector, 
		SourceSelector, compiledTemplate) {

	Gloria.module('ReportsApp.View', function(View, Gloria, Backbone, Marionette, $, _) {

		View.PartDeliveryPrecisionReportView = Backbone.Marionette.LayoutView.extend({

			initialize : function(options) {
				this.module = options.module;
			},
			
			regions : {
				companyCodeSelectorContainer : 'div#companyCodeSelectorContainer',
				suffixSelectorContainer : 'div#suffixSelectorContainer',
				projectSelectorContainer : 'div#projectSelectorContainer',
				buyerIdSelectorContainer : 'div#buyerIdSelectorContainer',
				supplierParmaIdSelectorContainer : 'div#supplierParmaIdSelectorContainer',
				supplierNameSelectorContainer : 'div#supplierNameSelectorContainer',
				deliveryControllerIdSelectorContainer : 'div#deliveryControllerIdSelectorContainer',
				sourceSelectorContainer : 'div#sourceSelectorContainer'
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
				this.projectSelectorContainer.show(new ProjectSelector({
					el : this.$('#project')
				}));
			},
			
			// Buyer Id Selector
			buyerIdSelector : function() {
				this.buyerIdSelectorContainer.show(new BuyerIdSelector({
					el : this.$('#buyerId')
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
			
			// Delivery Controller Id Selector
			deliveryControllerIdSelector : function() {
				var formData = Backbone.Syphon.serialize(this);
				this.deliveryControllerIdSelectorContainer.show(new DeliveryControllerIdSelector({
					el : this.$('#deliveryControllerId'),
					suffix : formData.dropdown.suffix
				}));
			},
			
			// Source Selector
			sourceSelector : function() {
				this.sourceSelectorContainer.show(new SourceSelector({
					el : this.$('#source')
				}));
			},
			
			render : function() {
				this.$el.html(compiledTemplate({
					module : this.module
				}));
				return this;
			},
			
			populate : function() {
				this.companyCodeSelector();
				this.suffixSelector();
				this.projectSelector();
				this.buyerIdSelector();
				this.supplierParmaIdSelector();
				this.supplierParmaName();
				this.deliveryControllerIdSelector();
				this.sourceSelector();
			},
			
			onShow : function() {
				this.populate();
			},
			
			onDestroy : function() {

			}
		});
	});

	return Gloria.ReportsApp.View.PartDeliveryPrecisionReportView;
});
