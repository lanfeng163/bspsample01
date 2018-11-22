package jp.co.muroo.systems.bspsample01;

import android.app.Application;

/**
 * 共有データクラス
 */
public class CommGlobals extends Application {

    private String userId;

    //1:決済　2:返金
    private int processKbn;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getProcessKbn() {
        return processKbn;
    }

    public void setProcessKbn(int processKbn) {
        this.processKbn = processKbn;
    }
}
