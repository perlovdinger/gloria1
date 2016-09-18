/*
 * Copyright 2009 Volvo Information Technology AB 
 * 
 * Licensed under the Volvo IT Corporate Source License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://java.volvo.com/licenses/CORPORATE-SOURCE-LICENSE 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.volvo.gloria.warehouse.b.entities;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.volvo.gloria.warehouse.d.entities.Warehouse;
import com.volvo.jvs.test.AbstractTestCase;

public class WarehouseTest extends AbstractTestCase {

    Warehouse warehouse = null;

//    private static Validator validator;

    @BeforeClass
    public static void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
    }

    @Before
    public void setUpTestData() throws Exception {
        warehouse = new Warehouse();
    }

    @Test
    public void warehouseCodeValueNotNull() {
       /* // Arrange
        // Act
        warehouse.setCountryCode("SE");
        warehouse.setCode(null);
        // Assert
        Set<ConstraintViolation<Warehouse>> constraintViolations = validator.validate(warehouse);
        assertEquals(1, constraintViolations.size());

        ConstraintViolation<Warehouse> constraintViolation = constraintViolations.iterator().next();        
        assertEquals("may not be null", constraintViolation.getMessage());

        Path path = constraintViolation.getPropertyPath();
        Node node = path.iterator().next();
        assertEquals("code", node.getName());*/
    }
    
    @Test
    public void warehouseCountryCodeTooLong() {
        /*// Arrange
        // Act
        warehouse.setCountryCode("SWEDEN");
        warehouse.setCode("201-52");
        // Assert
        Set<ConstraintViolation<Warehouse>> constraintViolations = validator.validate(warehouse);
        assertEquals(1, constraintViolations.size());

        ConstraintViolation<Warehouse> constraintViolation = constraintViolations.iterator().next();        
        assertEquals("size must be between 2 and 3", constraintViolation.getMessage());

        Path path = constraintViolation.getPropertyPath();
        Node node = path.iterator().next();
        assertEquals("countryCode", node.getName());*/
    }
}
