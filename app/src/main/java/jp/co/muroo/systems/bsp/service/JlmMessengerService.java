package jp.co.muroo.systems.bsp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

/**
 * Messenger インターフェースを使用するサービスの簡単です。
 */
public class JlmMessengerService extends Service {

    public JlmMessengerService() {
    }

    /** Command to the service to display a message */
    static final int MSG_SAY_HELLO = 1;

    //客户端请求用  Handler 实现内部类定义（服务的机能。）
    class IncomingHandler extends Handler {

        //用于接收客户端信息
        @Override
        public void handleMessage(Message msg) {
            //TODO Service's doing・・・

            //-------------- MessengerService 5  --------
            Log.i(JlmMessengerService.class.getName(),"IncomingHandler  handleMessage ");

            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "JLM hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    //利用Handler的实现类 为参数 做成Messenger对象
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        //------------------ MessengerService 2  --------
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        Log.i(JlmMessengerService.class.getName(),"IBinder onBind(Intent intent)");
        return mMessenger.getBinder();
    }

}
