define([ 'utils/backbone/GloriaModel' ], function(Model) {
	
	var MaterialRequestModel = Model.extend({        
       
		urlRoot : '/materialrequest/v1/materialrequests', 
		
		defaults: {
		    "type":  "SINGLE"	
		},
        
        isEditable : function() {            
            return this.isNew() || this.hasStatus('CREATED');
        },
        
        isEditableOrUpdated: function() {
            return this.isEditable() || this.hasStatus('UPDATED');
        },
        
        hasStatus: function(status) {
            if(!status) return this.get('materialRequestVersionStatus');
            return this.get('materialRequestVersionStatus') && this.get('materialRequestVersionStatus').toUpperCase() === status.toUpperCase(); 
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
            return (this.hasStatus('SENT') || this.hasStatus('SENT_WAIT')
            		|| this.hasStatus('SENT_ACCEPTED') || this.hasStatus('SENT_REJECTED')
                    || this.hasStatus('CANCELLED') || this.hasStatus('CANCEL_WAIT')
                    || this.hasStatus('CANCEL_REJECTED'));
        },
        
        isNewVersionAvailable: function() {
            return this.hasStatus('SENT_ACCEPTED') || this.hasStatus('SENT_REJECTED') || this.hasStatus('CANCEL_REJECTED');
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
        },
        
        isMaterialRequestCancelable: function() {
        	var version = this.get('materialRequestVersionVersion');        	
        	return (version > 1 && this.isRejected()) || (version >= 1 && this.isAccepted() || (version >= 1 && this.isCancelRejected()));        	
        },
        
        isCopyAndCreateNew: function() {
        	return this.get('materialRequestVersionStatus') && !this.hasStatus('CREATED') && !this.hasStatus('UPDATED');
		},
		
		isCancelRejected: function() {
            return this.hasStatus('CANCEL_REJECTED');
        },
		isCancelWait: function() {
            return this.hasStatus('CANCEL_WAIT');
        },
        isCancelled: function() {
            return this.hasStatus('CANCELLED');
        },
    });
	
	return MaterialRequestModel;
});