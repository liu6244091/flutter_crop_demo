package com.example.flutter_crop_demo.thread;

import android.util.Log;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理
 */
public class ThreadPoolManager {

    private volatile static ThreadPoolManager threadPoolManager;

    public static ThreadPoolManager getInstance() {
        synchronized (ThreadPoolManager.class) {
            if (null == threadPoolManager) {
                threadPoolManager = new ThreadPoolManager();
            }
        }
        return threadPoolManager;
    }


    //核心线程数
    private int corePoolSize = 1;

    //最大线程数
    private int maximumPoolSize = 4;

    private ThreadPoolExecutor threadPoolExecutor;

    private ThreadPoolManager() {

        //核心线程数根据手机CPU核数来定
        corePoolSize = Runtime.getRuntime().availableProcessors() + 1;

        //最大线程数是核心线程数x2 +1
        maximumPoolSize = corePoolSize*2 +1;

        Log.d("ThreadPoolManager", "corePoolSize:"+corePoolSize + "  maximumPoolSize:"+maximumPoolSize);
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 5, TimeUnit.SECONDS, new LinkedBlockingDeque<>(6));
    }

    /**
     * 执行任务
     * @param runnable
     */
    public void execute(Runnable runnable){
        threadPoolExecutor.execute(runnable);
    }
}
