package jp.co.muroo.systems.bsp;

import android.app.Application;
import android.util.Log;

/**
 * 共有データクラス
 */
public class MspApplication extends Application {

    private static final String tag = "MspApplication";

    //基本情報
    private String userId;
    private String digitalSignature;
    private String token;
    private String deviceId;
    private String shopName;
    private String shopInfo;

    //処理区分（11：決済結果 12：決済詳細　21：返金結果 22：返金詳細  99：エラー）
    private String resultKbn;
    //サーバーのデジタル署名
    private String serverDigitalSignature;

    //スキャン画面の処理区分（1:決済　2:返金）
    private int processKbn;

    //処理結果詳細データをセット　Start
    //処理金額
    private int payAmount;
    //決済会社
    private String payCompany;
    //取引番号
    private String payOrderId;
    //処理時刻
    private String payProcessDateTime;

    //処理結果詳細データをセット　End

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
    public String getPayOrderId() {
        return payOrderId;
    }
    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }
    public String getPayProcessDateTime() {
        return payProcessDateTime;
    }
    public void setPayProcessDateTime(String payProcessDateTime) {
        this.payProcessDateTime = payProcessDateTime;
    }
    public String getServerDigitalSignature() { return serverDigitalSignature; }
    public void setServerDigitalSignature(String serverDigitalSignature) {
        this.serverDigitalSignature = serverDigitalSignature;
    }
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
    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        configUncaughtExceptionHandler();
    }

    /**
     * 異常をキャッチします。
     */
    private void configUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e(tag, "======-----------configUncaughtExceptionHandler---------------=========" + ex.toString());
            }
        });
    }
}
