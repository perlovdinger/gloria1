define([ 
    'utils/backbone/GloriaModel'
], function(Model, UserHelper) {
    
    var LDAPUserDTOModel = Model.extend({
        
        urlRoot : function() {
            return '/user/v1/ldapusers/' + this.get('userId');
        }
            
    });
    return LDAPUserDTOModel;
});