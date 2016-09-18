package com.volvo.gloria.util.c;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Utility to create COMMA separated string of values.
 * 
 */
public class UniqueItems extends TreeSet<String> {
    private static final String COMMA = ",";
    private static final long serialVersionUID = 5175953621592592144L;

    @Override
    public boolean add(String e) {
        if (e != null) {
            return super.add(e);
        }
        return true;
    }

    public String createCommaSeparatedKey() {
        Iterator<String> setIterator = iterator();

        StringBuilder sb = new StringBuilder();
        while (setIterator.hasNext()) {
            String setElement = setIterator.next();
            if (setElement != null) {
                sb.append(setElement);

            }
            if (setIterator.hasNext()) {
                sb.append(COMMA);
            }
        }
        return sb.toString();
    }
}
