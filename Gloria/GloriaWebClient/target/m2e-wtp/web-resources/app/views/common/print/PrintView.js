define([
    'jquery',
    'handlebars',
    'backbone',
    'models/PartModel',
    'models/PrintModel',
    'hbs!views/common/print/print'
], function($, Handlebars, Backbone, PartModel, PrintModel, compiledTemplate) {
	
	var PrintView = Backbone.View.extend({		
		el : $('body'),
		events : {
			'click #print' : 'handlePrint',			
			'click #cancel' : 'handleCancel'
		},
    	html : undefined,
    	
	    collectData : function(){
	    	this.model.copies = $(this.el).find('#printLabelNo').val();
	    	return this.model;	
	    },
	    handlePrint : function() {
	    	var that = this;
			var printModel = new PrintModel({
				'id':that.model.get('partId'),
				'partNo':that.model.get('partNo'),
				'partVersion':that.model.get('partVersion'),
				'copies':that.$el.find('#printLabelNo').val()
			});
			
			printModel.save({},{
				success: function(){
					that.html.modal('hide');
				}
			});
		},
		
		handleCancel : function(e) {
			this.html.modal('hide');
		},
		
	    render : function (id) {    	
	    	var that = this;    	
	    	
	    	this.model = new PartModel({
				id : id
			});
	    	this.model.fetch({
				success : function(data) {
					that.html = $(compiledTemplate(
							data.toJSON()	
			    	));
					$(that.el).append(that.html);
					that.html.modal({
		        	  	keyboard : true
					}).on('hidden', that.removeHtml);
				}
			});	        
	        return this;
	    },
	    
	    removeHtml : function(event) {
	    	this.remove();
	    }	    
	});
	
	return PrintView;
});