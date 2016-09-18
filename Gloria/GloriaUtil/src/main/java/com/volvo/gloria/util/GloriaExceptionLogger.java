package com.volvo.gloria.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  class for Gloria ExceptionLogger.
 */
public final class GloriaExceptionLogger {

    public static final int MINIMUM_NUMBER = 100;
    private GloriaExceptionLogger() {
        super();
    }

    public static void log(Exception exception, Class<?> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        for (String errorLine : createLog(exception, clazz)) {
            logger.error(errorLine);
        }
    }

    public static List<String> createLog(Exception exception, Class<?> clazz) {
        List<String> errorLines = new ArrayList<String>();
        logExceptionStart(exception, errorLines, clazz);
        logExceptionEnd(exception, errorLines);
        return errorLines;
    }

    private static void logExceptionStart(Exception exception, List<String> errorLines, Class<?> clazz) {
        errorLines.add("Exception start *********************************************************************************");
        errorLines.add("cause-> " + exception.getClass().getSimpleName());
        errorLines.add("class-> " + clazz.getSimpleName());
        errorLines.add("detail message-> " + exception.getMessage());
    }

    private static void logExceptionEnd(Exception exception, List<String> errorLines) {
        errorLines.add("StackTrace start .....................................");
        StackTraceElement[] stackLines = null;
        if (exception.getCause() != null) {
            stackLines = exception.getCause().getStackTrace();
        } else {
            stackLines = exception.getStackTrace();
        }

        for (int i = 0; i < Math.min(MINIMUM_NUMBER, stackLines.length); i++) {
            errorLines.add(stackLines[i].toString());
        }
        errorLines.add("StackTrace end .....................................");
        errorLines.add("Exception end  **********************************************************************************");
    }
}
