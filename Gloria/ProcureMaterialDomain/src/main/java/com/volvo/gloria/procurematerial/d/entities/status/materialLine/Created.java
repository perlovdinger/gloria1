package com.volvo.gloria.procurematerial.d.entities.status.materialLine;



/**
 * Initial state.
 */
public class Created extends MaterialLineDefaultOperation {

    @Override
    public boolean isRevertable() {
        return true;
    }
}
