define([ 
    'utils/backbone/GloriaModel'
], function(Model) {
	
    var PublicConfigurationModel = Model.extend({
        
        urlRoot : function() {
            return '/GloriaUIServices/api/common/v1/publicconfiguration';
        }
            
    });
    return PublicConfigurationModel;
});