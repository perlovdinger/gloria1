package com.volvo.gloria.common.util;

import java.io.IOException;
import java.io.InputStream;

import com.volvo.gloria.util.IOUtil;

public abstract class InitDataNonoperative  {
    private static final String XML = "xml";
    protected InputStream[] getInputStreams(String costCenterPath, String env, String fileType) throws IOException {
        InputStream[] streams = null;
        try {
            streams = IOUtil.loadInputStreamFromClasspath(costCenterPath, fileType, env);

        } catch (IOException e) {
            streams = IOUtil.loadInputStreamFromClasspath(costCenterPath, fileType, "initDB/_global");
        }
        return streams;
    }
    protected String[] getFiles(String wbsPath, String env) throws IOException {
        String[] xmlFiles = null;
        try {
            xmlFiles = IOUtil.loadFileContentsFromClasspath(wbsPath, XML, env);
        } catch (IOException e) {
            xmlFiles = IOUtil.loadFileContentsFromClasspath(wbsPath, XML, "initDB/_global");
        }
        return xmlFiles;

    }

}
