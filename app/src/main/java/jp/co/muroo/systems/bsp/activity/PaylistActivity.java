package jp.co.muroo.systems.bsp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;
import jp.co.muroo.systems.bsp.comm.CommUtil;

/**
 * 決済・返金照会一覧画面
 */
public class PaylistActivity extends Activity {

    public MspApplication mspApp = null;
    //1:決済照会　2:返金照会
    private int processKbn = 0;

    CommUtil commUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylist);

        mspApp = (MspApplication) this.getApplication();

        commUtil = CommUtil.getInstance();

        processKbn =  mspApp.getProcessKbn();
        //処理区分により、画面の文字を初期化
        this.setLayout();

        List<Map<String, String>> list = new ArrayList<Map<String,String>>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "大阪");
        map.put("id", "270000");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "神戸");
        map.put("id", "280010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "豊岡");
        map.put("id", "280020");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "京都");
        map.put("id", "260010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "舞鶴");
        map.put("id", "260020");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "奈良");
        map.put("id", "290010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "風屋");
        map.put("id", "290020");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "和歌山");
        map.put("id", "300010");
        list.add(map);
        map = new HashMap<String, String>();
        map.put("name", "潮岬");
        map.put("id", "300020");
        list.add(map);

        ListView lvCityList = (ListView) findViewById(R.id.payListView);

        String[] from = {"name"};
        int[] to = {android.R.id.text1};
        SimpleAdapter adapter = new SimpleAdapter(PaylistActivity.this, list, android.R.layout.simple_expandable_list_item_1, from, to);

        lvCityList.setAdapter(adapter);
        lvCityList.setOnItemClickListener(new ListItemClickListener());

        //Jsonを作成
        JSONObject json = this.getLoginJson(mspApp);
        Log.i("PaylistActivity","送信Json is: " + json.toString());
        //ログイン送信は非同期で処理します
    //    new DoGetListData().execute(json);
    }

    /**
     * JSONデータを作成
     * @return
     */
    private JSONObject getLoginJson(MspApplication mspApp) {

        JSONObject json = new JSONObject();
        try {
            json.put("terminalId", mspApp.getDeviceId());
            json.put("userId", mspApp.getUserId());
            json.put("UserKey", mspApp.getDigitalSignature());
        } catch (JSONException e) {
            Log.d("PaylistActivity", e.getLocalizedMessage());
        }
        return json;
    }

    /**
     * リストが選択されたときの処理が記述されたメンバクラス。
     */
    private class ListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);
            String cityName = item.get("name");
            String cityId = item.get("id");

            if (processKbn == 1) { //決済
                mspApp.setResultKbn("12");//結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
            } else {  //返金
                mspApp.setResultKbn("22");
            }

            //詳細画面へ移動します
            Intent intent = new Intent(PaylistActivity.this, DetailActivity.class);
            intent.putExtra("payCompany", cityName);
            intent.putExtra("payAmount", cityId);
            intent.putExtra("payUserId", "test6");
            intent.putExtra("payOrderId", "test6");
            intent.putExtra("payDeviceId", "test6");
            intent.putExtra("payDateTime", "test6");

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
    }


    private void setPayList(JSONObject jsonResult) throws JSONException {

    }

    /**
     * 開始日付をセット
     * @param view
     */
    public void selectStartDate(View view) {

    }

    /**
     * 終了日付をセット
     * @param view
     */
    public void selectEndDate(View view) {

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
                //結果画面に遷移します。
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            } else {
                //
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

}
