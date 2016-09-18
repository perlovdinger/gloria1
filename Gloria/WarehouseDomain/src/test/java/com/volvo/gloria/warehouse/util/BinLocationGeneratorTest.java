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
package com.volvo.gloria.warehouse.util;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.AisleRackRow;
import com.volvo.gloria.warehouse.d.entities.BaySetting;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.StorageRoom;
import com.volvo.gloria.warehouse.d.entities.Zone;

public class BinLocationGeneratorTest extends TestCase {

    public void testGenerateBinLocationsForZone() {
        // Arrange
        StorageRoom storageRoom = new StorageRoom();
        storageRoom.setCode("S");
        
        Zone zone = new Zone();
        zone.setType(ZoneType.QI);
        zone.setCode("A");
        zone.setStorageRoom(storageRoom);

        // Act
        List<BinLocation> binLocations = BinLocationGenerator.generate(zone);
        // Assert
        Assert.assertNotNull(binLocations);
        Assert.assertEquals(1, binLocations.size());
        Assert.assertEquals("QI-01-01-01", binLocations.get(0).getCode());
    }

    public void testGenerateBinLocationsForAisleBaySetting() {
        // Arrange
        StorageRoom storageRoom = new StorageRoom();
        storageRoom.setCode("S");
        Zone zon = new Zone();
        zon.setCode("B");
        zon.setStorageRoom(storageRoom);
        
        AisleRackRow aisleRackRow = new AisleRackRow();
        aisleRackRow.setZone(zon);
        aisleRackRow.setCode("11");

        BaySetting baySetting = new BaySetting();
        baySetting.setAisleRackRow(aisleRackRow);
        baySetting.setBayCode("33");
        baySetting.setNumberOfLevels(1);
        baySetting.setNumberOfPositions(23);
        // Act
        List<BinLocation> binLocations = BinLocationGenerator.generate(baySetting);
        // Assert
        Assert.assertNotNull(binLocations);
        Assert.assertEquals(23, binLocations.size());
        Assert.assertEquals("11-33-01-01", binLocations.get(0).getCode());
        Assert.assertEquals("11-33-01-23", binLocations.get(binLocations.size() - 1).getCode());
    }
}
