package jp.co.muroo.systems.bsp.comm;

/**
 * 一覧画面の明細Bean
 */
public class CommPayDetailBean {

    //決済会社
    private String payCompany;
    //処理金額
    private String payAmount;
    //取引番号
    private String payOrderId;
    //処理時刻
    private String payDateTime;
    //処理ユーザー
    private String payUserId;
    //処理デバイス
    private String payDeviceId;

    public String getPayCompany() {
        return payCompany;
    }

    public void setPayCompany(String payCompany) {
        this.payCompany = payCompany;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

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
