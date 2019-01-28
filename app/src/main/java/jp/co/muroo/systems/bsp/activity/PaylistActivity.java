package jp.co.muroo.systems.bsp.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;
import jp.co.muroo.systems.bsp.comm.CommPayDetailBean;
import jp.co.muroo.systems.bsp.comm.CommUtil;

/**
 * 決済・返金照会一覧画面　「使っていない」
 */
public class PaylistActivity extends Activity {

    public MspApplication mspApp = null;
    //1:決済照会　2:返金照会
    private int processKbn = 0;

    CommUtil commUtil = null;

    private TextView payDateStart = null;
    private TextView payDateEnd = null;

    StringBuffer stringBuilderDatetime;

    private int lastVisibleItemPosition = 0;// 标记上次滑动位置，初始化默认为0
    private boolean scrollFlag = false;// 标记是否滑动

    // idがlistのListViewを取得
    ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylist);

        payDateStart = findViewById(R.id.txtStartDate);
        payDateEnd = findViewById(R.id.txtEndDate);
        listView = findViewById(R.id.payListView);

        mspApp = (MspApplication) this.getApplication();
        commUtil = CommUtil.getInstance();
        processKbn =  mspApp.getProcessKbn();

        //処理区分により、画面の文字を初期化
        this.setLayout();

        //onChangedをセット
        payDateStart.addTextChangedListener(new DateTextWatcher(payDateStart));
        payDateEnd.addTextChangedListener(new DateTextWatcher(payDateEnd));

        //スクロールしたときに表示するべきデータを選別できるようにします。
        //クラスにListView.OnScrollListenerインタフェースを実装します。
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            /**
             * スクロールの状態を検知する
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                switch (scrollState) {
                    // スクロールしていない
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时

                        // スクロール中であることを示すフラグをfalseに
                        scrollFlag = false;

                        // 判断滚动到底部 、position是从0开始算起的
                        if (view.getLastVisiblePosition() == (listView.getCount() - 1)) {
                        }
                        // 判断滚动到顶部
                        if (view.getFirstVisiblePosition() == 0) {
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        scrollFlag = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        // 当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时，即滚动时
                        scrollFlag = true;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //当滑动时
                if (scrollFlag) {
                    if (firstVisibleItem < lastVisibleItemPosition) {
                        // 上滑
                    } else if (firstVisibleItem > lastVisibleItemPosition) {
                        // 下滑
                    } else {
                        return;
                    }
                    lastVisibleItemPosition = firstVisibleItem;//更新位置
                }
            }
        });

        //明細データを検索処理
        this.doSearch();
    }
    /**
     * 明細データを検索処理
     * @return
     */
    private void doSearch() {
        //Jsonを作成
        JSONObject json = this.setSearchJson();
        Log.i("PaylistActivity","送信Json is: " + json.toString());
        //ログイン送信は非同期で処理します
        new DoGetListData().execute(json);
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
            json.put("procStartDateTime", payDateStart.getText().toString() + ":00");
            json.put("procEndDateTime", payDateEnd.getText().toString() + ":59");

        } catch (JSONException e) {
            Log.d("PaylistActivity", e.toString());
        }
        return json;
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
    public void setPayList(JSONObject jsonResult) throws JSONException {

        // ListViewに表示する項目を生成
        ArrayList<CommPayDetailBean> beanListData = new ArrayList<>();
        List<Map<String, String>> listData = new ArrayList<Map<String,String>>();
        JSONArray listBeans =  jsonResult.getJSONArray("ProcBeans");

        for (int i = 0; i < listBeans.length(); i++) {
            //jsonResult から　code の文字を取得
            JSONObject jsonBean = listBeans.getJSONObject(i);

            String payAmount = jsonBean.getString("Amount");
            if (payAmount == null || "".equals(payAmount.trim())) {
                payAmount = "0";
            }

            String payCancelAmount = jsonBean.getString("RefundAmount");
            if (payCancelAmount == null || "".equals(payCancelAmount.trim())) {
                payCancelAmount = "0";
            }


            String payOrderId = jsonBean.getString("ProcId");
            String payDateTime = jsonBean.getString("ProcessDateTime");
            String payUserId = jsonBean.getString("UserId");
            String payDeviceId = jsonBean.getString("TerminalId");

            String payCompanyKbn = jsonBean.getString("SystemId");
            String payCompanyNm = commUtil.getPayCompanyNm(payCompanyKbn);

            //for test
            /*
            String payAmount = "¥234円";
            String payOrderId = "12201812231234567"+i;
            String payDateTime = "2018-12-20 12:20:30";
            String payUserId = "UserId001";
            String payDeviceId = "12201812231234567";
            String payCompanyKbn = "01";
            String payCompanyNm = commUtil.getPayCompanyNm(payCompanyKbn);
            */

            CommPayDetailBean payBean = new CommPayDetailBean();
            payBean.setPayAmount(Integer.parseInt(payAmount));
            payBean.setPayCancelAmount(Integer.parseInt(payCancelAmount));
            payBean.setPayOrderId(payOrderId);
            payBean.setPayDateTime(payDateTime);
            payBean.setPayUserId(payUserId);
            payBean.setPayDeviceId(payDeviceId);
            payBean.setPayCompany(payCompanyNm);
            beanListData.add(payBean);

            HashMap<String,String> data = new HashMap<>();
            // 引数には、(名前,実際の値)という組合せで指定します　名前はSimpleAdapterの引数で使用します
            data.put("lineId", Integer.toString(i));
            data.put("payOrderId", getString(R.string.lab_payDetail_orderId) + payOrderId);

            String payAmountStr = "¥" + payAmount +"円";
            String payCancelAmountStr = "¥" + payCancelAmount +"円";
            data.put("payAmount", getString(R.string.lab_payDetail_amount) + payAmountStr);
            data.put("payCancelAmount", getString(R.string.lab_payCancel_amount) + payCancelAmountStr);

            data.put("payCompanyNm", getString(R.string.lab_payDetail_compang) + payCompanyNm);
            data.put("payDateTime", getString(R.string.lab_payDetail_dateTime) + payDateTime);
            listData.add(data);
        }

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
                new String[]{"lineId","payOrderId","payAmount","payCancelAmount","payCompanyNm", "payDateTime"}, // どの項目を
                new int[]{R.id.payListId, R.id.payListOrderId, R.id.payListAmount, R.id.payListCancelAmount, R.id.payListCompany, R.id.payListDateTime} // どのidの項目に入れるか
        );

        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(true);

        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new ListItemClickListener());
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
        payDateStart.setText(mspApp.getPayDateStart());//yyMM-dd-HH mm:ss
        payDateEnd.setText(mspApp.getPayDateEnd());//yyMM-dd-HH mm:ss
    }

    /**
     * カレンダーを表示「日付と時間を選択」
     * @param dateView
     */
    private void showCalendar(TextView dateView) {

        Calendar c = Calendar.getInstance();
        Dialog dateDialog = new DatePickerDialog(PaylistActivity.this, (arg0, year, month, day) -> {
            stringBuilderDatetime = new StringBuffer("");
            stringBuilderDatetime.append(year + "-" + ((month+1) < 10 ? "0"+ (month+1) : (month+1)+"") + "-" + (day < 10 ? "0"+ day : day));

            Calendar time = Calendar.getInstance();
            Dialog timeDialog = new TimePickerDialog(PaylistActivity.this, (view, hourOfDay, minute) -> {
                stringBuilderDatetime.append(" " + (hourOfDay < 10 ? "0"+ hourOfDay : hourOfDay) + ":"+(minute < 10 ? "0"+ minute : minute));
                //項目にデータをセット
                dateView.setText(stringBuilderDatetime);//yyMM-dd-HH mm:ss
            }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true);
            timeDialog.show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dateDialog.show();
    }

    //########### 内部クラス  ##################
    /**
     * APIに送信クラス　 「非同期処理」
     */
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
            result = commUtil.doPost(getString(R.string.msp_pay_ist_url), params[0], LoginActivity.class.getName());
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
                    mspApp.setResultKbn("99");//結果処理区分（99：失敗）
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
                Log.d(PaylistActivity.class.getName(), ex.getLocalizedMessage());
                resultMessage = getString(R.string.msg0016);
                //失敗
                mspApp.setResultKbn("99");//結果処理区分（99：失敗）
            }

            //処理が完了
            if (("00").equals(mspApp.getResultKbn())) {
                //正常
                //何もしません
            } else if (("-1").equals(mspApp.getResultKbn())) {//-1:データが無し
                Toast.makeText(PaylistActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
            } else if (("-2").equals(mspApp.getResultKbn())) {//-2:結果データがオーバー
                //データは最大件数を越えたら
                Toast.makeText(PaylistActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
            } else if (("-3").equals(mspApp.getResultKbn())) {
                //ログインの有効期限が切れてる
                Toast.makeText(PaylistActivity.this, resultMessage, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                //異常メッセージを表示
                Toast.makeText(PaylistActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
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

            if ("01".equals(resultCode) || "19".equals(resultCode)) {
                //01:成功 19:件数オーバー
                mspApp.setResultKbn("00");

                String tokenStr = jsonResult.getString("Token");
                String serverKeyStr = jsonResult.getString("ServerKey");

                //基本データをセット存する
                mspApp.setToken(tokenStr);
                mspApp.setServerDigitalSignature(serverKeyStr);

                if (!commUtil.checkDigitalSignature(mspApp.getServerDigitalSignature())) {
                    //デジタル署名　チェックします
                    mspApp.setResultKbn("99");//結果処理区分（99：失敗）
                    resultMessage = getString(R.string.msg0019);
                } else {

                    String resultCount = jsonResult.getString("ResultCount");
                    //明細データをセット(データが無し時、明細をクリアします)
                    setPayList(jsonResult);

                    if ("0".equals(resultCount)) {
                        mspApp.setResultKbn("-1");//結果データがない
                        resultMessage = getString(R.string.msg0010);
                    }
                    if ("19".equals(resultCode)) {
                        mspApp.setResultKbn("-2");//結果データがオーバー
                    }
                }
            } else if ("13".equals(resultCode)) {
                //ログインの有効期限が切れてる
                mspApp.setResultKbn("-3");//結果処理区分（-3：ログインの有効期限が切た）
                resultMessage = getString(R.string.msg0021);
            } else {
                //失敗
                mspApp.setToken(jsonResult.getString("Token"));
                mspApp.setResultKbn("99");//結果処理区分（99：失敗）
            }
            return resultMessage;
        }
    }

    /**
     * リストが選択されたときの処理
     */
    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //区分をセット
            if (processKbn == 1) { //決済
                mspApp.setPayResultKbn("12");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細）
            } else {  //返金
                mspApp.setPayResultKbn("22");
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
            intent.putExtra("payCancelAmount", payBean.getPayCancelAmount());
            intent.putExtra("payUserId", payBean.getPayUserId());
            intent.putExtra("payOrderId", payBean.getPayOrderId());
            intent.putExtra("payDeviceId", payBean.getPayDeviceId());
            intent.putExtra("payDateTime", payBean.getPayDateTime());

            mspApp.setPayDateStart(payDateStart.getText().toString());
            mspApp.setPayDateEnd(payDateEnd.getText().toString());

            startActivity(intent);
        }
    }

    /**
     * EditText テキストの変更 Listener
     */
    private class DateTextWatcher implements TextWatcher {

        // 通知するためのエディットボックス
        private final TextView mNotifyEditText;
        //修正前の文字
        private String beforeTextStr = "";
        //修正後の文字
        private String afterTextStr = "";

        // コンストラクタ
        public DateTextWatcher(final TextView notifyEditText) {
            this.mNotifyEditText = notifyEditText;
        }

        ////////////////////////////////////////////////////////////
        // テキスト変更前
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after) {
            Log.v(TextWatcher.class.getSimpleName(),
                    "beforeTextChanged s:" + s.toString());
            this.beforeTextStr = s.toString();
        }

        ////////////////////////////////////////////////////////////
        // テキスト変更中
        public void onTextChanged(CharSequence s, int start,
                                  int before, int count) {
            Log.v(TextWatcher.class.getSimpleName(),
                    "onTextChanged s: " + s.toString());
        }

        ////////////////////////////////////////////////////////////
        // テキスト変更後
        public void afterTextChanged(Editable s) {
            Log.v(TextWatcher.class.getSimpleName(),
                    "afterTextChanged s: " + s.toString());
            this.afterTextStr = s.toString();

            if (!this.beforeTextStr.equals(this.afterTextStr)) {
                //データが変わりました
                //明細データを検索処理
                doSearch();
            }
        }
    }

    //########### 内部クラス End  ##################

}
