package jp.co.muroo.systems.bsp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;

public class DetailActivity extends Activity {

    public MspApplication mspApp = null;

    private TextView viewShopNm = null;
    private TextView viewShopInfo = null;

    private TextView viewPayKbn = null;
    private TextView viewPayCompany = null;
    private TextView viewPayAmount = null;
    private TextView viewPayUserId = null;
    private TextView viewPayOrderId = null;
    private TextView viewPayDeviceId = null;
    private TextView viewPayDateTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mspApp = (MspApplication) this.getApplication();

        viewShopNm = findViewById(R.id.textViewShopNm);
        viewShopInfo = findViewById(R.id.textViewShopInfo);

        viewPayKbn = findViewById(R.id.textViewPayKbn);
        viewPayCompany = findViewById(R.id.textViewPayCompany);
        viewPayAmount = findViewById(R.id.textViewPayAmount);
        viewPayUserId = findViewById(R.id.textViewPayUserId);
        viewPayOrderId = findViewById(R.id.textViewPayOrderId);
        viewPayDeviceId = findViewById(R.id.textViewPayDeviceId);
        viewPayDateTime = findViewById(R.id.textViewPayDateTime);

        viewShopNm.setText(mspApp.getShopName());
        viewShopInfo.setText(mspApp.getShopInfo());

        //結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 99：失敗）
        if ("11".equals(mspApp.getResultKbn())) {
            viewPayKbn.setText("支払成功");
        } else if ("21".equals(mspApp.getResultKbn())) {
            viewPayKbn.setText("返金成功");
        } else if ("12".equals(mspApp.getResultKbn())) {
            viewPayKbn.setText("支払詳細");
        } else if ("22".equals(mspApp.getResultKbn())) {
            viewPayKbn.setText("返金詳細");
        }
        viewPayCompany.setText(mspApp.getPayCompany());
        viewPayAmount.setText("\\"+String.valueOf(mspApp.getPayAmount())+"円");
        viewPayUserId.setText(mspApp.getUserId());
        viewPayOrderId.setText(mspApp.getPayOrderId());
        viewPayDeviceId.setText(mspApp.getDeviceId());
        viewPayDateTime.setText(mspApp.getPayProcessDateTime());
    }

    /**
     * 印刷ボタンの処理
     * @param view
     */
    public void printClick(View view) {
        this.doPrint();
    }

    /**
     * 完了ボタンの処理
     * @param view
     */
    public void endClick(View view) {

        this.clearPayedInfo();

        //結果画面に遷移します。
        Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
        startActivity(intent);
    }

    private void clearPayedInfo() {
        mspApp.setResultKbn("");
        mspApp.setPayCompany("");
        mspApp.setPayAmount(0);
        mspApp.setPayOrderId("");
        mspApp.setPayProcessDateTime("");
    }

    /**
     * レシートを印刷します
     */
    private void doPrint() {
    }
}
