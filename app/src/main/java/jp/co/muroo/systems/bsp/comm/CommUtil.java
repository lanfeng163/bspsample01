package jp.co.muroo.systems.bsp.comm;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 共通の処理
 */
public class CommUtil {

    private static final String SERVICE＿PACKAGE = "woyou.aidlservice.jiuiv5";
    private static final String SERVICE＿ACTION = "woyou.aidlservice.jiuiv5.IWoyouService";

    private static CommUtil commUtil = new CommUtil();

    private CommUtil() {
    }

    public static CommUtil getInstance() {
        return commUtil;
    }

    /**
     * 決済センター名前を取得
     * @return
     */
    public String getPayCompanyNm(String kbn) {
        String payCompanyNm = "";
        if (kbn.equals("01")) {
            payCompanyNm = "ALi Pay";
        } else if (kbn.equals("02")) {
            payCompanyNm = "WeChat Pay";
        }
        return payCompanyNm;
    }

    /**
     * デジタル署名
     * @return
     */
    public String getDigitalSignatureStr() {
        //TODO:デジタル署名の処理が未完成!!!
        String keyStr = "0123456789j0987654321";
        return keyStr;
    }

    /**
     * サーバーのデジタル署名
     * @param serverKey
     * @return
     */
    public boolean checkDigitalSignature(String serverKey) {
        //TODO:デジタル署名のチェック処理が未完成!!!
        return true;
    }

    /**
     * WebAPIにPost送信
     * @param urlStr
     * @param jsonDate
     * @param activityName
     * @return
     */
    public String doPost(String urlStr, JSONObject jsonDate, String activityName) {

        HttpURLConnection connection = null;
        InputStream is = null;
        String result = null;

        try {
            // 接続先のURLの設定およびコネクションの取得
            URL url = new URL(urlStr);
            Log.i(activityName,"Request URL is " + urlStr);
            connection = (HttpURLConnection) url.openConnection();

            //JSONを送信　POST start
            // データを送信するためにはbyte配列に変換する必要がある
            byte[] sendJson = jsonDate.toString().getBytes("UTF-8");

            // 接続するための設定
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            // APIからの戻り値と送信するデータの設定を許可する
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // 送信するデータの設定
            connection.getOutputStream().write(sendJson);
            connection.getOutputStream().flush();
            connection.getOutputStream().close();
            //JSONを送信　POST end

            // 接続！ 1000(1秒)
            connection.setConnectTimeout(6000000);
            connection.setReadTimeout(6000000);
            connection.connect();

            if (connection.getResponseCode() != 200) {
                result = "NOT200ERROR";
                Log.e(activityName,"Response result is " + result);
            } else {
                is = connection.getInputStream();
                //InputStreamオブジェクトを文字列に変換
                result = getStringByIS(is);
                Log.i(activityName,"Response result is " + result);
            }
        } catch (Exception ex) {
            Log.e(activityName, ex.toString());
            result = "JAVAERROR";
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
            if(is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    Log.d(activityName, ex.toString());
                    result = "JAVAERROR";
                }
            }
        }
        return result;
    }

    /**
     * InputStreamオブジェクトを文字列に変換するメソッド。変換文字コードはUTF-8。
     * @param is
     * @return
     * @throws IOException
     */
    private String getStringByIS(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        char[] b = new char[1024];
        int line;
        while(0 <= (line = reader.read(b))) {
            sb.append(b, 0, line);
        }
        String sbStr = sb.toString();

        sbStr = sbStr.replaceAll("\\\\r\\\\n", "");
        sbStr = sbStr.replace("\"{", "{");
        sbStr = sbStr.replace("}\",", "},");
        sbStr = sbStr.replace("}\"", "}");
        sbStr = sbStr.replace("\\", "");
        return sbStr;
    }
}
