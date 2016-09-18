package com.volvo.gloria.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * DTO for file export.
 */
public class FileToExportDTO implements Serializable {

    private static final long serialVersionUID = 7091480872125794117L;

    private String name;

    private byte[] content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        if (content != null) {
            this.content = Arrays.copyOf(content, content.length);
        }
    }
}
