package com.volvo.gloria.util.c.dto.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object to represent report rows.
 * 
 */
public class ReportRow implements Serializable {

    private static final long serialVersionUID = 1512274760148690382L;

    private List<ReportColumn> reportColumns = new ArrayList<ReportColumn>();

    public List<ReportColumn> getReportColumns() {
        return reportColumns;
    }

    public void setReportColumns(List<ReportColumn> reportColumns) {
        this.reportColumns = reportColumns;
    }
}
