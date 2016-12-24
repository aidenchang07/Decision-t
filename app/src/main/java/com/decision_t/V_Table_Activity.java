package com.decision_t;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class V_Table_Activity extends AppCompatActivity {

    private ImageButton nav_tablename_edit;
    private ImageButton nav_description_edit;
    private ImageButton nav_member_edit;
    private TextView nav_table_name, nav_table_id, nav_table_host, nav_table_description;
    private ListView nav_table_member;
    private DrawerLayout drawer;
    private FloatingActionMenu fab_left;
    private FloatingActionButton fab_left_start;
    private FloatingActionButton fab_left_end;
    private FloatingActionButton fab_right;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String[] user_info, table_data;
    private ListView v_table_list;
    private ArrayList<String[]> data;
    private MyAdapter myAdapter;
    private TextView v_table_status;
    private ArrayList<String[]> member_data;
    private boolean can_vote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_table_activity_main);

        //先取得傳進來的資料
        user_info = getIntent().getStringArrayExtra("user_info");
        table_data = getIntent().getStringArrayExtra("table_data");

        /**右側選單初始化按鈕*/
        nav_tablename_edit = (ImageButton) findViewById(R.id.imageButton_tablename_edit);
        nav_description_edit = (ImageButton) findViewById(R.id.imageButton_description_edit);
        nav_member_edit = (ImageButton) findViewById(R.id.imageButton_member_edit);
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
                Intent memberIntent = new Intent(V_Table_Activity.this, MemberActivity.class);
                memberIntent.putExtra("table_data", table_data);
                startActivityForResult(memberIntent, 1);//返回後會執行onActivityResult
            }
        });

        //取得成員資訊
        showMemberList(table_data[0]);

        //toolbar設定
        toolbar = (Toolbar) findViewById(R.id.v_table_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(table_data[1]);

        drawer = (DrawerLayout) findViewById(R.id.v_table_drawer_layout);

        /** 初始化 FloatingActionButton */
        fab_left = (FloatingActionMenu) findViewById(R.id.v_table_fab_menu_left);
        fab_left_start = (FloatingActionButton) findViewById(R.id.v_table_fab_menu_item_start);
        fab_left_end = (FloatingActionButton) findViewById(R.id.v_table_fab_menu_item_end);
        fab_right = (FloatingActionButton) findViewById(R.id.v_table_fab_right);
        fab_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新增項目
                createItem(table_data[0], user_info[0]);
            }
        });

        /** 點旁邊可收合FloatingButton */
        fab_left.setClosedOnTouchOutside(true);
        //如果不是主持人則隱藏左側下方功能鍵
        if(!table_data[8].equals(user_info[0])) {
            fab_left.setVisibility(View.INVISIBLE);
        }

        /*設定左側下方選單按鈕可用與關閉*/
        if(table_data[5].equals("Y")){//已完結
            fab_left_start.setEnabled(false);
            fab_left_end.setEnabled(false);
        }else{
            if(table_data[6].equals("Y")){//已鎖定投票中
                if(!table_data[7].equals("") && !table_data[7].equals("null")){
                    fab_left_end.setEnabled(false);
                }
                fab_left_start.setEnabled(false);
            }else{//只是一開始
                fab_left_end.setEnabled(false);
            }
        }


        /** 配置開始投票的監聽器 */
        fab_left_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteStart();
            }
        });

        /** 配置結束投票的監聽器 */
        fab_left_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteEnd();
            }
        });

        //初始化listview
        v_table_list = (ListView) findViewById(R.id.v_table_list);
        v_table_list.setOnItemLongClickListener(long_click_item_list);
        v_table_list.setOnItemClickListener(click_item_list);
        //初始化桌狀態
        v_table_status = (TextView) findViewById(R.id.v_table_status);
        //刷新桌狀態
        if(table_data[5].equals("Y")){
            v_table_status.setText("已完結");
        }else{
            if(table_data[6].equals("Y")){
                if(table_data[7].equals("") || table_data[7].equals("null")){
                    v_table_status.setText("投票中");
                }else{
                    v_table_status.setText("待決策");
                }
            }else{
                v_table_status.setText("進行中");
            }
        }
        //顯示項目列
        getItemList(table_data[0]);
        //取得目前是否可以投票的權限，投過就不能投了
        can_vote = V_Table_Function.canVote(table_data[0], user_info[0]);
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
            /*
                            顯示決策桌的項目
                             依照目前狀態選用不同的sql
                             投票中看V_item_score的分數
                             完結了直接看Tables_item的分數
                        */
            String sql;
            if(table_data[5].equals("Y")){
                sql = "SELECT `a`.*, `b`.`Name` as `Account_Name`" +
                        "FROM `Tables_item` `a`, `Account` `b`" +
                        "WHERE `a`.`Account_ID` = `b`.`ID`" +
                        "   AND `a`.`Decision_tables_ID` = '"+ table_id +"'" +
                        "ORDER BY `ID` ASC";
            }else{
                sql = "SELECT `a`.`ID` , " +
                        "             `a`.`Name` ," +
                        "             `a`.`Info` ," +
                        "              SUM( `b`.`Score` ) `Score` ," +
                        "             `a`.`Decision_tables_ID` , " +
                        "             `a`.`Account_ID`," +
                        "             `c`.`Name` `Account_Name`" +
                        "FROM `Tables_item` `a`LEFT JOIN `V_item_score` `b` ON `a`.`ID` = `b`.`Item_ID` , `Account` `c` " +
                        "WHERE `a`.`Account_ID` = `c`.`ID`" +
                        "      AND `a`.`Decision_tables_ID` ='"+ table_id +"'" +
                        "GROUP BY `a`.`ID` ";
            }

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
            myAdapter = new MyAdapter(V_Table_Activity.this);
            v_table_list.setAdapter(myAdapter);
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
            convertView = myInflater.inflate(R.layout.x_table_item_list_view, null);
            if(table_data[7].equals(data.get(position)[0])){
                convertView.setBackgroundResource(R.drawable.item_yellow_form);
            }
            //設定元件內容
            TextView itemtitle = (TextView) convertView.findViewById(R.id.x_item_name);
            itemtitle.setText(data.get(position)[1]);
            TextView itemaccount = (TextView) convertView.findViewById(R.id.x_item_account);
            itemaccount.setText("建立者:" + data.get(position)[6]+"("+data.get(position)[5]+")");
            TextView itemscore = (TextView) convertView.findViewById(R.id.x_item_score);
            if(data.get(position)[3].equals("null")){
                data.get(position)[3] = "0";
            }
            itemscore.setText(data.get(position)[3]);
            //如果為進行中(決策桌剛建立)將score隱藏
            if(table_data[5].equals("N") && table_data[6].equals("N")){
                itemscore.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    //桌狀態判斷
    public void tableStatus(){
        table_data = TableFunction.table_data(table_data[0]);//更新桌資訊
        if(table_data[5].equals("Y")){
            v_table_status.setText("已完結");
        }else{
            if(table_data[6].equals("Y")){
                if(table_data[7].equals("") || table_data[7].equals("null")){
                    v_table_status.setText("投票中");
                }else{
                    v_table_status.setText("待決策");
                }
            }else{
                v_table_status.setText("進行中");
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
            Toast.makeText(getApplicationContext(), "目前為投票中狀態", Toast.LENGTH_SHORT).show();
            return;
        }else{
            //取得資料庫資訊確定真的可以新增
            tableStatus();
            if(table_data[5].equals("Y")){
                Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
                return;
            }else if(table_data[6].equals("Y")){
                Toast.makeText(getApplicationContext(), "目前為投票中狀態", Toast.LENGTH_SHORT).show();
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

    //決策桌表按下事件
    private AdapterView.OnItemClickListener click_item_list
            = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            //如果是投票中狀態且尚未投過票，則可以投票
            if(table_data[5].equals("N")){
                if(table_data[6].equals("Y")){
                    tableStatus();//更新資訊
                    if(table_data[7].equals("") || table_data[7].equals("null")){//確定還沒被主持人關閉即可投票
                        if(can_vote){//沒投過可以投
                            vote(position);
                        }else{
                            Toast.makeText(getApplicationContext(), "已經投過票囉！", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        /*若是主持人則可以進行最終決策
                                                *若不是則等待
                                                */
                        if(table_data[8].equals(user_info[0])){
                            finalDecision(position);
                        }else{
                            Toast.makeText(getApplicationContext(), "投票環節已過！\n請等待主持人做出最終決策！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }//未鎖定之前點擊沒反應
            }else{
                Toast.makeText(getApplicationContext(), "決策桌已完結！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //投票
    public void vote(final int position){
        AlertDialog.Builder ad = new AlertDialog.Builder(V_Table_Activity.this);
        ad.setTitle("投票");
        ad.setMessage("確定將票投給：\n" + data.get(position)[1] + "\n嗎？");
        ad.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "INSERT INTO `V_item_score`" +
                        "                   VALUES ('" + data.get(position)[0] + "'," +
                        "                                      '" + user_info[0] + "', " +
                        "                                     '1');";
                DBConnector.executeQuery(sql);
                data.get(position)[3] = String.valueOf(Integer.parseInt(data.get(position)[3]) + 1);
                myAdapter = new MyAdapter(V_Table_Activity.this);
                v_table_list.setAdapter(myAdapter);
                can_vote = false;
            }
        });
        ad.setNegativeButton("不要,再等等", null);
        ad.show();
    }


    //項目列長按
    private AdapterView.OnItemLongClickListener long_click_item_list = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder check = new AlertDialog.Builder(V_Table_Activity.this);
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
                            Toast.makeText(getApplicationContext(), "目前為投票中狀態", Toast.LENGTH_SHORT).show();
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

    //投票開始
    public void voteStart(){
        //確定是否是主持人
        if(table_data[8].equals(user_info[0])){
            if(table_data[5].equals("Y")){
                Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
            }else{
                if(table_data[6].equals("N")){
                    if(data.size() == 0){
                        Toast.makeText(getApplicationContext(), "至少需一個項目!", Toast.LENGTH_SHORT).show();
                    }else{
                        AlertDialog.Builder lockcheck = new AlertDialog.Builder(V_Table_Activity.this);
                        lockcheck.setTitle("進入下一階段？");
                        lockcheck.setMessage("進入下一階段  <投票中>？\n注意：此步驟不可逆");
                        lockcheck.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String sql = "UPDATE `Decision_tables` " +
                                        "                   SET `Lock` = 'Y'" +
                                        "            WHERE `ID` ="+table_data[0]+";";
                                DBConnector.executeQuery(sql);
                                //更新決策桌資訊，因為主持人只有一個所以不用連資料庫取得資料直接改就行
                                table_data[6] = "Y";
                                v_table_status.setText("投票中");
                                //打開按鈕
                                fab_left_start.setEnabled(false);
                                fab_left_end.setEnabled(true);
                                //更新列表佈局
                                getItemList(table_data[0]);
                                Toast.makeText(getApplication(), "開始投票！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        lockcheck.setNegativeButton("否", null);
                        lockcheck.show();
                    }
                }
            }
        }else{
            Toast.makeText(getApplicationContext(), "您不是主持人!", Toast.LENGTH_SHORT).show();
        }
        fab_left.close(true);
    }

    public void voteEnd(){
        //確定是否是主持人
        if(table_data[8].equals(user_info[0])){
            if(table_data[5].equals("Y")){
                Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
            }else{
                if(table_data[6].equals("Y")){//已鎖定
                    if(data.size() == 0){
                        Toast.makeText(getApplicationContext(), "至少需一個項目!", Toast.LENGTH_SHORT).show();
                    }else{
                        AlertDialog.Builder lockcheck = new AlertDialog.Builder(V_Table_Activity.this);
                        lockcheck.setTitle("進入下一階段？");
                        lockcheck.setMessage("確定要結束投票結算票數？\n注意：此步驟不可逆");
                        lockcheck.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //先更新決策桌暫時的建議方案
                                String sql  = "UPDATE `Decision_tables`" +
                                        "                     SET `Final_decision` =(SELECT `ID`" +
                                        "                                                                     FROM (SELECT `a`.`ID`," +
                                        "                                                                                                     IFNULL(SUM( `b`.`Score` ), 0) `Score` " +
                                        "                                                                                        FROM `Tables_item` `a`LEFT JOIN `V_item_score` `b` ON `a`.`ID` = `b`.`Item_ID` , `Account` `c` " +
                                        "                                                                                    WHERE `a`.`Account_ID` = `c`.`ID`" +
                                        "                                                                                           AND `a`.`Decision_tables_ID` ='" + table_data[0] + "'" +
                                        "                                                                                      GROUP BY `a`.`ID`" +
                                        "                                                                                      ORDER BY `Score` DESC" +
                                        "                                                                                        LIMIT 1) `Score`" +
                                        "                                                                             )" +
                                        "                          WHERE `ID` = '" + table_data[0] + "';";
                                DBConnector.executeQuery(sql);
                                //接著更新桌的項目統計分數
                                sql = "UPDATE `Tables_item` `ti` INNER JOIN " +
                                      "                 (SELECT `a`.`ID`," +
                                      "                                    IFNULL(SUM( `b`.`Score` ), 0) `Score` " +
                                      "                       FROM `Tables_item` `a`LEFT JOIN `V_item_score` `b` ON `a`.`ID` = `b`.`Item_ID` , `Account` `c` " +
                                      "                    WHERE `a`.`Account_ID` = `c`.`ID`" +
                                      "                          AND `a`.`Decision_tables_ID` ='" + table_data[0] + "'" +
                                      "                     GROUP BY `a`.`ID`" +
                                      "                     ORDER BY `Score` DESC" +
                                      "                 )`vis`" +
                                      "                 ON `ti`.`ID` = `vis`.`ID`" +
                                      "        SET `ti`.`Score` = `vis`.`Score`" +
                                      "WHERE `ti`.`Decision_tables_ID`= '" + table_data[0] + "' ";
                                DBConnector.executeQuery(sql);
                                //更新決策桌資訊
                                tableStatus();
                                //關閉按鈕
                                fab_left_end.setEnabled(false);
                                //更新列表佈局
                                getItemList(table_data[0]);
                                Toast.makeText(getApplication(), "投票結束！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        lockcheck.setNegativeButton("否", null);
                        lockcheck.show();
                    }
                }
            }
        }else{
            Toast.makeText(getApplicationContext(), "您不是主持人!", Toast.LENGTH_SHORT).show();
        }
        fab_left.close(true);
    }

    //最終決策
    public void finalDecision(final int position){
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
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
                myAdapter = new MyAdapter(V_Table_Activity.this);
                v_table_list.setAdapter(myAdapter);
            }
        });
        ad.setNegativeButton("不要,再等等", null);
        ad.show();
    }

    @Override // 覆寫 onActivityResult，member添加完成員後傳值回來時會執行此方法。
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //不管如何先更新member列表再說
        showMemberList(table_data[0]);
    }
}
