define([ 'utils/typeahead/TypeaheadView' ], function(TypeaheadView) {

    var BuyerCodeSelector = TypeaheadView.extend({
        
        key : 'buyerId',

        url : function() {
            return '/procurement/v1/buyercodes?organisationCode=' + this.purchaseOrganisationCode;
        },

        //cachePrefix : 'tpah.projects.',

        initialize : function(options) {
            options || (options = {});
            this.purchaseOrganisationCode = options.purchaseOrganisationCode;
            this.disabled = options.disabled;
           TypeaheadView.prototype.initialize.call(this, options);
        },

        resultMap : {
            id : 'buyerId',
            text : 'buyerId'
        },
        
        // making select2 options
        select2DefaultOptions : function() {
               return (_.extend(TypeaheadView.prototype.select2DefaultOptions.apply(this),{
                      createSearchChoice: this.createSearchChoice
               }));
        },
        
        createSearchChoice: function(term, data) {
               var results = _.filter(data, function(item) {
                      return (item && item.toString().localeCompare(term)) === 0; 
               });
               if (results.length === 0) {
           return {id:term, text:term};
             }              
        },
        
        searchTerm : function(term) {
            var data = {};
            if (this.purchaseOrganisationCode) {
            if (this.key) {
                data[this.key] = '*' + term;
            }
            return data;
        }
        }
    });

    return BuyerCodeSelector;
});