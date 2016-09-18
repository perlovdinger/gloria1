package com.volvo.gloria.util;

import java.io.Serializable;
import java.util.Arrays;

/**
 * DTO for document uploads.
 * 
 */
public class DocumentDTO implements Serializable {

    private static final long serialVersionUID = 4333951988550412704L;

    private long id;
    private String name;
    private byte[] content;
    private String categoryType;
    private long size;
    private String url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getURL() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DocumentDTO [id=" + id + ", name=" + name + ", content=" + Arrays.toString(content) + ", categoryType=" + categoryType + ", size=" + size
                + ", url=" + url + "]";
    }
}
