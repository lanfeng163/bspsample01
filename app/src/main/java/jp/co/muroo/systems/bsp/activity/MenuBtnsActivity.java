package jp.co.muroo.systems.bsp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;
import jp.co.muroo.systems.bsp.comm.CommAlertDialogBuilder;

/**
 * メニューActivity②－複数ボタン
 */
public class MenuBtnsActivity extends AppCompatActivity {

    public MspApplication mspApp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_btns);

        //this.setTitle(R.string.head_title_name2);//タイトルバーの文字列

        Toolbar myToolbar = findViewById(R.id.muroo_tool_bar);
        myToolbar.setTitle(R.string.head_title_name2);//タイトルバーの文字列
        setSupportActionBar(myToolbar);

        mspApp = (MspApplication) this.getApplication();

        // Reference to UI elements
        TextView info = findViewById(R.id.txt_MainMenuInfo);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        // Capture the layout's TextView and set the string as its text
        info.setText(mspApp.getShopName() + "/" + mspApp.getUserId());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            Toast.makeText(this, "設定", Toast.LENGTH_SHORT).show();
            return true;
        }else
            */

        if(id == R.id.action_sys_end){
            //ログアウトボタンの処理です。
            AlertDialog.Builder builder = CommAlertDialogBuilder.SetDialog(3, R.string.msg0011, this);
            // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
            builder.setPositiveButton(R.string.msg_return1,
                    (dialog, which) -> {
                        mspApp.setLogOut();
                        //ログイン画面に移動
                        Intent intent = new Intent(MenuBtnsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    });

            builder.setNegativeButton(R.string.msg_return2,
                    (dialog, which) -> {
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * メニューボタンの処理 test
     * @param view
     */
    public void menuBtnTestClick(View view) {
        Intent intent = null;
        mspApp.setStartEndDatetime();
        mspApp.setProcessKbn(1);   //1:決済照会　2:返金照会
        intent = new Intent(MenuBtnsActivity.this, PayMuroolistActivity.class);
        startActivity(intent);
    }

    /**
     * メニューボタンの処理
     * @param view
     */
    public void menuBtnClick(View view) {
        Button b = (Button)view;
        String buttonText = b.getText().toString();

        String selectedId = "0";
        if (getString(R.string.menu_name1).equals(buttonText)) {
            selectedId = "1";
        } else if (getString(R.string.menu_name2).equals(buttonText)) {
            selectedId = "2";
        } else if (getString(R.string.menu_name3).equals(buttonText)) {
            selectedId = "3";
        } else if (getString(R.string.menu_name4).equals(buttonText)) {
            selectedId = "4";
        }

        int idInt =  Integer.parseInt(selectedId);
        Intent intent = null;

        switch (idInt) {
            case 1:
                //決済処理
                //Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                mspApp.setProcessKbn(1);   //1:決済　2:返金
                //    Intent intent = new Intent(this, PayActivity.class);
                intent = new Intent(MenuBtnsActivity.this, PayActivity.class);
                startActivity(intent);
                break;
            case 2:
                //返金処理
                mspApp.setProcessKbn(2);   //1:決済　2:返金
                intent = new Intent(MenuBtnsActivity.this, PayActivity.class);
                startActivity(intent);
                break;
            case 3:
                //Toast.makeText(getApplicationContext(), getString(R.string.group2menu1) + "　は開発中...", Toast.LENGTH_SHORT).show();
                mspApp.setStartEndDatetime();
                mspApp.setProcessKbn(1);   //1:決済照会　2:返金照会
                intent = new Intent(MenuBtnsActivity.this, PayMuroolistActivity.class);
                startActivity(intent);
                break;
            case 4:
                //Toast.makeText(getApplicationContext(), getString(R.string.group2menu2) + "　は開発中...", Toast.LENGTH_SHORT).show();
                mspApp.setStartEndDatetime();
                mspApp.setProcessKbn(2);   //1:決済照会　2:返金照会
                intent = new Intent(MenuBtnsActivity.this, PayMuroolistActivity.class);
                startActivity(intent);
                break;
        }
    }
}

