package jp.co.muroo.systems.bsp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;
import jp.co.muroo.systems.bsp.comm.CommPayBean;
import jp.co.muroo.systems.bsp.comm.CommUtil;
import jp.co.muroo.systems.bsp.contents.MurooKeyView;

/**
 * 決済・返金処理Activity
 */
public class PayActivity extends Activity {

    //決済金額
    private EditText amountDataTxt = null;
    //客様の決済コード
    private EditText payCodeDataTxt = null;

    private TextView userInfoTextView = null;

    private Button btnSeedTmp = null;

    //1:決済処理　2:返金処理
    private int processKbn = 0;

    public MspApplication mspApp = null;

    private static final int START_SCAN = 0x0001;

    CommUtil commUtil = null;

    MurooKeyView murooKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mspApp = (MspApplication) this.getApplication();

        amountDataTxt = findViewById(R.id.txtAmount);
        payCodeDataTxt = findViewById(R.id.txtPayCode);
        userInfoTextView = findViewById(R.id.txtUserInfo);
        btnSeedTmp = findViewById(R.id.btnSeed);

        //画面のデータを初期化
        this.initLayout();

        userInfoTextView.setText(mspApp.getShopName() + "/" + mspApp.getUserId());

        processKbn =  mspApp.getProcessKbn();
        //処理区分により、画面の文字を初期化
        this.setLayout();

        //フォーカスを設定
        this.setFocus(amountDataTxt);

        //入力無効
        amountDataTxt.setInputType(InputType.TYPE_NULL);

        commUtil = CommUtil.getInstance();

        MurooKeyView murooKey = findViewById(R.id.muroo_key_input);

        //該当画面のキーの処理をセット
        murooKey.setNumListener(
                //Lambda書き方
                v -> {
                    System.out.println("MurooKeyView setNumListener");
                    //数字キーの入力処理
                    String valueStr = amountDataTxt.getText().toString().trim();
                    amountDataTxt.setText(valueStr + ((Button)v).getText().toString());
                }
                /*
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("text1View");
                    }
                }
                */
        );
        murooKey.setBackSpaceListener(//Lambda書き方
                v -> {
                    System.out.println("MurooKeyView setBackSpaceListener");

                    //入力したデータの最後文字を消す
                    this.doBackSpace();
                }
        );
        murooKey.setACListener(//Lambda書き方
                v -> {
                    System.out.println("MurooKeyView setACListener");

                    //画面に入力したデータをクリア
                    this.initLayout();
                }
        );
        murooKey.setEnterListener(//Lambda書き方
                v -> {
                    System.out.println("MurooKeyView setEnterListener");
                    //必須条件を判断
                    if (this.doScanCheck()) {
                        this.doScan();
                    }
                }
        );

    }

    //APP再開します
    @Override
    protected void onResume() {
        super.onResume();
        // The application is in foreground
    }

    //立ち留まる　APP一時停止 Backにします
    @Override
    protected void onPause() {
        super.onPause();
        // The application is in background
    }

    //終了します。
    @Override
    protected void onDestroy() {
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

        //画面に入力したデータをクリア
        this.initLayout();
    }

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
            result = commUtil.doPost(getString(R.string.msp_pay_url), params[0], PayActivity.class.getName());
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
                Log.d("PayActivity", ex.getLocalizedMessage());
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
                Toast.makeText(PayActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
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
                mspApp.setPayAmount(Integer.parseInt(jsonResult.getString("Amount")));

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
            btnSeedTmp.setText(R.string.button_seed);
        } else {
            //返金
            this.setTitle(R.string.head_title_name4);//タイトルバーの文字列
            btnSeedTmp.setText(R.string.button_cancel);
        }
    }
    //画面のデータを初期化
    private void initLayout() {
        payCodeDataTxt.setText("");
        amountDataTxt.setText("");
    }

    //入力したデータの最後文字を消す
    private void doBackSpace() {
        String valueStr = amountDataTxt.getText().toString().trim();
        if (!"".equals(valueStr)) {
            amountDataTxt.setText(valueStr.substring(0,valueStr.length()-1));
        }
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
            String kbnStr = checkPaySystemKbn(payCodeDataTxt.getText().toString());
            if ("-1".equals(kbnStr)) {//エラーです
                Toast.makeText(PayActivity.this, R.string.msg0020, Toast.LENGTH_SHORT).show();
                return;
            } else {
                payBean.setSystemId(kbnStr);
            }
            payBean.setAmount(Integer.parseInt(amountDataTxt.getText().toString()));
        } else {
            //返金
            payBean.setProcMode("02");//返金
            payBean.setSystemId("");//返金するときは空白

            if ( TextUtils.isEmpty(amountDataTxt.getText())) {//データがない
                payBean.setAmount(0);
            } else {
                payBean.setAmount(Integer.parseInt(amountDataTxt.getText().toString()));
            }
        }
        //決済QRコード OR 返金オーダーNo
        payBean.setQrCode(payCodeDataTxt.getText().toString());
        payBean.setUserKey(commUtil.getDigitalSignatureStr());

        //決済Jsonを作成
        JSONObject json = getJson(payBean);
        Log.i("PayActivity","送信Json is: " + json.toString());

        //決済は非同期で処理します
        new DoPayment().execute(json);

        //画面のデータを初期化
        initLayout();

        //フォーカスを設定
        setFocus(amountDataTxt);

        Toast.makeText(PayActivity.this, R.string.msg0012, Toast.LENGTH_SHORT).show();

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
            Log.d("PayActivity", ex.toString());
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
            json.put("amount", payBean.getAmount());
            json.put("qrCode", payBean.getQrCode());
            json.put("userKey", payBean.getUserKey());

        } catch (JSONException e) {
            Log.d("PayActivity", e.getLocalizedMessage());
        }
        return json;
    }

    //決済センターに送信前のチェック
    private boolean doSeedCheck() {
        //必須条件を判断
        if (processKbn == 1) {
            //決済
            //必須条件を判断
            if ( TextUtils.isEmpty(amountDataTxt.getText())) {//データがない
                Toast.makeText(PayActivity.this, R.string.msg0007, Toast.LENGTH_SHORT).show();
                //フォーカスを設定
                this.setFocus(amountDataTxt);
                return false;
            }
        } else {
            //返金
            //金額が入力していない、すべて返金します。
        }
        if ( TextUtils.isEmpty(payCodeDataTxt.getText())) {//データがない

            if (processKbn == 1) {
                //決済
                Toast.makeText(PayActivity.this, R.string.msg0008, Toast.LENGTH_SHORT).show();
            } else {
                //返金
                Toast.makeText(PayActivity.this, R.string.msg0018, Toast.LENGTH_SHORT).show();
            }
            //フォーカスを設定
            this.setFocus(payCodeDataTxt);
            return false;
        }
        return true;
    }

    //QRスキャン前のチェック
    private boolean doScanCheck() {
        if (processKbn == 1) {
            //決済
            //必須条件を判断
            if ( TextUtils.isEmpty(amountDataTxt.getText())) {//データがない
                Toast.makeText(PayActivity.this, R.string.msg0007, Toast.LENGTH_SHORT).show();
                //フォーカスを設定
                this.setFocus(amountDataTxt);
                return false;
            }
        } else {
            //返金
            //金額が入力していない、すべて返金します。
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
         "com.sunmi.sunmiqrcodescanner.activity.PayActivity");
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
                Log.i("onActivityResult", "QRコードのデータ:" +value);

                //スキャンしたデータです。
                payCodeDataTxt.setText(value);

                //決済センターに送信
                this.doSeed();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
