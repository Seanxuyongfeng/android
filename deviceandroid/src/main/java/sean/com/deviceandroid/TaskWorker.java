package sean.com.deviceandroid;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Sean on 2016/5/16.
 */
public class TaskWorker {

    private static ExecutorService mService = Executors.newFixedThreadPool(3, new MyThreadFactory());

    public static void post(Runnable task){
        mService.submit(task);
    }

    public static ExecutorService getWorker(){
        return mService;
    }

    private static class MyThreadFactory implements ThreadFactory{
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            return t;
        }
    }
}
