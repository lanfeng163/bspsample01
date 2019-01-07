package jp.co.muroo.systems.bsp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;

/**
 * メニューActivity①－ListView
 */
public class MenuActivity extends Activity {

    public MspApplication mspApp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.setTitle(R.string.head_title_name2);//タイトルバーの文字列

        mspApp = (MspApplication) this.getApplication();

        // Reference to UI elements
        TextView info = findViewById(R.id.txtMenuInfo);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        // Capture the layout's TextView and set the string as its text
        info.setText(mspApp.getShopName() + "/" + mspApp.getUserId());

        this.setMenuList();

    }
    private void setMenuList() {

        List<Map<String, String>> list = new ArrayList<Map<String,String>>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", getString(R.string.head_title_name3));
        map.put("id", "1");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.head_title_name4));
        map.put("id", "2");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.head_title_name7));
        map.put("id", "3");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.head_title_name8));
        map.put("id", "4");
        list.add(map);


        String[] from = {"name"};
        int[] to = {android.R.id.text1};
        SimpleAdapter adapter = new SimpleAdapter(MenuActivity.this, list, android.R.layout.simple_expandable_list_item_1, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView)super.getView(position, convertView, parent);
                view.setTextSize(24);
                return view;
            }
        };

        ListView menuList = findViewById(R.id.menuList);
        menuList.setAdapter(adapter);
        menuList.setOnItemClickListener(new ListItemClickListener());
    }

    //リストが選択されたときの処理が記述されたメンバクラス。
    private class ListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);

            String selectedId = item.get("id");
            int idInt =  Integer.parseInt(selectedId);
            Intent intent = null;

            switch (idInt) {
                case 1:
                    //決済処理
                    //Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

                    mspApp.setProcessKbn(1);   //1:決済　2:返金
                    //    Intent intent = new Intent(this, PayActivity.class);
                    intent = new Intent(MenuActivity.this, PayActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    //返金処理
                    mspApp.setProcessKbn(2);   //1:決済　2:返金
                    intent = new Intent(MenuActivity.this, PayActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    //Toast.makeText(getApplicationContext(), getString(R.string.group2menu1) + "　は開発中...", Toast.LENGTH_SHORT).show();

                    mspApp.setProcessKbn(1);   //1:決済照会　2:返金照会
                    intent = new Intent(MenuActivity.this, PaylistActivity.class);
                    startActivity(intent);
                    break;
                case 4:
                    //Toast.makeText(getApplicationContext(), getString(R.string.group2menu2) + "　は開発中...", Toast.LENGTH_SHORT).show();
                    mspApp.setProcessKbn(2);   //1:決済照会　2:返金照会
                    intent = new Intent(MenuActivity.this, PaylistActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
