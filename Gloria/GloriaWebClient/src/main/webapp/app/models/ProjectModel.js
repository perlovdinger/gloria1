define([
    'utils/backbone/GloriaModel'
], function(Model) {
    
	var ProjectModel = Model.extend({
	    
        urlRoot : '/material/v1/projects'
        
	});
	
	return ProjectModel;
});