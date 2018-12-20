package jp.co.muroo.systems.bsp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

/**
 *
 */
public class JlmService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
            Log.i(JlmService.class.getName(),"ServiceHandler  init. looper is  " + looper.toString());
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            //TODO do some work.
            // For our sample, we just sleep for 5 seconds.

            Log.i(JlmService.class.getName(),"ServiceHandler.handleMessage   Thread.sleep(5000); start.");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            Log.i(JlmService.class.getName(),"ServiceHandler.handleMessage   Thread.sleep(5000); end.");

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
            Log.i(JlmService.class.getName(),"ServiceHandler.handleMessage   stopSelf done. startId is " + msg.arg1 );
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

        Log.i(JlmService.class.getName(),"onCreate new HandlerThread  - start.");

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();



        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();

        Log.i(JlmService.class.getName(),"onCreate new ServiceHandler .");
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(JlmService.class.getName()," onStartCommand - service starting.");


        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        Log.i(JlmService.class.getName()," onStartCommand - sendMessage service startId is " + startId );
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        Log.i(JlmService.class.getName()," onBind - intent .");
        return null;
    }

    @Override
    public void onDestroy() {

        Log.i(JlmService.class.getName()," onDestroy - service done.");

    }
}
