package com.volvo.gloria.procurematerial.d.entities.status.requestline;

import com.volvo.gloria.procurematerial.d.entities.Material;

/**
 * Operations for START.
 */
public class Start extends MaterialStatusDefaultOperations {
    @Override
    public void init(boolean procureLineExists, Material material) {
        if (procureLineExists) {
            material.setStatus(MaterialStatus.ADD_NOT_ACCEPTED);
        } else {
            material.setStatus(MaterialStatus.ADDED);
        }
    }

}
