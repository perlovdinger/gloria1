package com.volvo.gloria.procurematerial.d.entities.status.materialLine;

import com.volvo.gloria.util.GloriaApplicationException;

public class RemovedBorrowed extends MaterialLineDefaultOperation {

    @Override
    public boolean isRevertable() throws GloriaApplicationException {
        return false;
    }

}
