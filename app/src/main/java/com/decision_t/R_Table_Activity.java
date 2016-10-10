package com.decision_t;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class R_Table_Activity extends AppCompatActivity {

    private DrawerLayout drawer;
    private FloatingActionButton fab_left_start;
    private FloatingActionButton fab_right;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView host;
    private String[] user_info, table_data;
    private ListView r_table_list;
    private ArrayList<String[]> data;
    private MyAdapter myAdapter;
    private FloatingActionMenu r_table_fab_menu_left;
    private TextView r_table_status;

    //以下是測試ListView用，不用可刪除
    private ListView testListView;
    private String[] list = {"Aiden", "Luke", "Alice",  "Belgium", "France", "France", "Italy", "Germany", "Spain"};
    private ArrayAdapter<String> testListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_table_activity_main);
        //先取得傳進來的資料
        user_info = getIntent().getStringArrayExtra("user_info");
        table_data = getIntent().getStringArrayExtra("table_data");

//暫時用不到  layout用menu不符合設計圖規範
        navigationView = (NavigationView) findViewById(R.id.r_table_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_name) {
                    Toast.makeText(getApplicationContext(), "你想修改隨機桌名稱？", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_id) {
                    Toast.makeText(getApplicationContext(), "你想複製ID？", Toast.LENGTH_SHORT).show();
                }

                //按完之後關起來
                drawer = (DrawerLayout) findViewById(R.id.r_table_drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });

        toolbar = (Toolbar) findViewById(R.id.r_table_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(table_data[1]);

        drawer = (DrawerLayout) findViewById(R.id.r_table_drawer_layout);

        //初始化右邊的 FloatingActionButton
        fab_right = (FloatingActionButton) findViewById(R.id.r_table_fab_right);
        fab_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //檢查是否可以新增
                tableStatus();
                if(table_data[5].equals("Y")){
                    Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
                    return;
                }else if(table_data[6].equals("Y")){
                    Toast.makeText(getApplicationContext(), "目前為待決策狀態", Toast.LENGTH_SHORT).show();
                    return;
                }
                //點擊新增項目
                final View dialog_text = LayoutInflater.from(R_Table_Activity.this).inflate(R.layout.dialog_text, null);
                AlertDialog.Builder newitem = new AlertDialog.Builder(R_Table_Activity.this);
                newitem.setTitle("請輸入新項目");
                newitem.setView(dialog_text);
                newitem.setPositiveButton("新增", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView text = (TextView) dialog_text.findViewById(R.id.editText);
                        createItem(text.getText().toString(), table_data[0], user_info[0]);
                    }
                });
                newitem.show();

            }
        });

        //初始化左邊的FloatingActionMenu
        r_table_fab_menu_left = (FloatingActionMenu) findViewById(R.id.r_table_fab_menu_left);
        //初始化左邊的 FloatingActionButton
        fab_left_start = (FloatingActionButton) findViewById(R.id.r_table_fab_menu_item_start);
        fab_left_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableStatus();
                if(table_data[8].equals(user_info[0])){
                    if(table_data[5].equals("Y")){
                        Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
                    }else{
                        if(table_data[6].equals("N")){
                            AlertDialog.Builder lockcheck = new AlertDialog.Builder(R_Table_Activity.this);
                            lockcheck.setTitle("進入下一階段？");
                            lockcheck.setMessage("進入下一階段  <決策中>？\n注意：此步驟不可逆");
                            lockcheck.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String sql = "UPDATE `Decision_tables` " +
                                            "                   SET `Lock` = 'Y'" +
                                            "            WHERE `ID` ="+table_data[0]+";";
                                    DBConnector.executeQuery(sql);
                                }
                            });
                            lockcheck.setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            lockcheck.show();
                        }
                        //開始隨機
                        randomStart();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "您不是主持人!", Toast.LENGTH_SHORT).show();
                }
                r_table_fab_menu_left.close(true);
            }
        });

        //初始化listview
        r_table_list = (ListView) findViewById(R.id.r_table_list);
        r_table_list.setOnItemLongClickListener(long_click_item_list);
        //初始化桌狀態
        r_table_status = (TextView) findViewById(R.id.r_table_status);
        //刷新桌狀態
        tableStatus();
        //顯示項目列
        getItemList(table_data[0]);

        //以下是測試側欄的ListView的效果如何，不用可刪除
        // TODO This 20161010 00:55
        View v = findViewById(R.id.r_table_nav_right);
        testListView = (ListView) v.findViewById(R.id.listview_member);
        testListAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        testListView.setAdapter(testListAdapter);
        testListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "你選擇的是" + list[position], Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * 使用這個會有自動填入的效果，不用可刪除
         * 可以參考這網站:
         * http://stackoverflow.com/questions/15805397/android-searchview-with-auto-complete-feature-inside-action-bar
         */
        // TODO This 20161010 00:55
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocompletetv_searchmember);
        textView.setAdapter(testListAdapter);

    }

    //創建右上角的 info
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //info 被點到會有所反應
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                drawer.openDrawer(GravityCompat.END);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //取得決策桌項目列表
    public void getItemList(String table_id){
        //先清空資料
        data = new ArrayList<String[]>();
        try {
            //顯示決策桌的項目
            String sql = "SELECT *" +
                    "              FROM `Tables_item`" +
                    "          WHERE `Decision_tables_ID` = '"+ table_id +"'";
            String result = DBConnector.executeQuery(sql);
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                data.add(new String[] {
                        jsonData.getString("ID"),
                        jsonData.getString("Name"),
                        jsonData.getString("Info"),
                        jsonData.getString("Score"),
                        jsonData.getString("Decision_tables_ID"),
                        jsonData.getString("Account_ID")});
            }
            myAdapter = new MyAdapter(R_Table_Activity.this);
            r_table_list.setAdapter(myAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        private int p;
        public MyAdapter(Context c) {
            myInflater = LayoutInflater.from(c);
            p = -1;
        }
        public MyAdapter(Context c, int p) {
            myInflater = LayoutInflater.from(c);
            this.p = p;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //產生一個table_list_view的view，使用系統原生自帶的就行
            convertView = myInflater.inflate(android.R.layout.simple_list_item_1, null);
            //指定給他亮
            if(position == p || table_data[7].equals(data.get(position)[0])){
                convertView.setBackgroundColor(0xC0FFFF00);
            }
            //設定元件內容
            TextView itemtitle = (TextView) convertView.findViewById(android.R.id.text1);
            itemtitle.setText(data.get(position)[1]);
            return convertView;
        }
    }

    //項目列長按
    private AdapterView.OnItemLongClickListener long_click_item_list = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder check = new AlertDialog.Builder(R_Table_Activity.this);
            check.setTitle("確定刪除?");
            check.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String sql;
                        //先檢查是否為決策桌主持人
                        if(!table_data[8].equals(user_info[0])){
                            //再檢查是否為該項目創建者
                            sql = "SELECT * FROM `Tables_item` WHERE `ID`='"+data.get(position)[0]+"' AND `Account_ID`='"+user_info[0]+"';";
                            String result = DBConnector.executeQuery(sql);
                            JSONArray jsonArray = new JSONArray(result);
                            if(jsonArray.length() == 0) {
                                Toast.makeText(getApplicationContext(), "您不能刪除其他人新增的項目", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        sql = "DELETE FROM `Tables_item`" +
                                "WHERE `ID` = '"+data.get(position)[0]+"';";
                        DBConnector.executeQuery(sql);
                        getItemList(table_data[0]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
            check.show();
            return true;
        }
    };

    //桌狀態判斷
    public void tableStatus(){
        table_data = TableFunction.table_data(table_data[0]);//更新桌資訊
        if(table_data[5].equals("Y")){
            r_table_status.setText("已完結");
        }else{
            if(table_data[6].equals("Y")){
                r_table_status.setText("待決策");
            }else{
                r_table_status.setText("進行中");
            }
        }
    }
    //創建新項目
    public void createItem(String text, String table_id, String user_id){
        String sql = "INSERT INTO `Tables_item` ( `Name`, `Decision_tables_ID`, `Account_ID`)" +
                "               VALUES('"+text+"', "+table_id+", '"+user_id+"');";
        DBConnector.executeQuery(sql);
        //新增完更新畫面
        getItemList(table_data[0]);
    }
    //隨機排序並亮第一欄
    public void randomStart(){
        //先刷新一次畫面再隨機
        tableStatus();
        getItemList(table_data[0]);
        //隨機打亂ArrayList
        ArrayList<String[]> randomList = new ArrayList<String[]>( data.size( ) );
        do{
            int randomIndex = Math.abs( new Random( ).nextInt( data.size() ) );
            randomList.add( data.remove( randomIndex ) );
        }while( data.size( ) > 0 );
        data =  randomList;
        myAdapter = new MyAdapter(R_Table_Activity.this, 0);
        r_table_list.setAdapter(myAdapter);
        Toast.makeText(getApplicationContext(), "隨機結果：" + data.get(0)[1], Toast.LENGTH_SHORT).show();
    }
}
