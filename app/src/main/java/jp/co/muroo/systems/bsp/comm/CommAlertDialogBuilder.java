package jp.co.muroo.systems.bsp.comm;

import android.app.AlertDialog;
import android.content.Context;

import jp.co.muroo.systems.bsp.R;

/**
 * メッセージ処理クラス
 */
public class CommAlertDialogBuilder {

    public static AlertDialog.Builder SetDialog(int titleKbn, int messageId, Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // アラートダイアログのタイトルを設定します
        if (titleKbn == 1) {
            //エラー
            alertDialogBuilder.setTitle(R.string.msg_title1);
        } else if (titleKbn == 2) {
            //メッセージ
            alertDialogBuilder.setTitle(R.string.msg_title2);
        } else if (titleKbn == 3) {
            //確認
            alertDialogBuilder.setTitle(R.string.msg_title3);
        }
        // アラートダイアログのメッセージを設定します
        alertDialogBuilder.setMessage(messageId);

        // アラートダイアログのキャンセルが可能かどうかを設定します
        alertDialogBuilder.setCancelable(true);

        return alertDialogBuilder;
    }
//
//
//    private AlertDialog.Builder SetDialog(Context context, int titleKbn, String messageStr) {
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//
//        // アラートダイアログのタイトルを設定します
//        if (titleKbn == 1) {
//            //エラー
//            alertDialogBuilder.setTitle(R.string.msg_title1);
//
//        } else if (titleKbn == 2) {
//            //メッセージ
//            alertDialogBuilder.setTitle(R.string.msg_title2);
//
//        } else if (titleKbn == 3) {
//            //確認
//            alertDialogBuilder.setTitle(R.string.msg_title3);
//
//        }
//        // アラートダイアログのメッセージを設定します
//        alertDialogBuilder.setMessage("メッセージ");
//
//
//        // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
//        alertDialogBuilder.setPositiveButton("肯定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//        // アラートダイアログの中立ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
//        alertDialogBuilder.setNeutralButton("中立",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//        // アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
//        alertDialogBuilder.setNegativeButton("否定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//        // アラートダイアログのキャンセルが可能かどうかを設定します
//        alertDialogBuilder.setCancelable(true);
//
//        return alertDialogBuilder;
//
//
//    }

}
