package jp.co.muroo.systems.bsp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

/**
 * Binder クラスを拡張する
 * サービスがローカルのアプリケーションでのみ使用します。
 */
public class JlmLocalService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    public JlmLocalService() {
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        public JlmLocalService getService() {

            Log.i("JlmLocalService", "test JlmLocalService getService");

            // Return this instance of LocalService so clients can call public methods
            return JlmLocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("JlmLocalService", "test JlmLocalService onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        Log.i("JlmLocalService", "test JlmLocalService onUnbind");
        return false;
    }

    /** method for clients */
    public int getRandomNumber() {
        Log.i("JlmLocalService", "test JlmLocalService getRandomNumber");
        return mGenerator.nextInt(100);
    }
}
