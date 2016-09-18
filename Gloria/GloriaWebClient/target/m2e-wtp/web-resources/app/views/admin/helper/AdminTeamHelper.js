define(['app',
        'utils/backbone/GloriaCollection',
        'utils/UserHelper'
], function(Gloria, Collection, UserHelper) {
    
    var getTeamListByTeamType = function(type) {
    	var jsonData = null;
        var userCollection = new Collection();
        userCollection.url = '/user/v1/teams?type=' + type;
        userCollection.fetch({
            cache : false,
            async : false,
            success : function(data) {
            	// Sort Team list
            	if(data) {
            		data.comparator = function(model) {
    					return model.get('name');
    				};
    				data.sort();
            	}
            	jsonData = data.toJSON();
            }
        });
        return jsonData;
    };
    
    var getTeamListByUserId = function(userId, type) {
    	var jsonData = null;
        var userCollection = new Collection();
        userCollection.url = '/user/v1/users/' + userId + '/teams?type=' + type;
        userCollection.fetch({
            cache : false,
            async : false,
            success : function(data) {
            	jsonData = data.toJSON();
            }
        });
        return jsonData; 
    };
    
    return {
        'getTeamListByTeamType' : getTeamListByTeamType,
        'getTeamListByUserId' : getTeamListByUserId
    };
    
});