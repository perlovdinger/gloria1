package com.volvo.gloria.config.b.beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/*
 * This class contains duplicate code which also exists in IOUtils in the GloriaUtil project.
 * The reasoning for the duplication is to prevent the ConfigUtils project to have a dependency on 
 * any other project
 */
public final class  ConfigUtils {
    
    public static final String FILE_TYPE_XML = "xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);
    private static final String DAV = "DAV";
    
    private ConfigUtils() {
        
    }
    
    public static String[] loadFileContentsFromClasspath(String directoryPath, String fileType, String environment) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:" + environment + directoryPath + "*." + fileType);

        String[] contents = new String[resources.length];
        int index = 0;
        for (Resource resource : resources) {
            if (FILE_TYPE_XML.equalsIgnoreCase(fileType)) {
                contents[index] = ConfigUtils.getStringFromClasspath(environment + directoryPath + resource.getFilename());
            } else {
                contents[index] = resource.getFile().getAbsolutePath();
            }
            index++;
        }
        return contents;
    }
    
    public static String getFromStream(InputStream ins) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
        StringBuffer buf = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buf.append(line);
        }
        ins.close();
        return buf.toString();
    }
    
    public static String getStringFromClasspath(String name) throws IOException {
        InputStream ins = ConfigUtils.class.getClassLoader().getResourceAsStream(name);
        return getFromStream(ins);
    }
    
    public static String[] loadFileContentsFromFileSystem(String dataFolderProperty, String fileType) throws IOException {
        File warehouseDataFolderFile = new File(dataFolderProperty);
        String[] fileContents = new String[0];
        File[] listOfFiles = warehouseDataFolderFile.listFiles();
        if (listOfFiles != null && listOfFiles.length > 0) {
            fileContents = new String[listOfFiles.length];
            int index = 0;
            for (File aFile : listOfFiles) {
                LOGGER.info("Loading file: " + dataFolderProperty + aFile.getName());
                if (!isWebDav(aFile)) {
                    if (FILE_TYPE_XML.equalsIgnoreCase(fileType)) {
                        fileContents[index] = getFileContentFromFileSystem(dataFolderProperty + aFile.getName());
                    } else {
                        fileContents[index] = dataFolderProperty + File.separator + aFile.getName();
                    }
                    index++;
                }
            }
        }
        return fileContents;
    }
    
    public static boolean isWebDav(File aFile) {
        return aFile.getName() != null && aFile.getName().endsWith(DAV);
    }
    
    public static String getFileContentFromFileSystem(String fileName) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        InputStream ins = null;
        InputStreamReader streamReader = null;
        BufferedReader reader = null;
        try {
            ins = new FileInputStream(fileName);
            streamReader = new InputStreamReader(ins, "UTF-8");
            reader = new BufferedReader(streamReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } finally {
            if (ins != null) {
                ins.close();
            }
            if (streamReader != null) {
                streamReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return stringBuffer.toString();
    }

}
