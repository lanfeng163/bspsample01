package jp.co.muroo.systems.bspsample01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

/**
 * 登録Activity
 */
public class MainActivity extends Activity {

    public CommGlobals globals = null;
    public final int REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globals = (CommGlobals) this.getApplication();
        globals.setUserId(null);

        //端末情報取得クラス:TelephonyManager生成
//        TelephonyManager telMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//
//        //デヴァイスID
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
//        }


        try {
//            String deviceid = telMgr.getDeviceId();
//            if (deviceid == null) {
//                //android.provider.Settings;
//                deviceid = Settings.Secure.getString(this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//            }
//            //SIMシリアルナンバー
//            String simSerialNumber = telMgr.getSimSerialNumber();

            String uniqueID = UUID.randomUUID().toString();
            TextView uuid = findViewById(R.id.txtUUID);
            uuid.setText(uniqueID);

            // 携帯端末固有ID
            String ids = null;

        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }

    public void login(View view) {

        //do some thing.
        EditText userIdTxt =  findViewById(R.id.txtUserId);
        EditText passwordTxt =  findViewById(R.id.txtPassWord);

        Editable userIdEdi  = userIdTxt.getText();
        Editable passwordEdi  = passwordTxt.getText();

        if ( TextUtils.isEmpty(userIdEdi) ) {
            //ユーザーIDが入力していない。
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
            globals.setUserId(null);
            return;
        }

        if ( TextUtils.isEmpty(passwordEdi) ) {
            //パースワードが入力していない。

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
            globals.setUserId(null);
            return;
        }

        String userId = userIdEdi.toString();
        String password = passwordEdi.toString();

        //userIdは固定です
        if (!"MSP".equals(userId)) {

            AlertDialog.Builder builder = CommAlertDialogBuilder.SetDialog(1, R.string.msg0003,this);
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
            globals.setUserId(null);
            return;
        }

        //passwordは固定です。
        if (!"MSP".equals(password)) {

            AlertDialog.Builder builder = CommAlertDialogBuilder.SetDialog(1, R.string.msg0004,this);
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
            globals.setUserId(null);
            return;
        }

        //共有データを保存する
        globals.setUserId(userId);

    //    Intent intent = new Intent(this, ScanActivity.class);
        Intent intent = new Intent(this, MenuActivity.class);
    //    intent.putExtra(LOGIN_USERID, userId);

        startActivity(intent);
    }

    public void clear(View view) {
        //do some thing.

        EditText userId =  findViewById(R.id.txtUserId);
        EditText password =  findViewById(R.id.txtPassWord);

        userId.setText("");
        password.setText("");

        globals.setUserId(null);
    }
}
