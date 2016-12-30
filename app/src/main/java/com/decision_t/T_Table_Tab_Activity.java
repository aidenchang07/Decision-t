package com.decision_t;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.decision_t.t_table_tab.NotSupportFragment;
import com.decision_t.t_table_tab.SupportFragment;
import com.decision_t.t_table_tab.ViewPagerAdapter;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class T_Table_Tab_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private DrawerLayout drawer;
    private FloatingActionButton fab_right;
    private String[] user_info, table_data, item_data;
    private ArrayList<String[]> support_data, notSupport_data;
    private SupportFragment supportFragment;
    private NotSupportFragment notSupportFragment;
    private TextView nav_item_name, nav_item_creator, nav_item_description;
    private ImageButton nav_description_edit, nav_item_name_edit;
    private UpdateScreenThead updateScreenThead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_table_tab);

        //取得相關資料
        user_info = getIntent().getStringArrayExtra("user_info");
        table_data = getIntent().getStringArrayExtra("table_data");
        item_data = getIntent().getStringArrayExtra("item_data");

        /** 初始化按鈕 */
        nav_item_name_edit = (ImageButton) findViewById(R.id.imageButton_item_name_edit);
        nav_description_edit = (ImageButton) findViewById(R.id.imageButton_item_description_edit);
        nav_item_name = (TextView) findViewById(R.id.textView_item_name);
        nav_item_creator = (TextView) findViewById(R.id.textView_item_creator);
        nav_item_description = (TextView) findViewById(R.id.textView_item_description);
        //若非主持人則隱藏按鈕
        if(!table_data[8].equals(user_info[0])){
            nav_item_name_edit.setVisibility(View.INVISIBLE);
            nav_description_edit.setVisibility(View.INVISIBLE);
        }
        nav_item_name.setText(item_data[1]);
        nav_item_creator.setText(item_data[5]);
        nav_item_description.setText(item_data[2]);
        nav_item_name_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemName(item_data[0]);
            }
        });
        nav_description_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemInfo(item_data[0]);
            }
        });

        /** 初始化各元件 */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(item_data[1]);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        drawer = (DrawerLayout) findViewById(R.id.t_table_tab_drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //取得論點資料
        getArgument(item_data[0], user_info[0]);
        //將資料塞入頁面
        /** 這裡的"支持"與"不支持"字串，是Tab上的String */
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        supportFragment = new SupportFragment(this, user_info, table_data, item_data, support_data);
        notSupportFragment = new NotSupportFragment(this, user_info, table_data, item_data, notSupport_data);
        viewPagerAdapter.addFragments(supportFragment, "支持");
        viewPagerAdapter.addFragments(notSupportFragment, "不支持");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        fab_right = (FloatingActionButton) findViewById(R.id.t_table_tab_fab_right);
        /** 配置右邊FloatingButton的監聽器 */
        fab_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新增論點
                createArgument(table_data[0], item_data[0], user_info[0], viewPager.getCurrentItem());
            }
        });

        /** 取代舊版ActionBar */
        setSupportActionBar(toolbar);
        /** 左上角出現返回鍵 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateScreenThead = UpdateScreenThead.getInstance();
        updateScreenThead.execute(handler);
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
        switch(item.getItemId()){
            case android.R.id.home:
                /** 對用戶按home icon的處理，本例只需關閉activity，就可返回上一activity，即主activity。 */
                finish();
                return true;
            case R.id.action_info:
                drawer.openDrawer(GravityCompat.END);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //取得論點資料
    public void getArgument(String item_id, String user_id){
        support_data = new ArrayList<>();
        notSupport_data = new ArrayList<>();
        try {
            /*
                            顯示決策桌的項目
                             依照目前狀態選用不同的sql
                             評分中看T_argument_score中自己的分數
                             完結了直接看Item_argument的分數
                        */
            String sql;
            if(table_data[5].equals("Y")){
                sql = "SELECT `a`.*, `b`.`Name` as 'Account_Name'" +
                        "FROM `Item_argument` `a`, `Account` `b`" +
                        "WHERE `a`.`Account_ID` = `b`.`ID`" +
                        "   AND `a`.`Tables_item_ID` = '"+ item_id +"'" +
                        "ORDER BY `ID` ASC";
            }else{
                sql = "SELECT `a`.`ID` , " +
                        "             `a`.`Name` ," +
                        "             `a`.`Type` ," +
                        "             `a`.`Info` ," +
                        "              `b`.`Score`," +
                        "             `a`.`Tables_item_ID` , " +
                        "             `a`.`Account_ID`," +
                        "             `c`.`Name` `Account_Name`" +
                        "FROM `Item_argument` `a`LEFT JOIN (SELECT *" +
                        "                                                                              FROM `T_argument_score`" +
                        "                                                                          WHERE `Account_ID` = '" + user_id + "') `b`" +
                        "                                                         ON `a`.`ID` = `b`.`Argument_ID` , `Account` `c` " +
                        "WHERE `a`.`Account_ID` = `c`.`ID`" +
                        "      AND `a`.`Tables_item_ID` ='"+ item_id +"'";
            }

            String result = DBConnector.executeQuery(sql);
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String[] data = new String[] {
                        jsonData.getString("ID"),
                        jsonData.getString("Name"),
                        jsonData.getString("Type"),
                        jsonData.getString("Info"),
                        jsonData.getString("Score"),
                        jsonData.getString("Tables_item_ID"),
                        jsonData.getString("Account_ID"),
                        jsonData.getString("Account_Name")};

                switch (jsonData.getString("Type")){
                    case "支持":
                        support_data.add(data);
                        break;
                    case "不支持":
                        notSupport_data.add(data);
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //創建新項目
    public void createArgument(final String table_id, final String item_id, final String user_id, final int type_id){
        //先初步檢查是否可以新增
        if(table_data[5].equals("Y")){
            Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
            return;
        }else if(table_data[6].equals("Y")){
            Toast.makeText(getApplicationContext(), "目前為評分中狀態", Toast.LENGTH_SHORT).show();
            return;
        }else{
            //取得資料庫資訊確定真的可以新增
            table_data = TableFunction.table_data(table_id);
            if(table_data[5].equals("Y")){
                Toast.makeText(getApplicationContext(), "決策桌已完結", Toast.LENGTH_SHORT).show();
                return;
            }else if(table_data[6].equals("Y")){
                Toast.makeText(getApplicationContext(), "目前為評分中狀態", Toast.LENGTH_SHORT).show();
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
                        String type;
                        if(type_id == 0){
                            type = "支持";
                        }else{
                            type = "不支持";
                        }
                        TextView text = (TextView) dialog_text.findViewById(R.id.editText);
                        String sql = "INSERT INTO `Item_argument` ( `Name`, `Type`, `Tables_item_ID`, `Account_ID`)" +
                                "               VALUES('"+text.getText()+"', '"+ type +"', "+item_id+", '"+user_id+"');";
                        DBConnector.executeQuery(sql);
                        //新增完更新畫面
                        getArgument(item_id, user_id);
                        supportFragment.reload(support_data);
                        notSupportFragment.reload(notSupport_data);
                    }
                });
                newitem.show();
            }
        }
    }

    //右側選單修改項目名稱
    public void updateItemName(final String item_id){
        final View dialog_text = LayoutInflater.from(this).inflate(R.layout.dialog_text, null);
        final TextView text = (TextView) dialog_text.findViewById(R.id.editText);
        text.setText(item_data[1]);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("修改項目名稱");
        dialog.setView(dialog_text);
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "UPDATE `Tables_item` SET `Name` = '"+ text.getText()+"'"+
                        "           WHERE `ID` = "+ item_id +";";
                DBConnector.executeQuery(sql);
                item_data[1] = String.valueOf(text.getText());
                getSupportActionBar().setTitle(text.getText());
                nav_item_name.setText(text.getText());
            }
        });
        dialog.show();
    }

    //右側選單修改項目INFO
    public void updateItemInfo(final String item_id){
        final View dialog_text = LayoutInflater.from(this).inflate(R.layout.dialog_text_multi_line, null);
        final TextView text = (TextView) dialog_text.findViewById(R.id.editText);
        text.setText(item_data[2]);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("修改項目描述");
        dialog.setView(dialog_text);
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "UPDATE `Tables_item` SET `Info` = '"+ text.getText()+"'"+
                        "           WHERE `ID` = "+ item_id +";";
                DBConnector.executeQuery(sql);
                item_data[2]=String.valueOf(text.getText());
                nav_item_description.setText(text.getText());
            }
        });
        dialog.show();
    }


    //給多執行緒更新畫面的介面
    private Handler handler = new Handler(){
        public  void  handleMessage(Message msg) {
            super.handleMessage(msg);
            getArgument(item_data[0], user_info[0]);
            supportFragment.reload(support_data);
            notSupportFragment.reload(notSupport_data);
            getSupportActionBar().setTitle(item_data[1]);
        }
    };
}
