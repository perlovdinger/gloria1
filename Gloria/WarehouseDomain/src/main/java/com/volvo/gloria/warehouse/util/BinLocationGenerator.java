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

import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.warehouse.c.Allocation;
import com.volvo.gloria.warehouse.c.ZoneType;
import com.volvo.gloria.warehouse.d.entities.AisleRackRow;
import com.volvo.gloria.warehouse.d.entities.BaySetting;
import com.volvo.gloria.warehouse.d.entities.BinLocation;
import com.volvo.gloria.warehouse.d.entities.Zone;

/**
 * 
 * This class generates Bin Locations for a given Aisle/Rack Row based on bays,
 * levels &\ positions.
 * 
 * 
 */
public final class BinLocationGenerator {

    private static final String SH_01_01_01 = "SH-01-01-01";
    private static final String TS_01_01_01 = "TS-01-01-01";
    private static final String BL_01_01_01 = "BL-01-01-01";
    private static final String QI_01_01_01 = "QI-01-01-01";
    static final String SEPARATOR = "-";
    private static final long DEFAULT_LEVEL_POSITION = 1;

    private BinLocationGenerator() {
    }

    public static List<BinLocation> generate(BaySetting baySetting) {
        List<BinLocation> binLocations = new ArrayList<BinLocation>();
        if (baySetting != null) {
            AisleRackRow aisleRackRow = baySetting.getAisleRackRow();
            long noOfLevels = baySetting.getNumberOfLevels();
            long noOfPositions = baySetting.getNumberOfPositions();
            for (long level = 1; level <= noOfLevels; level++) {
                for (long position = 1; position <= noOfPositions; position++) {
                    String binLocationCode = aisleRackRow.getCode() + SEPARATOR + baySetting.getBayCode() + SEPARATOR + String.format("%02d", level)
                            + SEPARATOR + String.format("%02d", position);
                    BinLocation binLocation = new BinLocation();
                    binLocation.setZone(aisleRackRow.getZone());
                    binLocation.setAllocation(Allocation.FIXED);
                    binLocation.setAisleRackRowCode(aisleRackRow.getCode());
                    binLocation.setBayCode(baySetting.getBayCode());
                    binLocation.setCode(binLocationCode);
                    binLocation.setLevel(level);
                    binLocation.setPosition(position);
                    binLocations.add(binLocation);
                }
            }
        }
        return binLocations;
    }

    public static List<BinLocation> generate(Zone zone) {
        List<BinLocation> binLocations = new ArrayList<BinLocation>();
        if (zone != null) {
            String binLocationCode = "";
            if (zone.getType().equals(ZoneType.QI)) {
                binLocationCode = QI_01_01_01;
            } else if (zone.getType().equals(ZoneType.QUARANTINE)) {
                binLocationCode = BL_01_01_01;
            } else if (zone.getType().equals(ZoneType.TO_STORE)) {
                binLocationCode = TS_01_01_01;
            } else if (zone.getType().equals(ZoneType.SHIPPING)) {
                binLocationCode = SH_01_01_01;
            }
            BinLocation binLocation = new BinLocation();
            binLocation.setZone(zone);
            binLocation.setAllocation(Allocation.FIXED);
            binLocation.setAisleRackRowCode("01");
            binLocation.setBayCode("01");
            binLocation.setCode(binLocationCode);
            binLocation.setLevel(DEFAULT_LEVEL_POSITION);
            binLocation.setPosition(DEFAULT_LEVEL_POSITION);
            binLocations.add(binLocation);
        }
        return binLocations;
    }
}
