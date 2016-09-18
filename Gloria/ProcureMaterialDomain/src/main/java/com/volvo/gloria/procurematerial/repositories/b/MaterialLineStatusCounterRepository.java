package com.volvo.gloria.procurematerial.repositories.b;

import java.util.Date;
import java.util.List;

import com.volvo.gloria.procurematerial.d.entities.MaterialLine;
import com.volvo.gloria.procurematerial.d.entities.MaterialLineStatusCounter;
import com.volvo.gloria.procurematerial.d.entities.status.materialLine.MaterialLineStatusCounterType;

/**
 * Repository.
 * 
 * Specific to MaterialLineStatusCounter
 * 
 */
public interface MaterialLineStatusCounterRepository {

    MaterialLineStatusCounter save(MaterialLineStatusCounter instanceToSave);

    MaterialLineStatusCounter findById(long id);

    List<MaterialLineStatusCounter> findAll();

    List<MaterialLineStatusCounter> getCount(Date fromDate, Date toDate, String[] project, 
                                             String[] warehouse, List<MaterialLineStatusCounterType> materialLineStatusCounterTypes);

    MaterialLineStatusCounter createAndSave(MaterialLine materialLine, MaterialLineStatusCounterType materialLineStatusCounterType);
}
