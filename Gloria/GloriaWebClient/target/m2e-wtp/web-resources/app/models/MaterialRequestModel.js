define(
    ['utils/backbone/GloriaModel'
     ], function(Model) {

    var MaterialRequestModel = Model.extend({        
        urlRoot : '/materialrequest/v1/materialrequests',        
        
        isEditable : function() {   
            return this.isNew() || this.hasStatus('CREATED');
        },
        
        isEditableOrUpdated: function() {
            return this.isEditable() || this.hasStatus('UPDATED');
        },
        
        hasStatus: function(status) {
            if(!status) return this.get('status');
            return this.get('status') && this.get('status').toUpperCase() === status.toUpperCase(); 
        },
        
        isSavable: function() {
            return this.isEditableOrUpdated();
        },
        
        isSendable: function() {
            return this.isEditableOrUpdated();
        },
        
        isCancelable: function() {
            return this.isEditableOrUpdated();
        },
        
        isRevertable: function() {
            return this.hasStatus('UPDATED');
        }, 
        
        isDeletable: function() {
            return this.hasStatus('CREATED');
        },
        
        isClosable: function() {
            return (this.hasStatus('SENT') || this.hasStatus('SENT_WAIT') || 
                    this.hasStatus('SENT_ACCEPTED') || this.hasStatus('SENT_REJECTED'));
        },
        
        isNewVersionAvailable: function() {
            return this.hasStatus('SENT_ACCEPTED') || this.hasStatus('SENT_REJECTED');
        },
        
        isInWork: function() {
            return this.hasStatus('CREATED') || this.hasStatus('UPDATED') || this.hasStatus('SENT'); 
        },
        
        isRejected: function() {
            return this.hasStatus('SENT_REJECTED');
        },
        
        isAccepted: function() {
            return this.hasStatus('SENT_ACCEPTED');
        },
        
        isWaitingForConfirmation: function() {
            return this.hasStatus('SENT_WAIT');
        }
    });

    return MaterialRequestModel;
});