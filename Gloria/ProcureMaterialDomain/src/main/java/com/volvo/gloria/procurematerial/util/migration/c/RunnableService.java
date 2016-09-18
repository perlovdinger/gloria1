package com.volvo.gloria.procurematerial.util.migration.c;

/**
 * Runnable abstract service.
 */
public abstract class RunnableService implements Runnable{
    
    private static int count = 0;
    
    public void run() {
        execute();
    }
    
    /**
     * Actual execution logic goes here.
     */
    public abstract void execute();
    
    protected static synchronized void incrementCount() {
        count = count + 1;
    }
    
    /**
     * get total number of execution. 
     * @return
     */
    public static int getCount() {
        return count;
    }
    
    /**
     * reset count.
     */
    public static void resetCount() {
        count = 0;
    }

}
