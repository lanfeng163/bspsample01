package jp.co.muroo.systems.bsp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.printerhelper.utils.AidlUtil;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;

/**
 * 照会画面（共通：決済結果、返金結果、決済詳細、返金詳細）
 */
public class DetailActivity extends Activity {

    public MspApplication mspApp = null;

    private TextView viewShopNm = null;
    private TextView viewShopInfo = null;
    private TextView viewShopTel = null;

    private TextView viewPayKbn = null;
    private TextView viewPayCompany = null;
    private TextView viewPayAmount = null;
    private TextView viewPayCancelAmount = null;
    private TextView viewPayUserId = null;
    private TextView viewPayOrderId = null;
    private TextView viewPayDeviceId = null;
    private TextView viewPayDateTime = null;

    private Button buttonCancel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mspApp = (MspApplication) this.getApplication();

        mspApp.setListToCancel(false);

        //遷移先により、データをセット Start
        if ("12".equals(mspApp.getPayResultKbn()) || "22".equals(mspApp.getPayResultKbn())) {
            //一覧画面からの場合
            Intent intent = getIntent();

            int payAmount = 0;
            int payCancelAmount = 0;
            payAmount = intent.getIntExtra("payAmount",0);
            payCancelAmount = intent.getIntExtra("payCancelAmount", 0);

            mspApp.setPayAmount(payAmount);
            mspApp.setPayCancelAmount(payCancelAmount);

            mspApp.setPayCompany(intent.getStringExtra("payCompany"));
            mspApp.setPayUserId(intent.getStringExtra("payUserId"));
            mspApp.setPayOrderId(intent.getStringExtra("payOrderId"));
            mspApp.setPayDeviceId(intent.getStringExtra("payDeviceId"));
            mspApp.setPayProcessDateTime(intent.getStringExtra("payDateTime"));

        } else {
            //処理画面からの場合
        }

        //遷移先により、データをセット End

        viewShopNm = findViewById(R.id.textViewShopNm);
        viewShopInfo = findViewById(R.id.textViewShopInfo);
        viewShopTel = findViewById(R.id.textViewShopTel);
        viewPayKbn = findViewById(R.id.textViewPayKbn);
        viewPayCompany = findViewById(R.id.textViewPayCompany);
        viewPayAmount = findViewById(R.id.textViewPayAmount);
        viewPayCancelAmount = findViewById(R.id.textViewPayCancelAmount);
        viewPayUserId = findViewById(R.id.textViewPayUserId);
        viewPayOrderId = findViewById(R.id.textViewPayOrderId);
        viewPayDeviceId = findViewById(R.id.textViewPayDeviceId);
        viewPayDateTime = findViewById(R.id.textViewPayDateTime);

        buttonCancel = findViewById(R.id.buttonPayCancel);

        viewShopNm.setText(mspApp.getShopName());
        viewShopInfo.setText(mspApp.getShopInfo());
        viewShopTel.setText(mspApp.getShopTel());

        //結果処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 ）
        if ("11".equals(mspApp.getPayResultKbn())) {
            viewPayKbn.setText(getString(R.string.lab_payDetail_kbn_value1));
            this.setTitle(R.string.head_title_name5);//タイトルバーの文字列
            // ボタンが利用可
            buttonCancel.setEnabled(true);
            buttonCancel.setVisibility(View.VISIBLE);
        } else if ("21".equals(mspApp.getPayResultKbn())) {
            viewPayKbn.setText(getString(R.string.lab_payDetail_kbn_value2));
            this.setTitle(R.string.head_title_name6);//タイトルバーの文字列
            // ボタンが利用不可
            buttonCancel.setEnabled(false);
            buttonCancel.setVisibility(View.GONE);
        } else if ("12".equals(mspApp.getPayResultKbn())) {
            if (mspApp.getPayAmount() == mspApp.getPayCancelAmount()) {//全返金した
                viewPayKbn.setText(getString(R.string.lab_payDetail_kbn_value4));
            } else {
                viewPayKbn.setText(getString(R.string.lab_payDetail_kbn_value3));
            }
            this.setTitle(R.string.head_title_name9);//タイトルバーの文字列
            // ボタンが利用可
            buttonCancel.setEnabled(true);
            buttonCancel.setVisibility(View.VISIBLE);
        } else if ("22".equals(mspApp.getPayResultKbn())) {
            viewPayKbn.setText(getString(R.string.lab_payDetail_kbn_value4));
            this.setTitle(R.string.head_title_name10);//タイトルバーの文字列
            // ボタンが利用不可
            buttonCancel.setEnabled(false);
            buttonCancel.setVisibility(View.GONE);
        }

        String payAmountStr = "¥" + String.valueOf(mspApp.getPayAmount())+"円";
        String payCancelAmountStr = "¥" + String.valueOf(mspApp.getPayCancelAmount())+"円";

        viewPayCompany.setText(mspApp.getPayCompany());
        viewPayAmount.setText(payAmountStr);
        viewPayCancelAmount.setText(payCancelAmountStr);
        viewPayUserId.setText(mspApp.getPayUserId());
        viewPayOrderId.setText(mspApp.getPayOrderId());
        viewPayDeviceId.setText(mspApp.getPayDeviceId());
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
     * 戻るボタンの処理
     * @param view
     */
    public void endClick(View view) {

        //遷移先に戻る
        Intent intent = null;

        //遷移先により、データをセット Start
        if ("12".equals(mspApp.getPayResultKbn()) || "22".equals(mspApp.getPayResultKbn())) {
            //一覧画面からの場合
            intent = new Intent(getApplicationContext(), PayMuroolistActivity.class);
        } else {
            //処理画面からの場合
            intent = new Intent(getApplicationContext(), PayActivity.class);
        }
        //遷移先により、データをセット End

        mspApp.clearPayedInfo();
        startActivity(intent);
    }

    /**
     * 返金ボタンの処理
     * @param view
     */
    public void cancelClick(View view) {
        if (mspApp.getPayAmount() - mspApp.getPayCancelAmount() > 0) {
            mspApp.setListToCancel(true);
            mspApp.setListToCancelAmount(mspApp.getPayAmount() - mspApp.getPayCancelAmount());
            mspApp.setProcessKbn(2);   //1:決済　2:返金
            //遷移先に戻る
            Intent intent = null;
            //処理画面からの場合
            intent = new Intent(getApplicationContext(), PayActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(DetailActivity.this, R.string.msg0022, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * レシートを印刷します
     */
    private void doPrint() {

        //Sunmiプリンターテストの処理 test
        if (AidlUtil.getInstance().isConnect()) {
            Log.i(DetailActivity.class.getName(),"doPrint() connectPrinterService is OK!");

        //    AidlUtil.getInstance().print1Line();
            AidlUtil.getInstance().printText(mspApp.getShopName(), 48, 1,true, false);
            AidlUtil.getInstance().printText(mspApp.getShopInfo(), 28, 0,false, false);
            AidlUtil.getInstance().printText(getString(R.string.lab_shopTel) + mspApp.getShopTel(), 26, 0,false, false);
            AidlUtil.getInstance().printText(getString(R.string.lab_payDetail_dateTime) + mspApp.getPayProcessDateTime(), 24,  0,false, false);

            //結果処理区分（99：失敗）
            String rKbnStr = getString(R.string.lab_payDetail_kbn);
            if ("11".equals(mspApp.getPayResultKbn())) {
                AidlUtil.getInstance().printText(rKbnStr+getString(R.string.lab_payDetail_kbn_value1), 24,  0,false, false);
            } else if ("21".equals(mspApp.getPayResultKbn())) {
                AidlUtil.getInstance().printText(rKbnStr+getString(R.string.lab_payDetail_kbn_value2), 24,  0,false, false);
            } else if ("12".equals(mspApp.getPayResultKbn())) {
                AidlUtil.getInstance().printText(rKbnStr+getString(R.string.lab_payDetail_kbn_value3), 24,  0,false, false);
            } else if ("22".equals(mspApp.getPayResultKbn())) {
                AidlUtil.getInstance().printText(rKbnStr+getString(R.string.lab_payDetail_kbn_value4), 24,  0,false, false);
            }
            AidlUtil.getInstance().printText(getString(R.string.lab_payDetail_compang) + mspApp.getPayCompany(), 24,  0,false, false);

            String payAmountStr = "¥" + String.valueOf(mspApp.getPayAmount())+"円";
            String payCancelAmountStr = "¥" + String.valueOf(mspApp.getPayCancelAmount())+"円";
            if ("11".equals(mspApp.getPayResultKbn()) || "12".equals(mspApp.getPayResultKbn()) ) {
                AidlUtil.getInstance().printText(getString(R.string.lab_payDetail_amount) + payAmountStr, 28,  0,false, false);
            } else if ("21".equals(mspApp.getPayResultKbn()) || "22".equals(mspApp.getPayResultKbn())) {
                AidlUtil.getInstance().printText(getString(R.string.lab_payCancel_amount) + payCancelAmountStr, 28,  0,false, false);
            }

            AidlUtil.getInstance().printText("--------------------------------", 24, 1,false, false);
            AidlUtil.getInstance().printText(getString(R.string.lab_payDetail_userId) + mspApp.getUserId(), 24,  0,false, false);
            AidlUtil.getInstance().printText(getString(R.string.lab_payDetail_orderId) + mspApp.getPayOrderId(), 24,  0,false, false);
            AidlUtil.getInstance().printText(getString(R.string.lab_payDetail_deviceId) + mspApp.getDeviceId(), 24,  0,false, false);
            AidlUtil.getInstance().printText("--------------------------------", 24, 1,false, false);

            AidlUtil.getInstance().printQr(mspApp.getPayOrderId(), 6,3);//QRコード
            AidlUtil.getInstance().printText("--------------------------------", 24, 1,false, false);
            AidlUtil.getInstance().print2Line();
            AidlUtil.getInstance().print2Line();

            Log.i(DetailActivity.class.getName(),"Print  seeded!");
        } else {
            Log.i(DetailActivity.class.getName(),"doPrint() connectPrinterService is  NOT OK!");
        }
        //Sunmiプリンターテストの処理 End
    }
}
