package jp.co.muroo.systems.bsp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * IntentService を使用するサービスの簡単です。
 */
public class JlmIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "jp.co.muroo.systems.bsp.service.action.FOO";
    private static final String ACTION_BAZ = "jp.co.muroo.systems.bsp.service.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "jp.co.muroo.systems.bsp.service.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "jp.co.muroo.systems.bsp.service.extra.PARAM2";

    public JlmIntentService() {
        super("JlmIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, JlmIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, JlmIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            } else {
                try {
                    Log.i(JlmIntentService.class.getName()," onHandleIntent - Thread.sleep(5000) start.");
                    Thread.sleep(5000);
                    Log.i(JlmIntentService.class.getName()," onHandleIntent - Thread.sleep(5000) end.");
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        Log.i("handleActionFoo","param is: " + param1 + "-1-2-Sart-"+param2);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("handleActionFoo","param is: " + param1 + "-1-2-end-"+param2);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        Log.i("handleActionBaz","param is: " + param1 + "-1-2-Start-"+param2);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("handleActionBaz","param is: " + param1 + "-1-2-end-"+param2);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(JlmIntentService.class.getName(),"onStartCommand is done! --      startId is " + startId );
        return super.onStartCommand(intent,flags,startId);
    }
}
