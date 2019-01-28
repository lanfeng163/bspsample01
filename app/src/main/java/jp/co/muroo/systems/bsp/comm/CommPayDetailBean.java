package jp.co.muroo.systems.bsp.comm;

/**
 * 一覧画面の明細Bean
 */
public class CommPayDetailBean {

    //決済会社
    private String payCompany;
    //処理金額
    private int payAmount;
    //返金済金額
    private int payCancelAmount;
    //取引番号
    private String payOrderId;
    //処理時刻
    private String payDateTime;
    //処理ユーザー
    private String payUserId;
    //処理デバイス
    private String payDeviceId;

    public int getPayCancelAmount() {        return payCancelAmount;    }
    public void setPayCancelAmount(int payCancelAmount) {        this.payCancelAmount = payCancelAmount;    }
    public String getPayCompany() {
        return payCompany;
    }

    public void setPayCompany(String payCompany) {
        this.payCompany = payCompany;
    }

    public int getPayAmount() {        return payAmount;    }
    public void setPayAmount(int payAmount) {        this.payAmount = payAmount;    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public String getPayDateTime() {
        return payDateTime;
    }

    public void setPayDateTime(String payDateTime) {
        this.payDateTime = payDateTime;
    }

    public String getPayUserId() {
        return payUserId;
    }

    public void setPayUserId(String payUserId) {
        this.payUserId = payUserId;
    }

    public String getPayDeviceId() {
        return payDeviceId;
    }

    public void setPayDeviceId(String payDeviceId) {
        this.payDeviceId = payDeviceId;
    }
}
