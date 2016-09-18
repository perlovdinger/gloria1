package com.volvo.gloria.procurematerial.util;
import java.util.ArrayList;
import java.util.List;

import com.volvo.gloria.procurematerial.d.entities.Material;
import com.volvo.gloria.procurematerial.d.entities.ProcureLine;

public class PreparedMaterialGroup {

    private String groupingKey;
    private List<Material> items;
    private ProcureLine currentProcureLine;

    public PreparedMaterialGroup(String groupingKey, ProcureLine procureLine) {
        items = new ArrayList<Material>();
        this.groupingKey = groupingKey;
        this.currentProcureLine = procureLine;
    }

    public String getGroupingKey() {
        return this.groupingKey;
    }

    public void addItem(Material object) {
        this.items.add(object);
    }

    public List<Material> getItems() {
        return this.items;
    }

    public int getSize() {
        return getItems().size();
    }
    
    public ProcureLine getCurrentProcureLine() {
        return currentProcureLine;
    }
    public void setCurrentProcureLine(ProcureLine procureLine) {
        this.currentProcureLine = procureLine;
    }
}
