define(
    ['utils/backbone/GloriaModel'
     ], function(Model) {

    var ProcureRequestHeaderModel = Model.extend({
        urlRoot : '/procurement/v1/materialheaders/current',

        isEditable : function() {
            return this.get('status') ? (this.get('status').toLowerCase() == 'in_work') : true;
        }
    });

    return ProcureRequestHeaderModel;
});