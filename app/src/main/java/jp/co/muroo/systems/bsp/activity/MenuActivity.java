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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.muroo.systems.bsp.MspApplication;
import jp.co.muroo.systems.bsp.R;

/**
 * メニューActivity
 */
public class MenuActivity extends Activity {

    private String userid = null;
    public MspApplication globals = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        globals = (MspApplication) this.getApplication();

        // Reference to UI elements
        TextView info = findViewById(R.id.txtMenuInfo);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
     //   userid = intent.getStringExtra(MainActivity.LOGIN_USERID);
        userid = ((MspApplication) this.getApplication()).getUserId();
        // Capture the layout's TextView and set the string as its text
        info.setText("ご利用ありがとうございます。");

        this.setMenuList();

    }
    private void setMenuList() {

        List<Map<String, String>> list = new ArrayList<Map<String,String>>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("name", getString(R.string.group1menu1));
        map.put("id", "1");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.group1menu2));
        map.put("id", "2");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.group2menu1));
        map.put("id", "3");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("name", getString(R.string.group2menu2));
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

    //フォントサイズを設定できるSimpleAdapter
//    private class MySimpleAdapter extends SimpleAdapter {
//        public MySimpleAdapter(Context context, List<Map<String, String>> items, int resource, String[] from, int[] to) {
//            super(context, items, resource, from, to);
//        }
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView view = (TextView)super.getView(position, convertView, parent);
//            view.setTextSize(24);
//            view.setHeight(50);
//            view.setMinimumHeight(50);
//            if (position % 2 == 0) {
//                view.setBackgroundColor(Color.BLACK);
//            } else {
//                view.setBackgroundColor(Color.DKGRAY);
//            }
//            return view;
//        }
//    }

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

                    globals.setProcessKbn(1);   //1:決済　2:返金
                    //    Intent intent = new Intent(this, ScanActivity.class);
                    intent = new Intent(MenuActivity.this, ScanActivity.class);
                    //intent.putExtra(MainActivity.LOGIN_USERID, userid);
                    startActivity(intent);
                    break;
                case 2:
                    //返金処理
                    //Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

                    globals.setProcessKbn(2);   //1:決済　2:返金
                    //    Intent intent = new Intent(this, ScanActivity.class);
                    //intent = new Intent(MenuActivity.this, CancelActivity.class);
                    intent = new Intent(MenuActivity.this, ScanActivity.class);
                    //intent.putExtra(MainActivity.LOGIN_USERID, userid);
                    startActivity(intent);
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), getString(R.string.group2menu1) + "　は開発中...", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), getString(R.string.group2menu2) + "　は開発中...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.group1Menu1:
                //Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

                //    Intent intent = new Intent(this, ScanActivity.class);
                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra(MainActivity.LOGIN_USERID, userid);
                startActivity(intent);
                break;
            case R.id.group1Menu2:
                Toast.makeText(getApplicationContext(), item.getTitle() + "　は開発中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.group2Menu1:
                Toast.makeText(getApplicationContext(), item.getTitle() + "　は開発中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.group2Menu2:
                Toast.makeText(getApplicationContext(), item.getTitle() + "　は開発中...", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
