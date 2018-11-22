package jp.co.muroo.systems.bspsample01;

/**
 * 決済Bean
 */
public class CommPayBean {

    /**
     * 決済センター区分
     */
    private String payCompanyKbn;

    /**
     * 金額
     */
    private int amount;

    /**
     * 顧客コード
     */
    private String userCode;

    /**
     * 店コード
     */
    private String shopCode;

    /**
     * 決済処理区分（決済・取消）
     */
    private String seedKbn;

    public String getSeedKbn() {
        return seedKbn;
    }

    public void setSeedKbn(String seedKbn) {
        this.seedKbn = seedKbn;
    }

    public String getPayCompanyKbn() {
        return payCompanyKbn;
    }

    public void setPayCompanyKbn(String payCompanyKbn) {
        this.payCompanyKbn = payCompanyKbn;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }
}
