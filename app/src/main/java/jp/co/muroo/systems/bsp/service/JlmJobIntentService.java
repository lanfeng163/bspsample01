package jp.co.muroo.systems.bsp.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * JobIntentService を使用するサービスの簡単です。
 */
public class JlmJobIntentService extends JobIntentService {

    /**
     * Service id
     */
    static final int JOB_ID = 10111;

    /**
     * Convenience method for enqueuing work in to this service. myself method.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, JlmJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork( Intent intent) {
        Log.d("JlmJobIntentService", "onHandleWork: "+intent.getStringExtra("work").toString());

        /*
         * Creates a new Intent containing a Uri object
         * BROADCAST_ACTION is a custom Intent action
         */
        //To send the status of a work request in an JobIntentService to other components
        Intent localIntent = new Intent("jp.co.muroo.systems.bsp.activity.MainActivity");
                        // Puts the status into the Intent
        localIntent.putExtra("ServiceStatus", "status is jlm00888888888888888888888888888888888");
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }
}
