package jp.co.muroo.systems.bsp.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;
import jp.co.muroo.systems.bsp.comm.CommPayDetailBean;
import jp.co.muroo.systems.bsp.comm.CommUtil;

/**
 * 決済・返金照会一覧画面
 */
public class PaylistActivity extends Activity {

    public MspApplication mspApp = null;
    //1:決済照会　2:返金照会
    private int processKbn = 0;

    CommUtil commUtil = null;

    private TextView payDateStart = null;
    private TextView payDateEnd = null;
    StringBuffer stringBuilderDatetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylist);

        payDateStart = findViewById(R.id.txtStartDate);
        payDateEnd = findViewById(R.id.txtEndDate);

        mspApp = (MspApplication) this.getApplication();

        commUtil = CommUtil.getInstance();

        processKbn =  mspApp.getProcessKbn();
        //処理区分により、画面の文字を初期化
        this.setLayout();
        /*
        //Jsonを作成
        JSONObject json = this.setSearchJson();
        Log.i("PaylistActivity","送信Json is: " + json.toString());
        //ログイン送信は非同期で処理します
        new DoGetListData().execute(json);
        */
        //For test
        this.setPayList(null);
    }

    /**
     * JSONデータを作成
     * @return
     */
    private JSONObject setSearchJson() {

        JSONObject json = new JSONObject();
        try {
            //デジタル署名を作成
            mspApp.setDigitalSignature(commUtil.getDigitalSignatureStr());

            json.put("terminalId", mspApp.getDeviceId());
            json.put("userId", mspApp.getUserId());
            json.put("token", mspApp.getToken());
            json.put("userKey", mspApp.getDigitalSignature());
            if (processKbn == 1) { //決済
                json.put("procMode", "01");
            } else {  //返金
                json.put("procMode", "02");
            }
            json.put("payStartDateTime", payDateStart.getText().toString());
            json.put("payEndDateTime", payDateEnd.getText().toString());

        } catch (JSONException e) {
            Log.d("PaylistActivity", e.toString());
        }
        return json;
    }

    /**
     * リストが選択されたときの処理
     */
    private class ListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //区分をセット
            if (processKbn == 1) { //決済
                mspApp.setResultKbn("12");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
            } else {  //返金
                mspApp.setResultKbn("22");
            }
            //選択した行Noを取得
            Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);
            String lineIdStr = item.get("lineId");
            int lineId = Integer.parseInt(lineIdStr);

            //明細行データを取得
            CommPayDetailBean payBean = mspApp.getPayDataList().get(lineId);

            //詳細画面へ移動します
            Intent intent = new Intent(PaylistActivity.this, DetailActivity.class);
            intent.putExtra("payCompany", payBean.getPayCompany());
            intent.putExtra("payAmount", payBean.getPayAmount());
            intent.putExtra("payUserId", payBean.getPayUserId());
            intent.putExtra("payOrderId", payBean.getPayOrderId());
            intent.putExtra("payDeviceId", payBean.getPayDeviceId());
            intent.putExtra("payDateTime", payBean.getPayDateTime());

            startActivity(intent);
        }
    }

    /**
     *処理区分により、画面の文字を初期化
     */
    private void setLayout() {
        if (processKbn == 1) {
            //決済
            this.setTitle(R.string.head_title_name7);//タイトルバーの文字列
        } else {
            //返金
            this.setTitle(R.string.head_title_name8);//タイトルバーの文字列
        }
        this.setInitDatetime();
    }

    /**
     * 明細データをセット
     * @param jsonResult
     * @return
     * @throws JSONException
     */
    public void setPayList(JSONObject jsonResult) {


        //TODO setPayList(JSONObject jsonResult)

        // ListViewに表示する項目を生成
        ArrayList<CommPayDetailBean> beanListData = new ArrayList<>();
        List<Map<String, String>> listData = new ArrayList<Map<String,String>>();

        for (int i = 0; i < 3; i++) {
            //jsonResult から　code の文字を取得
            /*
            String viewPayAmount = jsonResult.getString("Amount");
            viewPayAmount = "¥" + viewPayAmount +"円";

            String viewPayOrderId = jsonResult.getString("ProcId");
            String viewPayDateTime = jsonResult.getString("ProcessDateTime");
            String viewPayUserId = jsonResult.getString("UserId");
            String viewPayDeviceId = jsonResult.getString("TerminalId");

            String payCompanyKbn = jsonResult.getString("SystemId");
            String payCompanyNm = commUtil.getPayCompanyNm(payCompanyKbn);
*/
//for test
            String payAmount = "¥234円";
            String payOrderId = "12201812231234567"+i;
            String payDateTime = "2018-12-20 12:20:30";
            String payUserId = "UserId001";
            String payDeviceId = "12201812231234567";
            String payCompanyKbn = "01";
            String payCompanyNm = commUtil.getPayCompanyNm(payCompanyKbn);

            HashMap<String,String> data = new HashMap<>();
            // 引数には、(名前,実際の値)という組合せで指定します　名前はSimpleAdapterの引数で使用します
            data.put("payOrderId", payOrderId);
            data.put("payAmount", payAmount);
            data.put("lineId", Integer.toString(i));
            data.put("payCompanyNm", payCompanyNm);
            data.put("payDateTime", payDateTime);
            listData.add(data);

            CommPayDetailBean payBean = new CommPayDetailBean();
            payBean.setPayAmount(payAmount);
            payBean.setPayOrderId(payOrderId);
            payBean.setPayDateTime(payDateTime);
            payBean.setPayUserId(payUserId);
            payBean.setPayDeviceId(payDeviceId);
            payBean.setPayCompany(payCompanyNm);
            beanListData.add(payBean);
        }

        if (beanListData.isEmpty()) {
            //データがなしのメッセージを表示
            Toast.makeText(PaylistActivity.this, getString(R.string.msg0021), Toast.LENGTH_LONG).show();
        } else {
            //明細データを保存
            mspApp.setPayDataList(beanListData);

            /*
             * Adapterを生成
             * R.layout.custom_list_layout : リストビュー自身のレイアウト。今回は自作。
             * new String[]{***} : 受け渡し元項目名
             * new int[]{***} : 受け渡し先ID
             */
            SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                    listData, // 使用するデータ
                    R.layout.pay_list_item, // 自作したレイアウト
                    new String[]{"payOrderId","payAmount","lineId","payCompanyNm", "payDateTime"}, // どの項目を
                    new int[]{R.id.payListOrderId, R.id.payListAmount, R.id.payListId, R.id.payListCompany, R.id.payListDateTime} // どのidの項目に入れるか
            );
            // idがlistのListViewを取得
            ListView listView = (ListView) findViewById(R.id.payListView);
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new ListItemClickListener());
        }
    }


    /**
     * 開始日付をセット
     * @param view
     */
    public void selectStartDate(View view) {
        this.showCalendar(payDateStart);
    }

    /**
     * 終了日付をセット
     * @param view
     */
    public void selectEndDate(View view) {
        this.showCalendar(payDateEnd);
    }


    /**
     * 初期化の日付と時間をセット
     */
    private void setInitDatetime() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00");
        String dateStr = sdf.format(date);
        payDateStart.setText(dateStr);//yyMM-dd-HH mm:ss

        sdf = new SimpleDateFormat("yyyy-MM-dd 23:59");
        dateStr = sdf.format(date);
        payDateEnd.setText(dateStr);//yyMM-dd-HH mm:ss

    }

    /**
     * カレンダーを表示「日付と時間を選択」
     * @param dateView
     */
    private void showCalendar(TextView dateView) {

        Calendar c = Calendar.getInstance();
        Dialog dateDialog = new DatePickerDialog(PaylistActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                stringBuilderDatetime = new StringBuffer("");
                stringBuilderDatetime.append(year + "-" + ((month+1) < 10 ? "0"+ (month+1) : (month+1)+"") + "-" + (day < 10 ? "0"+ day : day));

                Calendar time = Calendar.getInstance();
                Dialog timeDialog = new TimePickerDialog(PaylistActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        stringBuilderDatetime.append(" " + (hourOfDay < 10 ? "0"+ hourOfDay : hourOfDay) + ":"+(minute < 10 ? "0"+ minute : minute));
                        //項目にデータをセット
                        dateView.setText(stringBuilderDatetime);//yyMM-dd-HH mm:ss
                    }
                }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true);
                timeDialog.show();
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }


    //########### 内部クラス 「非同期処理」  ##################
    // AsyncTask that configures the scanned data on background
    // thread and updated the result on UI thread with scanned data and type of label
    //その際、AsyncTaskにジェネリクスを3個指定する必要があります。
    // これは、AsyncTaskを継承したクラス内のメソッドの引数や戻り値の型を指定するためです。
    //ログイン送信クラス
    private class DoGetListData extends AsyncTask<JSONObject, String, String> {
        /**
         * 決済送信
         * @param params
         * @return
         */
        @Override
        public String doInBackground(JSONObject... params) {
            String result;
            //POSTで　WebAPIに決済送信
            result = commUtil.doPost(getString(R.string.msp_pay_ist_url), params[0], MainActivity.class.getName());
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
                Log.d(PaylistActivity.class.getName(), ex.getLocalizedMessage());
                resultMessage = getString(R.string.msg0016);
                //失敗
                mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
            }
            //処理が完了
            if (mspApp.getResultKbn() != null && !("99").equals(mspApp.getResultKbn())) {
                //結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
            } else {
                //異常メッセージを表示
                Toast.makeText(PaylistActivity.this, resultMessage, Toast.LENGTH_LONG).show();
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
                String serverKeyStr = jsonResult.getString("ServerKey");

                //基本データをセット存する
                mspApp.setToken(tokenStr);
                mspApp.setServerDigitalSignature(serverKeyStr);

                if (!commUtil.checkDigitalSignature(mspApp.getServerDigitalSignature())) {
                    //デジタル署名　チェックします
                    mspApp.setResultKbn("99");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
                    resultMessage = getString(R.string.msg0019);
                } else {
                    mspApp.setResultKbn("00");//WebAPI処理 正常

                    //明細データをセット
                    setPayList(jsonResult);
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
