package jp.co.muroo.systems.bsp.comm;

/**
 * 決済Bean
 */
public class CommPayBean {
    /**
     * 端末ID
     */
    private String terminalId;
    /**
     * 利用者ID
     */
    private String userId;
    /**
     * システムID(決済サービス区分)
     */
    private String systemId;
    /**
     * 処理種別(支払い・返金区分)
     */
    private String procMode;
    /**
     * 接続トークン
     */
    private String token;
    /**
     * 処理金額
     */
    private int amount;
    /**
     * 処理コード(支払QRコード・取引オーダーNo)
     */
    private String qrCode;
    /**
     * デジタル署名
     */
    private String userKey;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getProcMode() {
        return procMode;
    }

    public void setProcMode(String procMode) {
        this.procMode = procMode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getAmount() { return amount; }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getQrCode() {        return qrCode;    }
    public void setQrCode(String qrCode) {        this.qrCode = qrCode;    }

    public String getUserKey() {        return userKey;    }
    public void setUserKey(String userKey) {        this.userKey = userKey;    }
}
