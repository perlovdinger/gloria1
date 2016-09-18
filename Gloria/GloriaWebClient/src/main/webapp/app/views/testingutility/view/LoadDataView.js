define([ 'app', 'jquery', 'underscore', 'handlebars', 'backbone', 'marionette',
		'i18next', 'jquery.fileupload',
		'hbs!views/testingutility/view/load-data' ], function(Gloria, $, _,
		Handlebars, Backbone, Marionette, i18n, FileUpload, compiledTemplate) {

	Gloria.module('TestingUtilityApp.View', function(View, Gloria, Backbone,
			Marionette, $, _) {

		View.LoadDataView = Marionette.LayoutView.extend({

			initialize : function(options) {
				this.statusModel = new Backbone.Model();
			},

			events : {
			    'change #dataType,#siteID,#companyCodeID': 'formUpdate',
				'click #initiate' : 'initiate'
			},
			
			formUpdate: function(e) {
			    var dataType = this.$('#dataType').val();
			    if(dataType == '/testingutility/v1/sendfinancemsg') {
			    	this.$('#siteID').hide();
			    	this.$('#companyCodeID').show();
				    var companyCodeID = this.$('#companyCodeID').val();
			    } else {
			    	this.$('#companyCodeID').hide();
			    	this.$('#siteID').show();
			    	var siteIDs = this.$('#siteID').val();
			    }
			    if(dataType && (siteIDs || companyCodeID)) {
			        this.$('#initiate').removeAttr('disabled','disabled');
			    } else {
			        this.$('#initiate').attr('disabled','disabled');
			    }
			},

			initiate : function(e) {
				e.preventDefault();
				var dataType = this.$('#dataType').val();
				if(dataType == '/testingutility/v1/sendfinancemsg') { // companyCodeID
					var companyCodeID = this.$('#companyCodeID').val();
					if(companyCodeID && companyCodeID.length > 0) {
					    Gloria.TestingUtilityApp.trigger('DataMigration:load:initiate', dataType, companyCodeID);
					    this.refresh();
					}
				} else { // SiteID
					var siteIDs = this.$('#siteID').val();
					if(siteIDs && siteIDs.length > 0) {
						Gloria.TestingUtilityApp.trigger('DataMigration:load:initiate', dataType, siteIDs);
						this.refresh();
					}
				}
			},	
			
			refresh: function(e) {
				var that = this;
				this.listenTo(Gloria.TestingUtilityApp, 'DataMigration:load:refresh:data', function(data) {
					that.done = data.done;
					that.data = data;
				});
				var intervalId = window.setInterval(function() {
					if (that.done) {
						this.$('#completed').text('100');
						this.$('#isDone').text('true');   
						clearInterval(intervalId);
					} else {
						that.updateStatus(that.data || {});
						Gloria.TestingUtilityApp.trigger('DataMigration:load:refresh');
					}
				}, 1000);
			},
			
			updateStatus: function(data) {
			    this.$('#status').val(data.status);
                this.$('#siteInProgress').text(data.siteInProgress);
                this.$('#siteCompleted').text(data.siteCompleted);
                this.$('#completed').text(data.completed);
                this.$('#isDone').text(data.done);                
            },
			
			render : function() {
				this.$el.html(compiledTemplate());
				return this;
			}			
		});
	});

	return Gloria.TestingUtilityApp.View.LoadDataView;
});
