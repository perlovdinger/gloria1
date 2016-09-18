package com.volvo.gloria.procurematerial.d.type.directsend;


/**
 * Enum class for Direct send.
 */
public enum DirectSendType implements DirectSendTypeOperations{
    NO(new No()), 
    YES_TRANSFER(new YesTransfer()), 
    YES_REQUESTED(new YesRequested());

    
    private final DirectSendTypeOperations directSendTypeOperations;

    DirectSendType(DirectSendTypeOperations directSendTypeOperations) {
        this.directSendTypeOperations = directSendTypeOperations;
    }
    
    @Override
    public boolean isDirectSend() {
        return directSendTypeOperations.isDirectSend();
    }
}
