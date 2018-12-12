package jp.co.muroo.systems.bsp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;
import jp.co.muroo.systems.bsp.comm.CommAlertDialogBuilder;
import jp.co.muroo.systems.bsp.comm.CommUtil;

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

        userIdText =  findViewById(R.id.txtUserId);
        passwordText =  findViewById(R.id.txtPassWord);
        deviceIdView = findViewById(R.id.txtUUID);
        messageView = findViewById(R.id.textMessage);

        mspApp = (MspApplication) this.getApplication();

        commUtil = CommUtil.getInstance();

        this.doInit();

        try {
            //デバイスIDを取得します。
            loadDeviceId();

            /* Fading Transition Effect */
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } catch (Exception ex) {

            deviceIdView.setText("Get DeviceID is failed!");
            Log.e("MainActivity", ex.getLocalizedMessage());
        }
        //for test!
        mspApp.setDeviceId("0300000001");
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
              //      .setIcon(R.drawable.onlinlinew_warning_sign)
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
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
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

    public void doPermissionGrantedStuffs() throws SecurityException {
        //Have an  object of TelephonyManager
        TelephonyManager tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        /* ***********************************************
         * **********************************************
         * This is just an icing on the cake
         * the following are other children of TELEPHONY_SERVICE
         *
         //Get Subscriber ID
         String subscriberID=tm.getDeviceId();

         //Get Network Country ISO Code
         String networkCountryISO=tm.getNetworkCountryIso();

         //Get SIM Country ISO Code
         String SIMCountryISO=tm.getSimCountryIso();

         //Get the device software version
         String softwareVersion=tm.getDeviceSoftwareVersion()

         //Get the Voice mail number
         String voiceMailNumber=tm.getVoiceMailNumber();


         //Get the Phone Type CDMA/GSM/NONE
         int phoneType=tm.getPhoneType();

         switch (phoneType)
         {
         case (TelephonyManager.PHONE_TYPE_CDMA):
         // your code
         break;
         case (TelephonyManager.PHONE_TYPE_GSM)
         // your code
         break;
         case (TelephonyManager.PHONE_TYPE_NONE):
         // your code
         break;
         }

         //Find whether the Phone is in Roaming, returns true if in roaming
         boolean isRoaming=tm.isNetworkRoaming();
         if(isRoaming)
         phoneDetails+="\nIs In Roaming : "+"YES";
         else
         phoneDetails+="\nIs In Roaming : "+"NO";


         //Get the SIM state
         int SIMState=tm.getSimState();
         switch(SIMState)
         {
         case TelephonyManager.SIM_STATE_ABSENT :
         // your code
         break;
         case TelephonyManager.SIM_STATE_NETWORK_LOCKED :
         // your code
         break;
         case TelephonyManager.SIM_STATE_PIN_REQUIRED :
         // your code
         break;
         case TelephonyManager.SIM_STATE_PUK_REQUIRED :
         // your code
         break;
         case TelephonyManager.SIM_STATE_READY :
         // your code
         break;
         case TelephonyManager.SIM_STATE_UNKNOWN :
         // your code
         break;

         }
         */

        //Get SIM Serial Number
        String SIMSerialNumber=tm.getSimSerialNumber();

        mspApp.setDeviceId(SIMSerialNumber);
        deviceIdView.setText(SIMSerialNumber);
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

        //TODO:テストために、一時ソース
/*
        mspApp.setShopName("Test店舗名");
        mspApp.setShopInfo("東京都中央区日本橋８－８－８");
        mspApp.setToken("test");
        mspApp.setDigitalSignature("testKey");

        Intent intent2 = new Intent(this, MenuActivity.class);
        startActivity(intent2);
*/
        String userId = userIdEdi.toString();
        String password = passwordEdi.toString();

        //MSP WebAPIに送信処理
        mspApp.setUserId(userId);
        mspApp.setDigitalSignature(commUtil.getDigitalSignatureStr());

        //決済Jsonを作成
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
        deviceIdView.setText("");
        messageView.setText(getString(R.string.app_info));

        mspApp.setDeviceId("0300000001");
        mspApp.setUserId("");
        mspApp.setDigitalSignature("");

        mspApp.setShopName("");
        mspApp.setShopInfo("");
        mspApp.setToken("");
    }

    //################## 自定義方法 ################################################

    //########### 内部クラス   ##################
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
                String serverKeyStr = jsonResult.getString("ServerKey");
                //    String processDateTimeStr = rootJSON.getString("ProcessDateTime");

                //基本データをセット存する
                mspApp.setShopName(shopNameStr);
                mspApp.setShopInfo("東京都中央区日本橋８－８－８");
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
}
