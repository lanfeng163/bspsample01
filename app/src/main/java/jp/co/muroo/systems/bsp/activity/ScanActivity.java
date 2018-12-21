package jp.co.muroo.systems.bsp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;
import jp.co.muroo.systems.bsp.comm.CommPayBean;
import jp.co.muroo.systems.bsp.comm.CommUtil;

/**
 * 決済・返金処理Activity
 */
public class ScanActivity extends Activity {

    //決済金額
    private EditText amountDataTxt = null;
    //客様の決済コード
    private EditText customDataTxt = null;
    private TextView statusTextView = null;
    private TextView userInfoTextView = null;

    //1:決済処理　2:返金処理
    private int processKbn = 0;

    public MspApplication mspApp = null;

    private static final int START_SCAN = 0x0001;

    CommUtil commUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("ScanActivity","111111111 onCreate 11111111111");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mspApp = (MspApplication) this.getApplication();

        amountDataTxt = findViewById(R.id.txtAmount);
        customDataTxt = findViewById(R.id.txtPayCode);
        statusTextView = findViewById(R.id.textViewStatus);
        userInfoTextView = findViewById(R.id.txtUserInfo);

        //画面のデータを初期化
        this.initLayout();

        String userId = mspApp.getUserId();
        userInfoTextView.setText(userId);

        processKbn =  mspApp.getProcessKbn();
        //処理区分により、画面の文字を初期化
        this.setLayout();

        //フォーカスを設定
        this.setFocus(amountDataTxt);

        commUtil = CommUtil.getInstance();
    }

    //APP再開します
    @Override
    protected void onResume() {
        Log.d("ScanActivity","111111111 onResume ");
        super.onResume();
        // The application is in foreground
    }

    //立ち留まる　APP一時停止 Backにします
    @Override
    protected void onPause() {
        Log.d("ScanActivity","111111111 onPause ");
        super.onPause();
        // The application is in background
    }

    //終了します。
    @Override
    protected void onDestroy() {
        Log.d("ScanActivity","11111111 onDestroy ");
        super.onDestroy();
        // De-initialize scanner
    }

    //①スキャンーボタンの処理
    public void scan(View view) {

        //必須条件を判断
        if (this.doScanCheck()) {
            this.doScan();
        }
    }

    //②送信ボタンの処理
    public void seed(View view) {
        //必須条件を判断
        if (this.doSeedCheck()) {

            //送信
            this.doSeed();
        }
    }

    //③クリアボタンの処理
    public void clear(View view) {

        //画面のデータを初期化
        this.initLayout();
    }

    //################## 自定義方法 ################################################

    //########### 内部クラス   ##################
    // AsyncTask that configures the scanned data on background
    // thread and updated the result on UI thread with scanned data and type of label
    //その際、AsyncTaskにジェネリクスを3個指定する必要があります。
    // これは、AsyncTaskを継承したクラス内のメソッドの引数や戻り値の型を指定するためです。
    //決済クラス
    private class DoPayment extends AsyncTask<JSONObject, String, String> {
        /**
         * 決済送信
         * @param params
         * @return
         */
        @Override
        public String doInBackground(JSONObject... params) {
            String result = null;
            //POSTで　WebAPIに決済送信
            result = commUtil.doPost(getString(R.string.msp_pay_url), params[0], ScanActivity.class.getName());
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
            }
            catch(JSONException ex) {
                Log.d("ScanActivity", ex.getLocalizedMessage());
                resultMessage = getString(R.string.msg0016);
                //失敗
                mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
            }
            //処理が完了
            if (mspApp.getResultKbn() != null && !("99").equals(mspApp.getResultKbn())) {
                //結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
                //結果画面に遷移します。
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                startActivity(intent);
            } else {
                //メッセージをセット
                statusTextView.setText(resultMessage);
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
                mspApp.setToken(jsonResult.getString("Token"));
                mspApp.setPayOrderId(jsonResult.getString("ProcId"));
                mspApp.setPayProcessDateTime(jsonResult.getString("ProcessDateTime"));

                String payCompanyKbn = jsonResult.getString("SystemId");
                mspApp.setPayCompany(commUtil.getPayCompanyNm(payCompanyKbn));

                if (processKbn == 1) { //決済
                    mspApp.setResultKbn("11");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
                } else {  //返金
                    mspApp.setResultKbn("21");
                }
                //mspApp.setPayUserId(mspApp.getUserId());
                //mspApp.setPayDeviceId(mspApp.getDeviceId());
                mspApp.setServerDigitalSignature(jsonResult.getString("ServerKey"));

                if (!commUtil.checkDigitalSignature(mspApp.getServerDigitalSignature())) {
                    //デジタル署名　チェックします
                    mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
                    resultMessage = getString(R.string.msg0019);
                }
            } else {
                //失敗
                mspApp.setToken(jsonResult.getString("Token"));
                mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
            }
            return resultMessage;
        }
    }

    //########### 内部クラス End  ##################

    //処理区分により、画面の文字を初期化
    private void setLayout() {
        if (processKbn == 1) {
            //決済
            this.setTitle(R.string.head_title_name3);//タイトルバーの文字列
            ((TextView)findViewById(R.id.lab_PayAmount)).setText(R.string.lab_Amount);
            ((TextView)findViewById(R.id.lab_PayCardNo)).setText(R.string.lab_customId);
            ((Button)findViewById(R.id.btn_Seed)).setText(R.string.button_seed);
        } else {
            //返金
            this.setTitle(R.string.head_title_name4);//タイトルバーの文字列
            ((TextView)findViewById(R.id.lab_PayAmount)).setText(R.string.cancel_lab_Amount);
            ((TextView)findViewById(R.id.lab_PayCardNo)).setText(R.string.cancel_lab_customId);
            ((Button)findViewById(R.id.btn_Seed)).setText(R.string.button_cancel);
        }
    }
    //画面のデータを初期化
    private void initLayout() {
        customDataTxt.setText("");
        amountDataTxt.setText("");
        statusTextView.setText("");
    }

    //フォーカスを設定
    private void setFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    /**
     * 決済センターに送信
     */
    private void doSeed() {

        //TODO 決済送信データをセット…
        CommPayBean payBean = new CommPayBean();
      
        payBean.setTerminalId(mspApp.getDeviceId());
        payBean.setUserId(mspApp.getUserId());
        payBean.setToken(mspApp.getToken());
        if (processKbn == 1) {
            //決済
            payBean.setProcMode("01");//決済
            //決済QRコードにより、決済センターを決まり・チェック
            String kbnStr = checkPaySystemKbn(customDataTxt.getText().toString());
            if ("-1".equals(kbnStr)) {//エラーです
                statusTextView.setText(R.string.msg0020);
                return;
            } else {
                payBean.setSystemId(kbnStr);
            }
        } else {
            //返金
            payBean.setProcMode("02");//返金
            payBean.setSystemId("");//返金するときは空白
        }
        payBean.setAmount(Integer.parseInt(amountDataTxt.getText().toString()));
        //決済QRコード OR 返金オーダーNo
        payBean.setQrCode(customDataTxt.getText().toString());
        payBean.setUserKey(commUtil.getDigitalSignatureStr());

        mspApp.setPayAmount(payBean.getAmount());

        //決済Jsonを作成
        JSONObject json = getJson(payBean);
        Log.i("ScanActivity","送信Json is: " + json.toString());

        //決済は非同期で処理します
        new DoPayment().execute(json);

        //画面のデータを初期化
        initLayout();

        //フォーカスを設定
        setFocus(amountDataTxt);

        statusTextView.setText(R.string.msg0012);

    }

    /**
     * QRコードのチェック
     * @param qrCdStr
     * @return
     */
    private String checkPaySystemKbn(String qrCdStr) {
        /*
        バーコードの区別ルール:
        AliPay ２５～３０からスタート
        WeChat １０～１５からスタート
        */
        String paySystemKbn = "-1";
        try {
            String start2Str = qrCdStr.substring(0, 2);
            int sCd = Integer.parseInt(start2Str);
            if (sCd >= 10 && sCd <= 15) {
                paySystemKbn = "02"; //wechatpay
            } else if (sCd >= 25 && sCd <= 30) {
                paySystemKbn = "01";  //alipay
            }
        } catch (Exception ex) {
            Log.d("ScanActivity", ex.toString());
        }
        return paySystemKbn;
    }

    /**
     * JSONデータを作成
     * @param payBean
     * @return
     */
    private JSONObject getJson(CommPayBean payBean) {

        JSONObject json = new JSONObject();
     //   JSONObject subJson = new JSONObject();
        try {
            json.put("terminalId", payBean.getTerminalId());
            json.put("userId", payBean.getUserId());
            json.put("token", payBean.getToken());
            json.put("systemId", payBean.getSystemId());
            json.put("procMode", payBean.getProcMode());
/*
            if (processKbn == 1) {
                //決済
                subJson.put("amount", payBean.getAmount());
                subJson.put("authCode", payBean.getAuthCode());
            } else {
                //返金
                subJson.put("refundAmount", payBean.getAmount());
                subJson.put("platformOrderNo", payBean.getAuthCode());
            }
*/

        //    json.put("WeChatPayData", subJson);

            json.put("amount", payBean.getAmount());
            json.put("qrCode", payBean.getQrCode());
            json.put("userKey", payBean.getUserKey());

        } catch (JSONException e) {
            Log.d("ScanActivity", e.getLocalizedMessage());
        }
        return json;
    }

    //決済センターに送信前のチェック
    private boolean doSeedCheck() {

        //必須条件を判断
        if ( TextUtils.isEmpty(amountDataTxt.getText())) {//データがない
            statusTextView.setText(R.string.msg0007);
            //フォーカスを設定
            this.setFocus(amountDataTxt);
            return false;
        }
        if ( TextUtils.isEmpty(customDataTxt.getText())) {//データがない

            if (processKbn == 1) {
                //決済
                statusTextView.setText(R.string.msg0008);
            } else {
                //返金
                statusTextView.setText(R.string.msg0018);
            }
            //フォーカスを設定
            this.setFocus(customDataTxt);
            return false;
        }
        return true;
    }

    //QRスキャン前のチェック
    private boolean doScanCheck() {

        //必須条件を判断
        if ( TextUtils.isEmpty(amountDataTxt.getText())) {//データがない
            statusTextView.setText(R.string.msg0007);
            //フォーカスを設定
            this.setFocus(amountDataTxt);
            return false;
        }
        return true;
    }

    /**
     * QRスキャンナー起動（Sunmi）
     */
    private void doScan() {
        /**
         * ①
         * 外部应用在自己的业务代码需要启动扫码的地方使用下面的方式创建Intent，
         * 然后使用startActivityForResult()调用起商米的扫码模块;
         */
        Intent intent = new Intent("com.summi.scan");
        intent.setPackage("com.sunmi.sunmiqrcodescanner");
        /*
         * 使用该方式也可以调用扫码模块
         *Intent intent = new Intent("com.summi.scan");
         *intent.setClassName("com.sunmi.sunmiqrcodescanner",
         "com.sunmi.sunmiqrcodescanner.activity.ScanActivity");
         */

        /**
         //②
         // Local srcを利用 com.sunmi.codescanner.activity.ScanActivityを使う
         //        Intent intent = new Intent();
         //        intent.setAction("com.sunmi.scan");
         //        intent.setPackage("com.sunmi.codescanner");
         */

        //扫码模块有一些功能选项，开发者可以通过传递参数控制这些参数，
        //所有参数都有一个默认值，开发者只要在需要的时候添加这些配置就可以。
        intent.putExtra("CURRENT_PPI", 0X0002);//当前分辨率
        //M1和V1的最佳是800*480,PPI_1920_1080 = 0X0001;PPI_1280_720 =0X0002;PPI_BEST = 0X0003;

        intent.putExtra("PLAY_SOUND", true);// 扫描完成声音提示  默认true
        intent.putExtra("PLAY_VIBRATE", false);
        //扫描完成震动,默认false，目前M1硬件支持震动可用该配置，V1不支持

        intent.putExtra("IDENTIFY_INVERSE_QR_CODE", true);// 识别反色二维码，默认true
        intent.putExtra("IDENTIFY_MORE_CODE", false);// 识别画面中多个二维码，默认false
        intent.putExtra("IS_SHOW_SETTING", true);// 是否显示右上角设置按钮，默认true
        intent.putExtra("IS_SHOW_ALBUM", false);// 是否显示从相册选择图片按钮，默认true

        //JLM Add
        intent.putExtra("IS_OPEN_LIGHT", true);// 灯模式: false 灯灭; true 灯亮，默认false
        intent.putExtra("LIGHT_BRIGHT_TIME", 200);//灯亮时间（单位: 毫秒）
        intent.putExtra("LIGHT_DROWN_TIME", 500);//灯灭时间（单位: 毫秒）

        startActivityForResult(intent, START_SCAN);
    }

    /**
     * スキャン結果の処理 （Sunmi）
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == START_SCAN && data != null) {

            Bundle bundle = data.getExtras();
            ArrayList<HashMap<String, String>> result
                    = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
            Iterator<HashMap<String, String>> it = result.iterator();
            while (it.hasNext()) {

                HashMap<String, String> hashMap = it.next();

                String type = hashMap.get("TYPE");
                String value = hashMap.get("VALUE");
                Log.i("onActivityResult", "QRコードのタイプ:" + type);
                Log.i("onActivityResult", "QRコードのデータ:" +value);//refundorder_15434699667033220

                //スキャンしたデータです。
                customDataTxt.setText(value);

                //決済センターに送信
                this.doSeed();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
