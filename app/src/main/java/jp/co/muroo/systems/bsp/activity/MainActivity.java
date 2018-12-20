package jp.co.muroo.systems.bsp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.printerhelper.utils.AidlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;
import jp.co.muroo.systems.bsp.comm.CommAlertDialogBuilder;
import jp.co.muroo.systems.bsp.comm.CommUtil;
import jp.co.muroo.systems.bsp.service.JlmIntentService;
import jp.co.muroo.systems.bsp.service.JlmJobIntentService;
import jp.co.muroo.systems.bsp.service.JlmLocalService;
import jp.co.muroo.systems.bsp.service.JlmMessengerService;
import jp.co.muroo.systems.bsp.service.JlmService;

import static java.security.AccessController.getContext;

/**
 * ログインActivity
 */
public class MainActivity extends Activity {

    public MspApplication mspApp = null;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_0 = 0;

    EditText userIdText = null;
    EditText passwordText =  null;
    TextView deviceIdView = null;
    TextView messageView = null;

    CommUtil commUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle(R.string.head_title_name1);//タイトルバーの文字列

        userIdText =  findViewById(R.id.txtUserId);
        passwordText =  findViewById(R.id.txtPassWord);
        deviceIdView = findViewById(R.id.txtUUID);
        messageView = findViewById(R.id.textMessage);

        mspApp = (MspApplication) this.getApplication();

        commUtil = CommUtil.getInstance();

        this.doInit();

        //test ===③===　JlmJobIntentService 服务 和客户端 交互信息  Start
        /*
        IntentFilter statusIntentFilter = new IntentFilter("jp.co.muroo.systems.bsp.activity.MainActivity");
        // Instantiates a new Receiver
        JlmStateReceiver stateReceiver =  new JlmStateReceiver();
        // Registers the JlmStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(stateReceiver, statusIntentFilter);
        */
        //test ===③===　JlmJobIntentService End
    }

    /**
     * Called when the 'loadDeviceId' function is triggered.
     */
    public void loadDeviceId() throws SecurityException {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission();
        } else {
            // READ_PHONE_STATE permission is already been granted.
            doPermissionGrantedStuffs();
        }
    }

    /**
     * Requests the READ_PHONE_STATE permission.
     * If the permission has been denied previously, a dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission Request")
                    .setMessage(getString(R.string.permission_read_phone_state_rationale))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_0);
                        }
                    })
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_0);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_0) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doPermissionGrantedStuffs();
            } else {
                alertAlert(getString(R.string.permissions_not_granted_read_phone_state));
            }
        }
    }
    private void alertAlert(String msg) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })
             //   .setIcon(R.drawable.onlinlinew_warning_sign)
                .show();
    }

    /**
     * シリアル番号を取得すます。(TelephonyManagerから取得します)「SIMSerialNumber」
     * @throws SecurityException
     */
    public void doPermissionGrantedStuffs() throws SecurityException {
        //Have an  object of TelephonyManager
        TelephonyManager tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        /*
        //Get Subscriber ID
        String subscriberID=tm.getDeviceId();

        //Get SIM Serial Number
        String SIMSerialNumber=tm.getSimSerialNumber();

        //Get Network Country ISO Code
        String networkCountryISO=tm.getNetworkCountryIso();

        //Get SIM Country ISO Code
        String SIMCountryISO=tm.getSimCountryIso();

        //Get the device software version
        String softwareVersion=tm.getDeviceSoftwareVersion();

        //Get the Voice mail number
        String voiceMailNumber=tm.getVoiceMailNumber();
        */

        //Get SIM Serial Number
        String SIMSerialNumber=tm.getSimSerialNumber();
        if (SIMSerialNumber == null || "".equals(SIMSerialNumber)) {
            SIMSerialNumber = getSerialNumber();
        }

        mspApp.setDeviceId(SIMSerialNumber);
        deviceIdView.setText(SIMSerialNumber);
    }

    /**
     * シリアル番号を取得すます。(システムプロパティから取得します)「ro.serialno」
     * @return
     */
    public String getSerialNumber() {

        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");//OK got
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }

    /**
     * ログイン処理
     * @param view
     */
    public void login(View view) {

        Editable userIdEdi  = userIdText.getText();
        Editable passwordEdi  = passwordText.getText();

        //ユーザーIDが入力していない。
        if ( TextUtils.isEmpty(userIdEdi) ) {
            AlertDialog.Builder builder = CommAlertDialogBuilder.SetDialog(1, R.string.msg0001,this);
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            builder.setPositiveButton(R.string.msg_return1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText txtUserId =  findViewById(R.id.txtUserId);
                            txtUserId.setFocusable(true);
                            txtUserId.setFocusableInTouchMode(true);
                            txtUserId.requestFocus();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            mspApp.setUserId(null);
            return;
        }

        //パースワードが入力していない。
        if ( TextUtils.isEmpty(passwordEdi) ) {
            AlertDialog.Builder builder = CommAlertDialogBuilder.SetDialog(1, R.string.msg0002, this);
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            builder.setPositiveButton(R.string.msg_return1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText txtPassWord =  findViewById(R.id.txtPassWord);
                            txtPassWord.setFocusable(true);
                            txtPassWord.setFocusableInTouchMode(true);
                            txtPassWord.requestFocus();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            mspApp.setUserId(null);
            return;
        }

        String userId = userIdEdi.toString();
        String password = passwordEdi.toString();

        //MSP WebAPIに送信処理
        mspApp.setUserId(userId);
        mspApp.setDigitalSignature(commUtil.getDigitalSignatureStr());

        //Jsonを作成
        JSONObject json = this.getLoginJson(mspApp, password);
        Log.i("MainActivity","送信Json is: " + json.toString());

        //ログイン送信は非同期で処理します
        new DoLogin().execute(json);

        messageView.setText(R.string.msg0012);
        //フォーカスを設定
        this.setFocus(userIdText);

    }

    //フォーカスを設定
    private void setFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    /**
     * JSONデータを作成
     * @return
     */
    private JSONObject getLoginJson(MspApplication mspApp, String passwordStr) {

        JSONObject json = new JSONObject();
        try {
            json.put("terminalId", mspApp.getDeviceId());
            json.put("userId", mspApp.getUserId());
            json.put("loginPassword", passwordStr);
            json.put("UserKey", mspApp.getDigitalSignature());
        } catch (JSONException e) {
            Log.d("MainActivity", e.getLocalizedMessage());
        }
        return json;
    }

    /**
     * クリアボタンの処理
     * @param view
     */
    public void clear(View view) {
        this.doInit();
    }

    /**
     * 初期化
     */
    private void doInit() {

        userIdText.setText("user01");
        passwordText.setText("abc123ABC");
        messageView.setText(getString(R.string.app_info));

        mspApp.setUserId("");
        mspApp.setDigitalSignature("");

        mspApp.setShopName("");
        mspApp.setShopInfo("");
        mspApp.setShopTel("");
        mspApp.setToken("");

        try {
            //デバイスIDを取得します。
            loadDeviceId();

            /* Fading Transition Effect */
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } catch (Exception ex) {

            deviceIdView.setText("Get DeviceID is failed!");
            Log.e("MainActivity", ex.getLocalizedMessage());
        }
    }

    //########### 内部クラス 「非同期処理」  ##################
    // AsyncTask that configures the scanned data on background
    // thread and updated the result on UI thread with scanned data and type of label
    //その際、AsyncTaskにジェネリクスを3個指定する必要があります。
    // これは、AsyncTaskを継承したクラス内のメソッドの引数や戻り値の型を指定するためです。
    //ログイン送信クラス
    private class DoLogin extends AsyncTask<JSONObject, String, String> {
        /**
         * 決済送信
         * @param params
         * @return
         */
        @Override
        public String doInBackground(JSONObject... params) {
            String result;
            //POSTで　WebAPIに決済送信
            result = commUtil.doPost(getString(R.string.msp_login_url), params[0], MainActivity.class.getName());
            return result;
        }

        /**
         * 結果により、エラー情報を表示　OR　結果画面に遷移
         * @param result
         */
        @Override
        public void onPostExecute(String result) {
            String resultMessage = null;
            try {
                if (result == null || "JAVAERROR".equals(result)) {
                    resultMessage = getString(R.string.msg0015);
                    //失敗
                    mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
                } else if ("NOT200ERROR".equals(result)) {
                    resultMessage = getString(R.string.msg0024);
                    //失敗
                    mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
                } else {
                    JSONObject rootJSON = new JSONObject(result);

                    //BSP WebAPI　から受信結果を処理
                    resultMessage = setMspResult(rootJSON);
                }
            } catch (JSONException ex) {
                Log.d(MainActivity.class.getName(), ex.getLocalizedMessage());
                resultMessage = getString(R.string.msg0016);
                //失敗
                mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
            }
            //処理が完了
            if (mspApp.getResultKbn() != null && !("99").equals(mspApp.getResultKbn())) {
                //結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
                //結果画面に遷移します。
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            } else {
                //メッセージをセット
                messageView.setText(resultMessage);
            }
        }

        /**
         * 受信結果をセット
         * @param jsonResult
         * @return
         */
        private String setMspResult(JSONObject jsonResult) throws JSONException {
            String resultMessage = "";//最終結果情報
            //jsonResult から　code の文字を取得
            String resultCode = jsonResult.getString("ResultCode");
            resultMessage = jsonResult.getString("Message");

            if ("01".equals(resultCode)) {
                //成功
                String tokenStr = jsonResult.getString("Token");
                String shopNameStr = jsonResult.getString("ShopName");
                String shopInfo = jsonResult.getString("ShopAddress");
                String shopTel = jsonResult.getString("ShopTel");
                String serverKeyStr = jsonResult.getString("ServerKey");
                //    String processDateTimeStr = rootJSON.getString("ProcessDateTime");

                //基本データをセット存する
                mspApp.setShopName(shopNameStr);
                mspApp.setShopInfo(shopInfo);
                mspApp.setShopTel(shopTel);
                mspApp.setToken(tokenStr);
                mspApp.setServerDigitalSignature(serverKeyStr);

                if (!commUtil.checkDigitalSignature(mspApp.getServerDigitalSignature())) {
                    //デジタル署名　チェックします
                    mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
                    resultMessage = getString(R.string.msg0019);
                } else {
                    mspApp.setResultKbn("00");//WebAPI処理 正常
                }
            } else {
                //WebAPI処理　失敗
                mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
            }
            return resultMessage;
        }
    }

    //########### 内部クラス End  ##################




    //TODO Sunmiプリンターテストの処理 TESTTEST
    /**
     *
     * Sunmiプリンター　テスト と　Service開始します
     * @param view
     */
    public void doServiceStart(View view) {

        //Sunmiプリンターテストの処理 test
        /*
        if (AidlUtil.getInstance().isConnect()) {
            Log.i("MspApplication","connectPrinterService is OK!");
            String content = "Test Sunmi Printer";
            float size = 12;
            AidlUtil.getInstance().printText(content, size, false, false);
            Log.i("MspApplication","Print test seeded!");
        } else {
            Log.i("MspApplication","connectPrinterService is NOT OK!");
        }
        */
        //Sunmiプリンターテストの処理 End


        //test ===①===　JLMIntentService Start
/*
        //特別のサービス
        JlmIntentService.startActionBaz(this,"bazp1" ,"baz2");

        //特別のサービス2
        Intent intent = new Intent(this, JlmIntentService.class);
        intent.setAction("jp.co.muroo.systems.bsp.service.action.FOO");
        intent.putExtra("jp.co.muroo.systems.bsp.service.extra.PARAM1", "p11");
        intent.putExtra("jp.co.muroo.systems.bsp.service.extra.PARAM2", "p22");
        this.startService(intent);

        //デフォルトのサービス
        Intent intent1 = new Intent(this, JlmIntentService.class);
        startService(intent1);
*/
        //test JLMIntentService End


        //test　===②=== JLMService Start
        /*
        Intent intent11 = new Intent(this, JlmService.class);
        startService(intent11);


        Intent intent12 = new Intent(this, JlmService.class);
        startService(intent12);
*/
        //test JLMService End

        //test ===③===　JlmJobIntentService 服务 和客户端 交互信息  Start

        /*
        Intent workIntent = new Intent();
        workIntent.putExtra("work","work num:JLM001");
        JlmJobIntentService.enqueueWork(this, JlmJobIntentService.class, 10111, workIntent);

        Intent workIntent1 = new Intent();
        workIntent1.putExtra("work","work num:JLM002");
        JlmJobIntentService.enqueueWork(this,workIntent1);

*/
        //test JlmJobIntentService End


        // test ===④=== JlmLocalService start
        /*
        if (mBound) {

            Log.i("use JlmLocalService", "test unbindService");
            unbindService(mConnection);
            mBound = false;
        } else {

            Log.i("use JlmLocalService", "test bindService");
            Intent intent = new Intent(this, JlmLocalService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        */
        // test the JlmLocalService end

        //test  ===⑤===　JlmMessengerService Start
        /*
        if (mBound) {
            //------------------ MessengerService 1  --------
            unbindService(mConnection);
            mBound = false;
        } else {
            // Bind to the service
            bindService(new Intent(this, JlmMessengerService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
        }
        */
        //test  ===⑤===　JlmMessengerService End

    }



    //test print sunmi
    /*
    private void setTestPrintInfo() {

        mspApp.setUserId("User00112");
        mspApp.setShopName("ムロオ東京");
        mspApp.setShopInfo("東京都中央区日本橋８－８－８タカコービル四階");
        mspApp.setShopTel("03-6892-0550");
        mspApp.setResultKbn("11");
        mspApp.setPayCompany("WeChat Pay");
        mspApp.setPayAmount(2580);
        mspApp.setPayOrderId("12201812190123456");
        mspApp.setPayProcessDateTime("2018-12-19 17:12:34");
    }

    private void doPrint() {

        //Sunmiプリンターテストの処理 test
        if (AidlUtil.getInstance().isConnect()) {
            Log.i(DetailActivity.class.getName(),"doPrint() connectPrinterService is OK!");

            //    AidlUtil.getInstance().print1Line();
            AidlUtil.getInstance().printText(mspApp.getShopName(), 48, 1,true, false);
            AidlUtil.getInstance().printText(mspApp.getShopInfo(), 28, 0,false, false);
            AidlUtil.getInstance().printText("電話："+ mspApp.getShopTel(), 26, 0,false, false);
            AidlUtil.getInstance().printText("処理時刻：" + mspApp.getPayProcessDateTime(), 24,  0,false, false);

            //結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
            String rKbnStr = "処理状態：";
            if ("11".equals(mspApp.getResultKbn())) {
                AidlUtil.getInstance().printText(rKbnStr+"支払成功", 24,  0,false, false);
            } else if ("21".equals(mspApp.getResultKbn())) {
                AidlUtil.getInstance().printText(rKbnStr+"返金成功", 24,  0,false, false);
            } else if ("12".equals(mspApp.getResultKbn())) {
                AidlUtil.getInstance().printText(rKbnStr+"支払詳細", 24,  0,false, false);
            } else if ("22".equals(mspApp.getResultKbn())) {
                AidlUtil.getInstance().printText(rKbnStr+"返金詳細", 24,  0,false, false);
            }
            AidlUtil.getInstance().printText("決済会社：" + mspApp.getPayCompany(), 24,  0,false, false);
            String amountStr = "¥" + String.valueOf(mspApp.getPayAmount())+"円";
            AidlUtil.getInstance().printText("処理金額：" + amountStr, 28,  0,false, false);

            AidlUtil.getInstance().printText("--------------------------------", 24, 1,false, false);
            AidlUtil.getInstance().printText("担当番号：" + mspApp.getUserId(), 24,  0,false, false);
            AidlUtil.getInstance().printText("取引番号：" + mspApp.getPayOrderId(), 24,  0,false, false);
            AidlUtil.getInstance().printText("処理端末：" + mspApp.getDeviceId(), 24,  0,false, false);
            AidlUtil.getInstance().printText("--------------------------------", 24, 1,false, false);

            AidlUtil.getInstance().printQr(mspApp.getPayOrderId(), 6,3);//QRコード
            AidlUtil.getInstance().printText("--------------------------------", 24, 1,false, false);
            AidlUtil.getInstance().print2Line();
            AidlUtil.getInstance().print2Line();

            Log.i(DetailActivity.class.getName(),"Print  seeded!");
        } else {
            Log.i(DetailActivity.class.getName(),"doPrint() connectPrinterService is  NOT OK!");
        }
        //Sunmiプリンターテストの処理 End
    }
*/
    //test print sunmi

    /**
     * サービスを実行します。
     * @param view
     */
    public void doServiceTest(View view) {

        //test print sunmi
//        setTestPrintInfo();
//        doPrint();
        //test print sunmi

        // test ===④=== JlmLocalService start
        /*
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            int num = mService.getRandomNumber();
            Toast.makeText(this, "number: " + num, Toast.LENGTH_SHORT).show();
            Log.i("use JlmLocalService", "test mService.getRandomNumber" + " number: " + num);
        } else {
            Log.i("use JlmLocalService", "test         if (mBound) is false.");
        }
        */
        //test JlmLocalService end

        //test  ===⑤===　JlmMessengerService Start

        /*
        if (!mBound) {
            return;
        }
        // Create and send a message to the service, using a supported 'what' value
        //メッセージ対象を作成します
        Message msg = Message.obtain(null, 1, 0, 0);
        try {
            //客户端利用 Messenger对象给服务端发送Message对象。
            //-------------- MessengerService 4  --------
            mServiceMessenger.send(msg);
            Log.i(JlmMessengerService.class.getName(),"mServiceMessenger.send(msg) ");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        */
        //test  ===⑤===　JlmMessengerService End

    }

    //test ===③===　JlmJobIntentService 服务 和客户端 交互信息  Start
    /*
    private class JlmStateReceiver extends BroadcastReceiver
    {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle Intents here.
            Log.i("JlmStateReceiver","===From JlmJobIntentService=================received ServiceStatus is " + intent.getStringExtra("ServiceStatus"));
        }
    }
    */
    //test ===③===　JlmJobIntentService End


    //test  ===④===　JlmLocalService Start
/*
    JlmLocalService mService;
    boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Log.i("ServiceConnection", "test  onServiceConnected");

            // We've bound to LocalService, cast the IBinder and get LocalService instance
            JlmLocalService.LocalBinder binder = (JlmLocalService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            Log.i("ServiceConnection", "test  onServiceConnected end!!!!!!!!!!!!!!!!!! ");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i("ServiceConnection", "test  onServiceDisconnected");
            mBound = false;
        }
    };
*/
    //test ===④=== JlmLocalService End


    //test  ===⑤===　JlmMessengerService Start
/*
    Messenger mServiceMessenger = null;
    boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.

            //-------------- MessengerService 3  --------

            //客户端利用Ibinder来 实例化Messenger对象。
            mServiceMessenger = new Messenger(service);
            mBound = true;//サービスが用意しました。FLGをセットします。
            Log.i(JlmMessengerService.class.getName(),"ServiceConnection onServiceConnected  ");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mServiceMessenger = null;
            mBound = false;
        }
    };
    */

    //test  ===⑤===　JlmMessengerService　JlmLocalService Start

}
