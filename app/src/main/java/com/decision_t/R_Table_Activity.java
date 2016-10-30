package com.decision_t;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class R_Table_Activity extends AppCompatActivity {

    private Button nav_tablename_edit;
    private Button nav_description_edit;
    private Button nav_member_edit;
    private TextView nav_table_name, nav_table_id, nav_table_host, nav_table_description;
    private ListView nav_table_member;
    private DrawerLayout drawer;
    private FloatingActionMenu r_table_fab_menu_left;
    private FloatingActionButton fab_left_start;
    private FloatingActionButton fab_right;
    private Toolbar toolbar;
    private String[] user_info, table_data;
    private ListView r_table_list;
    private ArrayList<String[]> data;
    private MyAdapter myAdapter;
    private TextView r_table_status;
    private ArrayList<String[]> member_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_table_activity_main);
        //先取得傳進來的資料
        user_info = getIntent().getStringArrayExtra("user_info");
        table_data = getIntent().getStringArrayExtra("table_data");

        /**右側選單初始化按鈕*/
        nav_tablename_edit = (Button) findViewById(R.id.button_tablename_edit);
        nav_description_edit = (Button) findViewById(R.id.button_description_edit);
        nav_member_edit = (Button) findViewById(R.id.button_member_edit);
        nav_table_name = (TextView) findViewById(R.id.textView);
        nav_table_id = (TextView) findViewById(R.id.textView_id);
        nav_table_host = (TextView) findViewById(R.id.textView_host);
        nav_table_description = (TextView) findViewById(R.id.textView_description);
        nav_table_member = (ListView) findViewById(R.id.listView_member);
        //若非主持人則隱藏按鈕
        if(!table_data[8].equals(user_info[0])){
            nav_tablename_edit.setVisibility(View.INVISIBLE);
            nav_description_edit.setVisibility(View.INVISIBLE);
            nav_member_edit.setVisibility(View.INVISIBLE);
        }
        nav_table_name.setText(table_data[1]);
        nav_table_id.setText(table_data[0]);
        nav_table_host.setText(table_data[8]);
        nav_table_description.setText(table_data[3]);
        nav_tablename_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTableName(table_data[0]);
            }
        });
        nav_description_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTableInfo(table_data[0]);
            }
        });

        //member設定按鈕動作
        nav_member_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent memberIntent = new Intent(R_Table_Activity.this, MemberActivity.class);
                startActivity(memberIntent);
            }
        });

        //取得成員資訊
        showMemberList(table_data[0]);

        //toolbar設定
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
                //新增項目
                createItem(table_data[0], user_info[0]);
            }
        });

        //初始化左邊的FloatingActionMenu
        r_table_fab_menu_left = (FloatingActionMenu) findViewById(R.id.r_table_fab_menu_left);
        /** 點旁邊可收合FloatingButton */
        r_table_fab_menu_left.setClosedOnTouchOutside(true);

        //初始化左邊的 FloatingActionButton
        fab_left_start = (FloatingActionButton) findViewById(R.id.r_table_fab_menu_item_start);
        fab_left_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //確定是否是主持人
                if(table_data[8].equals(user_info[0])){
                    if(table_data[5].equals("Y")){
                        Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
                    }else{
                        if(table_data[6].equals("N")){
                            if(data.size() == 0){
                                Toast.makeText(getApplicationContext(), "至少需一個項目!", Toast.LENGTH_SHORT).show();
                            }else{
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
                                        //更新決策桌資訊，因為主持人只有一個所以不用連資料庫取得資料直接改就行
                                        table_data[6] = "Y";
                                        r_table_status.setText("投票中");

                                        //開始隨機
                                        randomStart();
                                    }
                                });
                                lockcheck.setNegativeButton("否", null);
                                lockcheck.show();
                            }
                        }else{
                            //開始隨機
                            randomStart();
                        }
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
        r_table_list.setOnItemClickListener(click_item_list);
        //初始化桌狀態
        r_table_status = (TextView) findViewById(R.id.r_table_status);
        //刷新桌狀態
        if(table_data[5].equals("Y")){
            r_table_status.setText("已完結");
        }else{
            if(table_data[6].equals("Y")){
                r_table_status.setText("待決策");
            }else{
                r_table_status.setText("進行中");
            }
        }
        //顯示項目列
        getItemList(table_data[0]);
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
            String sql = "SELECT `a`.*, `b`.`Name` as 'Account_Name'" +
                    "              FROM `Tables_item` `a`, `Account` `b`" +
                    "          WHERE `a`.`Account_ID` = `b`.`ID`" +
                    "                 AND `a`.`Decision_tables_ID` = '"+ table_id +"'" +
                    "           ORDER BY `ID` ASC";
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
                        jsonData.getString("Account_ID"),
                        jsonData.getString("Account_Name")});
            }
            myAdapter = new MyAdapter(R_Table_Activity.this);
            r_table_list.setAdapter(myAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        public MyAdapter(Context c) {
            myInflater = LayoutInflater.from(c);
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
            //產生一個table_list_view的view
            convertView = myInflater.inflate(R.layout.item_list_view, null);
            if(table_data[7].equals(data.get(position)[0])){
                convertView.setBackgroundColor(0xC0FFFF00);
            }
            //設定元件內容
            TextView itemtitle = (TextView) convertView.findViewById(R.id.item_name);
            itemtitle.setText(data.get(position)[1]);
            TextView itemaccount = (TextView) convertView.findViewById(R.id.item_origin);
            itemaccount.setText("建立者:" + data.get(position)[6]+"("+data.get(position)[5]+")");
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
                        //再檢查目前狀態
                        tableStatus();
                        if(table_data[5].equals("Y")){
                            Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(table_data[6].equals("Y")){
                            Toast.makeText(getApplicationContext(), "目前為待決策狀態", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            sql = "DELETE FROM `Tables_item`" +
                                    "WHERE `ID` = '"+data.get(position)[0]+"';";
                            DBConnector.executeQuery(sql);
                            getItemList(table_data[0]);
                        }

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
    public void createItem(final String table_id, final String user_id){
        //先初步檢查是否可以新增
        if(table_data[5].equals("Y")){
            Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
            return;
        }else if(table_data[6].equals("Y")){
            Toast.makeText(getApplicationContext(), "目前為待決策狀態", Toast.LENGTH_SHORT).show();
            return;
        }else{
            //取得資料庫資訊確定真的可以新增
            tableStatus();
            if(table_data[5].equals("Y")){
                Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
                return;
            }else if(table_data[6].equals("Y")){
                Toast.makeText(getApplicationContext(), "目前為待決策狀態", Toast.LENGTH_SHORT).show();
                return;
            }else{
                //點擊新增項目
                final View dialog_text = LayoutInflater.from(this).inflate(R.layout.dialog_text, null);
                AlertDialog.Builder newitem = new AlertDialog.Builder(this);
                newitem.setTitle("請輸入新項目");
                newitem.setView(dialog_text);
                newitem.setPositiveButton("新增", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextView text = (TextView) dialog_text.findViewById(R.id.editText);
                        String sql = "INSERT INTO `Tables_item` ( `Name`, `Decision_tables_ID`, `Account_ID`)" +
                                "               VALUES('"+text.getText()+"', "+table_id+", '"+user_id+"');";
                        DBConnector.executeQuery(sql);
                        //新增完更新畫面
                        getItemList(table_data[0]);
                    }
                });
                newitem.show();
            }
        }
    }
    //隨機排序並亮第一欄
    public void randomStart(){
        //先刷新一次畫面再隨機
        getItemList(table_data[0]);
        //隨機打亂ArrayList
        ArrayList<String[]> randomList = new ArrayList<String[]>( data.size( ) );
        do{
            int randomIndex = Math.abs( new Random( ).nextInt( data.size() ) );
            randomList.add( data.remove( randomIndex ) );
        }while( data.size( ) > 0 );
        data =  randomList;
        finalDecision(0);//最終決策判斷
        myAdapter = new MyAdapter(R_Table_Activity.this);
        r_table_list.setAdapter(myAdapter);
    }
    //決策桌表按下事件
    private AdapterView.OnItemClickListener click_item_list
            = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            //如果是主持人並處於未完成+待決策狀態就可以選擇最終決策
            if(table_data[8].equals(user_info[0]) && table_data[5].equals("N") && table_data[6].equals("Y")){
                finalDecision(position);
            }
        }
    };
    //最終決策
    public void finalDecision(final int position){
        AlertDialog.Builder ad = new AlertDialog.Builder(R_Table_Activity.this);
        ad.setTitle("最終決策");
        ad.setMessage("確定選擇：\n" + data.get(position)[1] + "\n作為最終選擇嗎？");
        ad.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "UPDATE `Decision_tables` SET `Final_decision` = "+ data.get(position)[0] +","+
                        "                                                                       `Complete`= 'Y'" +
                        "WHERE `ID` = "+ table_data[0]+";";
                DBConnector.executeQuery(sql);
                tableStatus();
                myAdapter = new MyAdapter(R_Table_Activity.this);
                r_table_list.setAdapter(myAdapter);
            }
        });
        ad.setNegativeButton("不要,再等等", null);
        ad.show();
    }

    //右側選單修改決策桌名稱
    public void updateTableName(final String table_id){
        final View dialog_text = LayoutInflater.from(this).inflate(R.layout.dialog_text, null);
        final TextView text = (TextView) dialog_text.findViewById(R.id.editText);
        text.setText(table_data[1]);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("修改決策桌名稱");
        dialog.setView(dialog_text);
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String sql = "UPDATE `Decision_tables` SET `Name` = '"+ text.getText()+"'"+
                        "           WHERE `ID` = "+ table_id +";";
                DBConnector.executeQuery(sql);
                table_data[1] = String.valueOf(text.getText());
                getSupportActionBar().setTitle(text.getText());
                nav_table_name.setText(text.getText());
            }
        });
        dialog.show();
    }

    //右側選單修改決策桌INFO
    public void updateTableInfo(final String table_id){
        final View dialog_text = LayoutInflater.from(this).inflate(R.layout.dialog_text_multi_line, null);
        final TextView text = (TextView) dialog_text.findViewById(R.id.editText);
        text.setText(table_data[3]);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("修改決策桌描述");
        dialog.setView(dialog_text);
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "UPDATE `Decision_tables` SET `Info` = '"+ text.getText()+"'"+
                        "           WHERE `ID` = "+ table_id +";";
                DBConnector.executeQuery(sql);
                table_data[3]=String.valueOf(text.getText());
                nav_table_description.setText(text.getText());
            }
        });
        dialog.show();
    }

    public void showMemberList(String table_id){
        member_data = TableFunction.getMember(table_id);
        MemberAdapter memberAdapter = new MemberAdapter(this);
        nav_table_member.setAdapter(memberAdapter);
    }

    public class MemberAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        public MemberAdapter(Context c) {
            myInflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            return member_data.size();
        }

        @Override
        public Object getItem(int position) {
            return member_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //產生一個table_list_view的view
            convertView = myInflater.inflate(R.layout.item_list_view, null);
            //設定元件內容
            TextView itemname = (TextView) convertView.findViewById(R.id.item_name);
            itemname.setTextSize(16);
            itemname.setText(member_data.get(position)[1]);
            TextView itemaccount = (TextView) convertView.findViewById(R.id.item_origin);
            itemaccount.setText("(" + member_data.get(position)[0] + ")");
            return convertView;
        }
    }
}
