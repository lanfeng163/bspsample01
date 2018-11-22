package jp.co.muroo.systems.bspsample01;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * スキャン　決済送信Activity
 */
public class ScanActivity extends Activity {

    // Edit Text that is used to display scanned barcode data

    //決済会社区分
    RadioGroup radioGroup = null;
  //  RadioButton radioButtonPayCpNm1 = null;

    //決済金額
    private EditText amountDataTxt = null;
    //客様の決済コード
    private EditText customDataTxt = null;

    // Text view to display status of EMDK and Barcode Scanning Operations
    private TextView statusTextView = null;
    private TextView userInfoTextView = null;

    //boolean flag to start scanning after scanner initialization, Used in OnStatus callback to insure scanner is idle before read() is called
    private boolean startRead = false;

    private boolean starting = false;
    private boolean ending = false;

    //1:決済　2:返金
    private int processKbn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("111111111 onCreate 11111111111");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Reference to UI elements
        radioGroup = findViewById(R.id.radioGroup);
      //  radioButtonPayCpNm1 = findViewById(R.id.radioButtonPayCpNm1);

        amountDataTxt = findViewById(R.id.txtAmount);
        customDataTxt = findViewById(R.id.txtPayCode);
        statusTextView = findViewById(R.id.textViewStatus);
        userInfoTextView = findViewById(R.id.txtUserInfo);


        radioGroup.check(R.id.radioButtonPayCpNm2);

        //画面のデータを初期化
        this.initLayout();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        //String userId = intent.getStringExtra(MainActivity.LOGIN_USERID);
        String userId = ((CommGlobals) this.getApplication()).getUserId();

        processKbn =  ((CommGlobals) this.getApplication()).getProcessKbn();


        // Capture the layout's TextView and set the string as its text
        userInfoTextView.setText(userId);

        //客様の決済コードのイベントを追加する
    //    customDataTxt.addTextChangedListener(new onCustomDataChanged());

        System.out.println("111111111 onCreate  EMDKManager.getEMDKManager 11111111111");
        //EMDKManagerを初期化

        //処理区分により、画面の文字を初期化
        this.setLayout();

        //フォーカスを設定
        this.setFocus(amountDataTxt);
    }

    //APP再開します
    @Override
    protected void onResume() {
        System.out.println("111111111 onResume 11111111111");
        super.onResume();
        // The application is in foreground

    }

    //立ち留まる　APP一時停止 Backにします
    @Override
    protected void onPause() {
        System.out.println("111111111 onPause 11111111111");
        super.onPause();
        // The application is in background


    }

    //終了します。
    @Override
    protected void onDestroy() {

        System.out.println("11111111 onDestroy 1111111111");

        super.onDestroy();

        // De-initialize scanner

    }

    //開始ボタンの処理
    public void start(View view) {

        //必須条件を判断
        if (this.doStartCheck()) {

            // Call this method to enable Scanner and its listeners
            starting = true;

            System.out.println(" ##############JLM#####　＄＄＄＄＄＄＄＄＄＄＄＄＄＄＄　起動ボタンの処理;  ");

            //スキャンナーを初期化

            //スキャンナーを開始
            //scanRead();

            starting = false;
        }
    }

    //中止ボタンの処理
    public void end(View view) {
        System.out.println(" ##############JLM#####　＄＄＄＄＄＄＄＄＄＄＄＄＄＄＄　中止ボタンが押下した  ");

        //スキャンナーを終了
        this.scanEnd();

        statusTextView.setText(R.string.msg0009);
    }

    //送信ボタンの処理
    public void seed(View view) {
        //必須条件を判断
        if (this.doSeedCheck()) {

            //スキャンナーを終了
            this.scanEnd();

            this.doSeed();
        }
    }

    //クリアボタンの処理
    public void clear(View view) {

        //スキャンナーを終了
        this.scanEnd();

        //画面のデータを初期化
        this.initLayout();

        statusTextView.setText(R.string.msg0010);

    }

    //################## 自定義方法 ################################################

    //########### 内部クラス   ##################
    // AsyncTask that configures the scanned data on background
    // thread and updated the result on UI thread with scanned data and type of label
    //その際、AsyncTaskにジェネリクスを3個指定する必要があります。
    // これは、AsyncTaskを継承したクラス内のメソッドの引数や戻り値の型を指定するためです。
    //決済クラス
    private class DoPayment extends AsyncTask<JSONObject, String, String> {

        //決済送信
        @Override
        public String doInBackground(JSONObject... params) {

            String result = null;
            //Get 送信 天気予報情報を取得
            //result = this.getInfo();

            //POSTで　決済送信
            result = this.payByPost(params[0]);

            return result;
        }

        //結果を表示
        @Override
        public void onPostExecute(String result) {
            String resultMessage = null;
            try {
                if (result == null || "JAVAERROR".equals(result)) {
                    resultMessage = getString(R.string.msg0015);
                } else if ("NOT200ERROR".equals(result)) {
                    resultMessage = getString(R.string.msg0024);
                } else {
                    JSONObject rootJSON = new JSONObject(result);

                    // Get 送信 天気予報情報を取得 結果処理 test Start
                    /*

                    String desc = "";
                    String dateLabel = "";
                    String telop = "";

                    //①rootJSONから　description JSONObjectを取得
                    JSONObject descriptionJSON = rootJSON.getJSONObject("description");
                    //①－１　descriptionJSON　から　textの文字を取得
                    desc = descriptionJSON.getString("text");

                    //②rootJSONから　description JSONArrayを取得
                    JSONArray forecastsJsonList = rootJSON.getJSONArray("forecasts");
                    //②－１　forecastsJsonListから　index=0(本日)のJSONObjectを取得
                    JSONObject forecastNowJson = forecastsJsonList.getJSONObject(0);
                    //②－２　forecastNowJson　(本日)　から　dateLabelの文字を取得
                    dateLabel = forecastNowJson.getString("dateLabel");
                    //②－３　forecastNowJson　(本日)　から　telopの文字を取得
                    telop = forecastNowJson.getString("telop");
                    statusTextView.setText(telop + " " + dateLabel);
                    */
                    // Get 送信 天気予報情報を取得 結果処理 test end

                    //POST 送信 NODE.JS TEST Server　結果処理 Test
                    /*
                    telop = rootJSON.getString("name"); //mspJLM
                    int ids = rootJSON.getInt("id"); //mspJLM
                    String testStr = rootJSON.getString("profession"); //mspJLM
                    statusTextView.setText(telop + " " + dateLabel);
                    */

                    //BSP WebAPI　から受信結果を処理
                    resultMessage = setMspResult(rootJSON);
                }
            }
            catch(JSONException ex) {
                ex.printStackTrace();
                resultMessage = getString(R.string.msg0016);
            }
            statusTextView.setText(resultMessage);
        }

        /**
         * InputStreamオブジェクトを文字列に変換するメソッド。変換文字コードはUTF-8。
         *
         * @param is 変換対象のInputStreamオブジェクト。
         * @return 変換された文字列。
         * @throws IOException 変換に失敗した時に発生。
         */
        private String getStringByIS(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            char[] b = new char[1024];
            int line;
            while(0 <= (line = reader.read(b))) {
                sb.append(b, 0, line);
            }
            String sbStr = sb.toString();
//
//            if (sbStr.startsWith("\"")) {
//                //頭の「"」を切り
//                sbStr = sbStr.substring(1);
//            }
//            if (sbStr.endsWith("\"")) {
//                //後ろの「"」を切り
//                sbStr = sbStr.substring(0,sbStr.length()-1);
//            }
            sbStr = sbStr.replaceAll("\\\\r\\\\n", "");
            sbStr = sbStr.replace("\"{", "{");
            sbStr = sbStr.replace("}\",", "},");
            sbStr = sbStr.replace("}\"", "}");
            sbStr = sbStr.replace("\\", "");
            return sbStr;
        }

        //Get 送信 天気予報情報を取得
        private String getInfo() {

            //天気予報情報を取得
            String cityId = null;
            cityId = "260020"; //舞鶴 do test
            String urlStr = getString(R.string.msp_webapi_url2) + "?city=" + cityId;
            //http://weather.livedoor.com/forecast/webservice/json/v1?city=" + id;

            HttpURLConnection connection = null;
            InputStream is = null;
            String result = null;

            try {
                // 接続先のURLの設定およびコネクションの取得
                URL url = new URL(urlStr);
                System.out.println("Request URL is " + urlStr);
                connection = (HttpURLConnection) url.openConnection();

                //天気予報情報を取得 GET start
                connection.setRequestMethod("GET");
                //天気予報情報を取得 GET end

                // 接続！
                connection.setConnectTimeout(6000);
                connection.setReadTimeout(6000);
                connection.connect();

                if (connection.getResponseCode() != 200) {
                    result = "NOT200ERROR";
                    return result;
                }
                is = connection.getInputStream();

                result = getStringByIS(is);
                System.out.println("Response result is " + result);
            }
            catch(MalformedURLException ex) {
                ex.printStackTrace();
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
            finally {
                if(connection != null) {
                    connection.disconnect();
                }
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch(IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return result;
        }

        //POST 送信 NODE.JS TEST Server
        private String payByPost2(JSONObject json) {

            //POST 送信　TEST
            String urlStr = getString(R.string.msp_webapi_url1);

            HttpURLConnection connection = null;
            InputStream is = null;
            String result = null;

            try {
                // 接続先のURLの設定およびコネクションの取得
                URL url = new URL(urlStr);
                System.out.println("Request URL is " + urlStr);
                connection = (HttpURLConnection) url.openConnection();

                //JSONを送信　POST start
                // データを送信するためにはbyte配列に変換する必要がある
                byte[] sendJson = json.toString().getBytes("UTF-8");

                // 接続するための設定
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                // APIからの戻り値と送信するデータの設定を許可する
                connection.setDoInput(true);
                connection.setDoOutput(true);
                // 送信するデータの設定
                connection.getOutputStream().write(sendJson);
                connection.getOutputStream().flush();
                connection.getOutputStream().close();
                //JSONを送信　POST end

                // 接続！
                connection.setConnectTimeout(6000);
                connection.setReadTimeout(6000);
                connection.connect();

                if (connection.getResponseCode() != 200) {
                    result = "NOT200ERROR";
                    return result;
                }
                is = connection.getInputStream();

                result = getStringByIS(is);
                System.out.println("Response result is " + result);
            }
            catch(MalformedURLException ex) {
                ex.printStackTrace();
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
            finally {
                if(connection != null) {
                    connection.disconnect();
                }
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch(IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return result;
        }

        //POST 送信 MSP WebAPI
        private String payByPost(JSONObject json) {

            //POST 送信　TEST
            String urlStr = getString(R.string.msp_webapi_url);

            HttpURLConnection connection = null;
            InputStream is = null;
            String result = null;

            try {
                // 接続先のURLの設定およびコネクションの取得
                URL url = new URL(urlStr);
                System.out.println("Request URL is " + urlStr);
                connection = (HttpURLConnection) url.openConnection();

                //JSONを送信　POST start
                // データを送信するためにはbyte配列に変換する必要がある
                byte[] sendJson = json.toString().getBytes("UTF-8");

                // 接続するための設定
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                // APIからの戻り値と送信するデータの設定を許可する
                connection.setDoInput(true);
                connection.setDoOutput(true);
                // 送信するデータの設定
                connection.getOutputStream().write(sendJson);
                connection.getOutputStream().flush();
                connection.getOutputStream().close();
                //JSONを送信　POST end

                // 接続！
                connection.setConnectTimeout(6000);
                connection.setReadTimeout(6000);
                connection.connect();

                if (connection.getResponseCode() != 200) {
                    result = "NOT200ERROR";
                } else {
                    is = connection.getInputStream();
                    //InputStreamオブジェクトを文字列に変換
                    result = getStringByIS(is);
                }
                System.out.println("Response result is " + result);
            }
            catch(MalformedURLException ex) {
                ex.printStackTrace();
                result = "JAVAERROR";
            }
            catch(IOException ex) {
                ex.printStackTrace();
                result = "JAVAERROR";
            }
            catch(Exception ex) {
                ex.printStackTrace();
                result = "JAVAERROR";
            }
            finally {
                if(connection != null) {
                    connection.disconnect();
                }
                if(is != null) {
                    try {
                        is.close();
                    }
                    catch(IOException ex) {
                        ex.printStackTrace();
                        result = "JAVAERROR";
                    }
                }
            }
            return result;
        }

        //MSP WebAPI　の　受信結果処理
        private String setMspResult(JSONObject jsonResult) {
            //TODO 受信結果を処理
            /*
            支払成功時のデータ　例：
            {
                "meta": {
                        "code": "00",
                        "message": "SUCCESS"
            　　},
                "data": {
                        "errorCode": "",
                        "errorInfo": "",
                        "result": ""
            　　}
            }
            返金成功時のデータ：
            {
              "meta": {
                    "code": "06",
                    "message": "SUCCESS"
              },
              "data": {
                    "errorCode": "",
                    "errorInfo": "",
                    "result": ""
              }
            }
            */

            String resultMessage = null;//最終結果情報
            try {
                //metaJSON JSONObjectを取得
                JSONObject metaJSON = jsonResult.getJSONObject("meta");
                //metaJSON　から　code の文字を取得
                String metaCode = metaJSON.getString("code");
                //支払成功のは"code":"00"、返金成功のは"code":"06"
                if ("00".equals(metaCode)) {
                    resultMessage = getString(R.string.msg0017);
                } else {
                    //失敗:メッセージを取得
                    JSONObject dataJSON = jsonResult.getJSONObject("data");
                    resultMessage = dataJSON.getString("errorInfo");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                resultMessage = getString(R.string.msg0016);
            }
            return resultMessage;
        }
    }

    //########### 内部クラス   ##################

    //処理区分により、画面の文字を初期化
    private void setLayout() {

        if (processKbn == 1) {
            //決済

            ((TextView)findViewById(R.id.lab_PayAmount)).setText(R.string.lab_Amount);
            ((TextView)findViewById(R.id.lab_PayCardNo)).setText(R.string.lab_customId);
            ((Button)findViewById(R.id.btn_Seed)).setText(R.string.button_seed);

        } else {
            //返金

            ((TextView)findViewById(R.id.lab_PayAmount)).setText(R.string.cancel_lab_Amount);
            ((TextView)findViewById(R.id.lab_PayCardNo)).setText(R.string.cancel_lab_customId);
            ((Button)findViewById(R.id.btn_Seed)).setText(R.string.button_cancel);
        }
    }

    //画面のデータを初期化
    private void initLayout() {
        radioGroup.check(radioGroup.getCheckedRadioButtonId());
        customDataTxt.setText("");
        amountDataTxt.setText("");
        statusTextView.setText("");
        startRead = false;
    }

    //フォーカスを設定
    private void setFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    //決済センターに送信
    private void doSeed() {

        Context context = this.getApplicationContext();

        AlertDialog.Builder builder = CommAlertDialogBuilder.SetDialog(3, R.string.msg0005,context);
        // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        builder.setPositiveButton(R.string.msg_return1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        System.out.println(" ##############JLM###　＄＄　決済送信　肯定ボタンがクリックされた  ");

                        //TODO 決済送信データを取得…

                        //WebAPIを読んで、決済送信を処理します

                        int payKbnRadioBtnId = radioGroup.getCheckedRadioButtonId();
                        String payCompanyKbn = null;
                        if (payKbnRadioBtnId == R.id.radioButtonPayCpNm1) {
                            payCompanyKbn = "alipay";
                        } else if (payKbnRadioBtnId == R.id.radioButtonPayCpNm2) {
                            payCompanyKbn = "wechatpay";
                        }

                        CommPayBean payBean = new CommPayBean();
                        payBean.setPayCompanyKbn(payCompanyKbn);
                        payBean.setAmount(Integer.parseInt(amountDataTxt.getText().toString()));
                        payBean.setUserCode(customDataTxt.getText().toString());
                        payBean.setShopCode("88888888");
                        if (processKbn == 1) {
                            //決済
                            payBean.setSeedKbn("0");//決済
                        } else {
                            //返金
                            payBean.setSeedKbn("1");//返金
                        }

                        //決済Jsonを作成
                        JSONObject json = getJson(payBean);

                        new DoPayment().execute(json);

                        //画面のデータを初期化
                        initLayout();

                        //フォーカスを設定
                        setFocus(amountDataTxt);

                        statusTextView.setText(R.string.msg0012);

                        //画面をロック
//                        RelativeLayout page2=(RelativeLayout) findViewById(R.id.page2);
//                        page2.setVisibility(View.VISIBLE);
//                        page2.setOnClickListener(null);

                    }
                });

        // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        builder.setNegativeButton(R.string.msg_return2,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        System.out.println(" ##############JLM###　＄＄　決済送信　否定ボタンがクリックされた  ");

                        //フォーカスを設定
                        setFocus(amountDataTxt);

                        statusTextView.setText(R.string.msg0013);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //JSONデータを作成
    private JSONObject getJson(CommPayBean payBean) {

        //TODO 決済送信Jsonをセット…

        JSONObject json = new JSONObject();
        try {
            json.put("payType", payBean.getPayCompanyKbn());
            json.put("proKbn", payBean.getSeedKbn());
            json.put("userCode", payBean.getUserCode());
            json.put("amount", payBean.getAmount());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    //決済センターに送信前のチェック
    private boolean doSeedCheck() {

        //必須条件を判断
        int payKbn = radioGroup.getCheckedRadioButtonId();
        if (payKbn == -1) {//選択していない
            statusTextView.setText(R.string.msg0006);
            //フォーカスを設定
            this.setFocus(radioGroup);
            return false;
        }
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

    //起動前のチェック
    private boolean doStartCheck() {

        //必須条件を判断
        int payKbn = radioGroup.getCheckedRadioButtonId();
        if (payKbn == -1) {//選択していない
            statusTextView.setText(R.string.msg0006);
            //フォーカスを設定
            this.setFocus(radioGroup);
            return false;
        }
        if ( TextUtils.isEmpty(amountDataTxt.getText())) {//データがない
            statusTextView.setText(R.string.msg0007);
            //フォーカスを設定
            this.setFocus(amountDataTxt);
            return false;
        }
        return true;
    }

    //スキャンナーを終了
    private void scanEnd() {

        //終了中…
        ending = true;

        System.out.println(" ##############JLM#####　＄＄＄＄＄＄＄　スキャンナーを終了処理;  ");


        ending = false;
    }


    public Context getApplicationContext() {
        return this;
    }

    //################## private #################################################

    /*

    //客様の決済コードが変わったときのイベント
    private class onCustomDataChanged implements TextWatcher {
        public void afterTextChanged(Editable arg0) {
        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int txtLength = s.length();
            if (txtLength == 0) {
                return;
            }
            Context context = getApplicationContext();

            Toast.makeText(context, txtLength + " bit data is changed by onCustomDataChanged", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = CommAlertDialogBuilder.SetDialog(3, R.string.msg0005,context);
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            builder.setPositiveButton(R.string.msg_return1,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            System.out.println(" ##############JLM###　＄＄　決済送信　肯定ボタンがクリックされた  ");
                            scanEnd();

                            //画面のデータを初期化
                            initLayout();

                            amountDataTxt.setFocusable(true);
                            amountDataTxt.setFocusableInTouchMode(true);
                            amountDataTxt.requestFocus();

                            statusTextView.setText("決済送信をやりました??");
                        }
                    });

            // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            builder.setNegativeButton(R.string.msg_return2,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            System.out.println(" ##############JLM###　＄＄　決済送信　否定ボタンがクリックされた  ");
                            scanEnd();

                            //画面のデータを初期化
                            initLayout();

                            amountDataTxt.setFocusable(true);
                            amountDataTxt.setFocusableInTouchMode(true);
                            amountDataTxt.requestFocus();

                            statusTextView.setText("決済送信を取消しました。");
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    */

}
