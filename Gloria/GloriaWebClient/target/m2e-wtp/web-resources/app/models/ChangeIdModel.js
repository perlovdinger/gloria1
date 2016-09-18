define([ 
         'utils/backbone/GloriaModel' 
         ], function(Model) {

    var ChangeIdModel = Model.extend({
        urlRoot: '/procurement/v1/changeids',
        hasStatus: function(status) {
            if(!status) return this.get('status');
            return this.get('status') && this.get('status').toUpperCase() === status.toUpperCase(); 
        },        
        isWaitingForConfirmation: function() {
            return this.get('status') && this.get('status').toUpperCase() === 'WAIT_CONFIRM';
        },
        isAccepted: function() {
            return this.get('status') && this.get('status').toUpperCase() === 'ACCEPTED';
        },
        isRejected: function() {
            return this.get('status') && this.get('status').toUpperCase() === 'REJECTED';
        },
        isCancelWait: function() {
            return this.get('status') && this.get('status').toUpperCase() === 'CANCEL_WAIT';
        },
        isCancelled: function() {
            return this.get('status') && this.get('status').toUpperCase() === 'CANCELLED';
        },
        isCancelRejected: function() {
            return this.hasStatus('CANCEL_REJECTED');
        }
    });

    return ChangeIdModel;
});