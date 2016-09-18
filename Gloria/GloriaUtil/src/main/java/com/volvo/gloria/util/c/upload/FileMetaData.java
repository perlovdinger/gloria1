package com.volvo.gloria.util.c.upload;


/**
 * File upload support.
 */
public class FileMetaData {
    private String name;
    private long id;
    private long size;
    private String url;

    public FileMetaData(long id, String filename, long size, String url) {
        this.id = id;
        this.name = filename;
        this.size = size;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
