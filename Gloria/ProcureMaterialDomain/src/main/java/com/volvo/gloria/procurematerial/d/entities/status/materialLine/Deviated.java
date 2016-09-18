package com.volvo.gloria.procurematerial.d.entities.status.materialLine;


public class Deviated extends MaterialLineDefaultOperation {
    
    @Override
    public boolean isPicked() {
        return true;
    }
}
