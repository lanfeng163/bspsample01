package jp.co.muroo.systems.bsp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;
import jp.co.muroo.systems.bsp.comm.CommUtil;

/**
 * ログインActivity
 */
public class LoginActivity extends Activity {

    public MspApplication mspApp = null;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_0 = 0;

    EditText userIdText = null;
    EditText passwordText =  null;
    TextView deviceIdView = null;
    CommUtil commUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setTitle(R.string.head_title_name1);//タイトルバーの文字列

        userIdText =  findViewById(R.id.txtUserId);
        passwordText =  findViewById(R.id.txtPassWord);
        deviceIdView = findViewById(R.id.txtUUID);

        mspApp = (MspApplication) this.getApplication();

        commUtil = CommUtil.getInstance();

        this.doInit();
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
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Permission Request")
                    .setMessage(getString(R.string.permission_read_phone_state_rationale))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        //re-request
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE_0);
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
                Toast.makeText(LoginActivity.this, R.string.permissions_not_granted_read_phone_state, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * シリアル番号を取得すます。(TelephonyManagerから取得します)「SIMSerialNumber」
     * @throws SecurityException
     */
    public void doPermissionGrantedStuffs() throws SecurityException {
        //Have an  object of TelephonyManager
        TelephonyManager tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        //Get SIM Serial Number
        String SIMSerialNumber=tm.getSimSerialNumber();
        if (SIMSerialNumber == null || "".equals(SIMSerialNumber)) {
            SIMSerialNumber = getSerialNumber();
        }
        if (SIMSerialNumber == null || "".equals(SIMSerialNumber)) {
            Toast.makeText(LoginActivity.this, R.string.msg0004, Toast.LENGTH_SHORT).show();
            deviceIdView.setText(getString(R.string.msg0005));
        } else {
            mspApp.setDeviceId(SIMSerialNumber);
            deviceIdView.setText(SIMSerialNumber);
        }
    }

    /**
     * シリアル番号を取得すます。(システムプロパティから取得します)「ro.serialno」
     * @return
     */
    public String getSerialNumber()  {
        String serialNumber = "";

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serialNumber = (String) get.invoke(c, "ro.serialno");//OK got

        } catch (Exception ex) {
            Log.e("LoginActivity", ex.toString());
        }
        return serialNumber;
    }

    /**
     * パスワードの表示を選択する
     * @param view
     */
    public void onCheckboxClicked(View view) {
        final boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkBoxPdV:
                if (checked) {
                    // チェックボックスがチェックされる
                    passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // チェックボックスのチェックが外される
                    passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordText.setSelection(passwordText.length());
                }
        }
    }

    /**
     * ログイン処理
     * @param view
     */
    public void login(View view) {
        try {

            Editable userIdEdi  = userIdText.getText();
            Editable passwordEdi  = passwordText.getText();

            //ユーザーIDが入力していない。
            if ( TextUtils.isEmpty(userIdEdi) ) {
                Toast.makeText(LoginActivity.this, R.string.msg0013, Toast.LENGTH_SHORT).show();
                mspApp.setUserId(null);
                return;
            }

            //パースワードが入力していない。
            if ( TextUtils.isEmpty(passwordEdi) ) {
                Toast.makeText(LoginActivity.this, R.string.msg0014, Toast.LENGTH_SHORT).show();
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
            Log.i("LoginActivity","送信Json is: " + json.toString());

            //ログイン送信は非同期で処理します
            new DoLogin().execute(json);

            //フォーカスを設定
            this.setFocus(userIdText);

            Toast.makeText(LoginActivity.this, R.string.msg0012, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(LoginActivity.this, R.string.msg0003, Toast.LENGTH_SHORT).show();
            Log.e("LoginActivity", ex.toString());
        }
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
    private JSONObject getLoginJson(MspApplication mspApp, String passwordStr) throws JSONException {

        JSONObject json = new JSONObject();
        json.put("terminalId", mspApp.getDeviceId());
        json.put("userId", mspApp.getUserId());
        json.put("loginPassword", passwordStr);
        json.put("UserKey", mspApp.getDigitalSignature());
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
     * 入力ボタンの処理
     * @param view
     */
    public void input(View view) {
        userIdText.setText("user01");
        passwordText.setText("abc123ABC");
    }

    /**
     * 初期化
     */
    private void doInit() {

        userIdText.setText("");
        passwordText.setText("");

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
            Toast.makeText(LoginActivity.this, R.string.msg0004, Toast.LENGTH_SHORT).show();
            deviceIdView.setText(getString(R.string.msg0005));
            Log.e("LoginActivity", ex.toString());
        }
    }

    /**
     * ログイン送信クラス ########### 内部クラス 「非同期処理」  ##################
     */
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
            result = commUtil.doPost(getString(R.string.msp_login_url), params[0], LoginActivity.class.getName());
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
                    mspApp.setResultKbn("99");//結果処理区分（ 99：失敗）
                } else if ("NOT200ERROR".equals(result)) {
                    resultMessage = getString(R.string.msg0024);
                    //失敗
                    mspApp.setResultKbn("99");//結果処理区分（99：失敗）
                } else {
                    JSONObject rootJSON = new JSONObject(result);

                    //BSP WebAPI　から受信結果を処理
                    resultMessage = setMspResult(rootJSON);
                }
            } catch (JSONException ex) {
                Log.d(LoginActivity.class.getName(), ex.toString());
                resultMessage = getString(R.string.msg0016);
                //失敗
                mspApp.setResultKbn("99");//結果処理区分（99：失敗）
            }
            //処理が完了

            //処理が完了
            if (("00").equals(mspApp.getResultKbn())) {//正常
                //結果画面に遷移します。
                //Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                Intent intent = new Intent(getApplicationContext(), MenuBtnsActivity.class);
                startActivity(intent);
            } else {
                //メッセージをセット
                Toast.makeText(LoginActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
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

                String serverKeyStr = jsonResult.getString("ServerKey");
                mspApp.setServerDigitalSignature(serverKeyStr);
                if (!commUtil.checkDigitalSignature(mspApp.getServerDigitalSignature())) {
                    //デジタル署名　チェックします
                    mspApp.setResultKbn("99");//結果処理区分（99：失敗）
                    resultMessage = getString(R.string.msg0019);
                } else {
                    mspApp.setResultKbn("00");//WebAPI処理 正常
                    //成功
                    String tokenStr = jsonResult.getString("Token");
                    String shopNameStr = jsonResult.getString("ShopName");
                    String shopInfo = jsonResult.getString("ShopAddress");
                    String shopTel = jsonResult.getString("ShopTel");
                    //    String processDateTimeStr = rootJSON.getString("ProcessDateTime");

                    //基本データをセット存する
                    mspApp.setShopName(shopNameStr);
                    mspApp.setShopInfo(shopInfo);
                    mspApp.setShopTel(shopTel);
                    mspApp.setToken(tokenStr);
                }
            } else {
                //WebAPI処理　失敗
                mspApp.setResultKbn("99");//結果処理区分（99：失敗）
            }
            return resultMessage;
        }
    }
    //########### 内部クラス End  ##################
}
