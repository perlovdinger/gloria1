define(['utils/backbone/GloriaPageableCollection',
        'models/ProjectModel' 
        ], function(PageableCollection,ProjectModel) {
	
	var ProjectCollection = PageableCollection.extend({
	   
		model : ProjectModel,
	    
        state : undefined,
        
		url : '/material/v1/projects'
        
	});
	
	return ProjectCollection;
});