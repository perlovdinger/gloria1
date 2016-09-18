define(['app',
        'jquery',
        'underscore',
 	    'handlebars',
 	    'backbone',
 	    'marionette',
	    'bootstrap',
	    'i18next',
	    'select2',
	    'datepicker',
		'moment',
	    'utils/typeahead/CompanyCodeTypeaheadView',
	    'utils/typeahead/GLAccountTypeaheadView',
	    'utils/typeahead/BuildSitesTypeaheadView',
	    'hbs!views/material/details/view/material-info'
], function(Gloria, $, _, Handlebars, Backbone, Marionette, BootStrap, i18n, select2, Datepicker, moment,
		CompanyCodeTypeaheadView, GLAccountTypeaheadView, BuildSitesTypeaheadView, compiledTemplate) {

	Gloria.module('MaterialApp.View', function(View, Gloria, Backbone, Marionette, $, _) {
		
		View.MaterialInfoView = Marionette.LayoutView.extend({
	        
	    	initialize : function(options) {
	    		this.model = options.model;
	    		this.materialLineModel = options.materialLineModel;
	    		this.userWithMCRole = options.userWithMCRole;
	    		this.listenTo(Gloria.MaterialApp, 'editMode:materialLines', this.enterEditMode);
	    	},
	    	
	    	regions : {
				companyCodeSelectorContainer : 'div#companyCodeSelectorContainer',
				glAccountSelectorContainer : 'div#glAccountSelectorContainer',
				outboundLocationIdSelectorContainer : 'div#outboundLocationIdSelectorContainer'
			},
	    	
	    	enterEditMode: function() {
	    		this.$('span#partVersion').addClass('hidden');
				this.$('input#partVersion').removeClass('hidden');
	    	},
	        
	    	// CompanyCode Typeahead View
			companyCodeTypeaheadView : function() {
				var that = this;
				this.companyCodeSelectorContainer.show(new CompanyCodeTypeaheadView({
					type : 'MATERIAL_CONTROL',
					userId : false,
					el : this.$('#companyCode'),
					disabled : true,
					select2Options: {
					    width: 'off'
					}
				}));
			},
			
			// GLAccount Typeahead View
			glAccountTypeaheadView : function(companyCode) {
				var that = this;
				this.glAccountSelectorContainer.show(new GLAccountTypeaheadView({
					el : this.$('#glAccount'),
					companyCode : companyCode,
					disabled : true,
                    select2Options: {
                        width: 'off'
                    }
				}));
			},
			
			// BuildSites Typeahead View
			buildSitesTypeaheadView : function() {
				var that = this;
				this.outboundLocationIdSelectorContainer.show(new BuildSitesTypeaheadView({
					el : this.$('#outboundLocationId'),
					disabled : true,
                    select2Options: {
                        width: 'off'
                    }
				}));
			},
	    	
	        render : function() {
	        	this.$el.html(compiledTemplate({
	        		data: this.model ? this.model.toJSON() : {},
	        		isEditable : this.enableOrDiableBuildStartDate()	
	        	}));
	        	this.$('.date').datepicker();
	            return this;
	        },
	        
	  enableOrDiableBuildStartDate : function () {
	      if(this.userWithMCRole && this.model.get('migrated') && 
	    		  !(this.materialLineModel.get('status') == 'CREATED' || this.materialLineModel.get('status') == 'WAIT_TO_PROCURE'
	        			|| this.model.get('materialType') == 'REMOVED_DB')) {
	    	  Gloria.MaterialApp.trigger('MaterialDetails:testobject:showhide', true);
	        		return true;
	        	} else { return false; }
			},
	        
	        showHideUpdateTO : function () {
	        	if(this.userWithMCRole && this.model.get('migrated') 
	        			&& (this.materialLineModel.get('status') == 'STORED' || this.materialLineModel.get('status') == 'SHIPPED')
	        			&& (this.model.get('materialType') != 'ADDITIONAL_USAGE' && this.model.get('materialType') != 'ADDITIONAL' 
	        					&& this.model.get('materialType') != 'RELEASED')) {
	        		this.$('#updateTO').removeClass('hidden');
					this.$('span#testObject').addClass('hidden');
					this.$('input#testObject').removeClass('hidden');
		        	Gloria.MaterialApp.trigger('MaterialDetails:testobject:showhide', true);
	        	}
			},
	        
	        onShow : function() {
				this.companyCodeTypeaheadView();
				this.glAccountTypeaheadView(this.model.get('companyCode'));
				this.buildSitesTypeaheadView();
				this.showHideUpdateTO();
				
			},
			
			onDestroy : function() {
				this.$('.date').datepicker('remove');
			}
	    });
	});
    
	return Gloria.MaterialApp.View.MaterialInfoView;
 });
