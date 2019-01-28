package jp.co.muroo.systems.bsp;

import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.printerhelper.utils.AidlUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import jp.co.muroo.systems.bsp.activity.PayActivity;
import jp.co.muroo.systems.bsp.comm.CommPayDetailBean;

/**
 * The Applicationクラス
 */
public class MspApplication extends Application {

    //基本情報
    private String userId;
    private String digitalSignature;
    private String token;
    private String deviceId;
    private String shopName;
    private String shopInfo;
    private String shopTel;

    //処理結果
    private String resultKbn;
    //サーバーのデジタル署名
    private String serverDigitalSignature;

    //スキャン画面の処理区分（1:決済　2:返金）
    private int processKbn;

    //処理結果詳細データをセット　Start

    //処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細 ）
    private String payResultKbn;
    //処理金額
    private int payAmount;
    //返済金額
    private int payCancelAmount;

    //決済会社
    private String payCompany;
    //取引番号
    private String payOrderId;
    //処理時刻
    private String payProcessDateTime;
    //処理ユーザー
    private String payUserId;
    //処理デバイス
    private String payDeviceId;
    //処理結果詳細データをセット　End

    private String payDateStart;
    private String payDateEnd;

    //一覧画面の明細データ
    ArrayList<CommPayDetailBean> payDataList = new ArrayList<>();

    private boolean listToCancel;
    private int listToCancelAmount;

    public boolean isListToCancel() {        return listToCancel;    }
    public void setListToCancel(boolean listToCancel) {        this.listToCancel = listToCancel;    }
    public int getListToCancelAmount() {        return listToCancelAmount;    }
    public void setListToCancelAmount(int listToCancelAmount) {        this.listToCancelAmount = listToCancelAmount;    }

    public int getPayCancelAmount() {        return payCancelAmount;    }
    public void setPayCancelAmount(int payCancelAmount) {        this.payCancelAmount = payCancelAmount;    }

    public String getPayResultKbn() {        return payResultKbn;    }
    public void setPayResultKbn(String payResultKbn) {        this.payResultKbn = payResultKbn;   }
    public ArrayList<CommPayDetailBean> getPayDataList() {        return payDataList;    }
    public void setPayDataList(ArrayList<CommPayDetailBean> payDataList) {        this.payDataList = payDataList;    }
    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getShopInfo() {
        return shopInfo;
    }
    public void setShopInfo(String shopInfo) {
        this.shopInfo = shopInfo;
    }
    public String getShopTel() {        return shopTel;    }
    public void setShopTel(String shopTel) {        this.shopTel = shopTel;    }

    public String getResultKbn() {
        return resultKbn;
    }
    public void setResultKbn(String resultKbn) { this.resultKbn = resultKbn; }
    public int getPayAmount() {
        return payAmount;
    }
    public void setPayAmount(int payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayCompany() {
        return payCompany;
    }
    public void setPayCompany(String payCompany) {
        this.payCompany = payCompany;
    }
    public String getPayOrderId() {        return payOrderId;    }
    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }
    public String getPayProcessDateTime() {
        return payProcessDateTime;
    }
    public void setPayProcessDateTime(String payProcessDateTime) {        this.payProcessDateTime = payProcessDateTime;    }
    public String getPayUserId() {        return payUserId;    }
    public void setPayUserId(String payUserId) {        this.payUserId = payUserId;    }
    public String getPayDeviceId() {        return payDeviceId;    }
    public void setPayDeviceId(String payDeviceId) {        this.payDeviceId = payDeviceId;    }
    public String getServerDigitalSignature() { return serverDigitalSignature; }
    public void setServerDigitalSignature(String serverDigitalSignature) {        this.serverDigitalSignature = serverDigitalSignature;    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) { this.token = token; }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public int getProcessKbn() {        return processKbn;    }
    public void setProcessKbn(int processKbn) {
        this.processKbn = processKbn;
    }
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getDigitalSignature() { return digitalSignature; }
    public void setDigitalSignature(String digitalSignature) {        this.digitalSignature = digitalSignature;    }

    public String getPayDateStart() {        return payDateStart;    }
    public void setPayDateStart(String payDateStart) {        this.payDateStart = payDateStart;    }
    public String getPayDateEnd() {        return payDateEnd;    }
    public void setPayDateEnd(String payDateEnd) {        this.payDateEnd = payDateEnd;    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Sunmi Scan
        configUncaughtExceptionHandler();
        //Sunmi Scan

        //Sunmi Print
        isAidl = true;
        AidlUtil.getInstance().connectPrinterService(this);
        //Sunmi Print
    }

    //Sunmi Print
    private boolean isAidl;
    public boolean isAidl() {
        return isAidl;
    }
    public void setAidl(boolean aidl) {  isAidl = aidl;    }
    //Sunmi Print

    /**
     * Sunmi Scan
     * 異常をキャッチします。
     */
    private void configUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e("MspApplication", "==------configUncaughtExceptionHandler----Sunmi Scan---==" + ex.toString());
                Toast.makeText(MspApplication.this, ex.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ログアウトの処理
     */
    public void setLogOut() {

        this.setUserId("");
        this.setDigitalSignature("");

        this.setShopName("");
        this.setShopInfo("");
        this.setShopTel("");
        this.setToken("");
    }

    /**
     * 決済詳細データをクリア
     */
    public void clearPayedInfo() {
        this.setPayResultKbn("");
        this.setPayCompany("");
        this.setPayAmount(0);
        this.setPayCancelAmount(0);
        this.setPayOrderId("");
        this.setPayProcessDateTime("");
        this.setPayUserId("");
        this.setPayDeviceId("");

        this.setListToCancel(false);
        this.setListToCancelAmount(0);
    }

    /**
     * 初期化の日付と時間をセット
     */
    public void setStartEndDatetime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00");
        payDateStart = sdf.format(date);

        sdf = new SimpleDateFormat("yyyy-MM-dd 23:59");
        payDateEnd = sdf.format(date);
    }
}
