package jp.co.muroo.systems.bsp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;

/**
 * メニューActivity②－複数ボタン
 */
public class MenuBtnsActivity extends Activity {

    public MspApplication mspApp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_btns);

        this.setTitle(R.string.head_title_name2);//タイトルバーの文字列

        mspApp = (MspApplication) this.getApplication();

        // Reference to UI elements
        TextView info = findViewById(R.id.txt_MainMenuInfo);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        // Capture the layout's TextView and set the string as its text
        info.setText(mspApp.getShopName() + "/" + mspApp.getUserId());

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

                mspApp.setProcessKbn(1);   //1:決済照会　2:返金照会
                intent = new Intent(MenuBtnsActivity.this, PaylistActivity.class);
                startActivity(intent);
                break;
            case 4:
                //Toast.makeText(getApplicationContext(), getString(R.string.group2menu2) + "　は開発中...", Toast.LENGTH_SHORT).show();
                mspApp.setProcessKbn(2);   //1:決済照会　2:返金照会
                intent = new Intent(MenuBtnsActivity.this, PaylistActivity.class);
                startActivity(intent);
                break;
        }
    }
}

