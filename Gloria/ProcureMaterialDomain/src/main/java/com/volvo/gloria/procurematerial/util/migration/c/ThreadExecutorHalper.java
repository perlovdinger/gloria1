package com.volvo.gloria.procurematerial.util.migration.c;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for ExecutorHalper.
 */
public class ThreadExecutorHalper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadExecutorHalper.class);

    private ExecutorService executor = null;
    private int completed = 0;
    private long startTime;
    private int totalCount;

    public ThreadExecutorHalper(int threadPoolSize, int totalCount) {
        this.totalCount = totalCount;
        executor = Executors.newFixedThreadPool(threadPoolSize);
        startTime = System.currentTimeMillis();
    }

    public void execute(RunnableService runnable) {
        executor.execute(runnable);
    }

    public void awaitTermination() {
        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            checkProgress();
        }
    }

    private void checkProgress() {
        int doneCount = RunnableService.getCount();
        if (totalCount != 0 && completed < (doneCount * 100 / totalCount) && (completed = doneCount * 100 / totalCount) % 4 == 0 && completed < 100) {
            float elapsedTime = ((long) System.currentTimeMillis() - startTime) / 1000f;
            float remainingTime = (elapsedTime * 100 / completed) - elapsedTime;
            if (remainingTime > 60) {
                LOGGER.info(completed + "%-" + Math.round(remainingTime / 60f) + "m.");
            } else {
                LOGGER.info(completed + "%-" + Math.round(remainingTime) + "s.");
            }
        }
    }

}
