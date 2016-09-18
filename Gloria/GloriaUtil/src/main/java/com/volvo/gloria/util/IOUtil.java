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
package com.volvo.gloria.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Utility class for IO operations.
 */

/*
 * This class contains duplicate code which also exists in ConfigUtils.java in the GloriaConfig project. The reasoning for the duplication is to prevent the
 * ConfigUtils project to have a dependency on any other project
 */
public abstract class IOUtil {

    public static final String FILE_TYPE_XML = "xml";
    public static final String FILE_TYPE_EXCEL_OLD = "xls";
    public static final String FILE_TYPE_EXCEL_NEW = "xlsx";
    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtil.class);

    private IOUtil() {

    }

    public static String getStringFromClasspath(String name) throws IOException {
        InputStream ins = IOUtil.class.getClassLoader().getResourceAsStream(name);
        if (ins == null) {
            return null;
        } else {
            return getFromStream(ins);
        }
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

    public static String getStringFromFileSystem(String fileName) throws IOException {
        return getStringFromFileSystem(fileName, "UTF-8");
    }

    public static String getStringFromFileSystem(String fileName, String charset) throws IOException {
        StringBuffer buf;
        InputStream ins = null;
        InputStreamReader streamReader = null;
        BufferedReader reader = null;
        try {
            ins = new FileInputStream(fileName);
            streamReader = new InputStreamReader(ins, charset);
            reader = new BufferedReader(streamReader);
            buf = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
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
        return buf.toString();
    }

    public static boolean isExcelType(String fileType) {
        if (fileType != null && (FILE_TYPE_EXCEL_NEW.equalsIgnoreCase(fileType) || FILE_TYPE_EXCEL_OLD.equalsIgnoreCase(fileType))) {
            return true;
        }
        return false;
    }

    public static String[] loadFileContentsFromClasspath(String directoryPath, String fileType, String environment) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String path = "classpath:" + environment + directoryPath + "*." + fileType;
        LOGGER.info("path: " + path);
        Resource[] resources = resolver.getResources("classpath:" + environment + directoryPath + "*." + fileType);

        String[] contents = new String[resources.length];
        int index = 0;
        for (Resource resource : resources) {
            if (FILE_TYPE_XML.equalsIgnoreCase(fileType)) {
                contents[index] = IOUtil.getStringFromClasspath(environment + directoryPath + resource.getFilename());
            } else {
                contents[index] = resource.getFile().getAbsolutePath();
            }
            index++;
        }
        return contents;
    }

    public static InputStream[] loadInputStreamFromClasspath(String directoryPath, String fileType, String environment) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String path = "classpath:" + environment + directoryPath + "*." + fileType;
        LOGGER.info("path: " + path);
        Resource[] resources = resolver.getResources("classpath:" + environment + directoryPath + "*." + fileType);

        InputStream[] contents = new InputStream[resources.length];
        int index = 0;
        for (Resource resource : resources) {
            contents[index] = resource.getInputStream();
            index++;
        }
        return contents;
    }

    public static InputStream[] loadInputStreamFromClasspath(String path) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        LOGGER.info("path: " + path);
        Resource[] resources = resolver.getResources("classpath:" + path);

        InputStream[] contents = new InputStream[resources.length];
        int index = 0;
        for (Resource resource : resources) {
            contents[index] = resource.getInputStream();
            index++;
        }
        return contents;
    }

}
