package com.decision_t;

import android.content.DialogInterface;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_table_tab);

        //取得相關資料
        user_info = getIntent().getStringArrayExtra("user_info");
        table_data = getIntent().getStringArrayExtra("table_data");
        item_data = getIntent().getStringArrayExtra("item_data");

        /** 初始化各元件 */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        drawer = (DrawerLayout) findViewById(R.id.t_table_tab_drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //取得論點資料
        getArgument(item_data[0], user_info[0]);
        //將資料塞入頁面
        /** 這裡的"支持"與"不支持"字串，是Tab上的String */
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
                //點擊新增項目
                final View dialog_text = LayoutInflater.from(T_Table_Tab_Activity.this).inflate(R.layout.dialog_text, null);
                AlertDialog.Builder newitem = new AlertDialog.Builder(T_Table_Tab_Activity.this);
                newitem.setTitle("請輸入新論點");
                newitem.setView(dialog_text);
                newitem.setPositiveButton("新增", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO This 2016-11-16
                    }
                });
                newitem.show();
            }
        });

        /** 取代舊版ActionBar */
        setSupportActionBar(toolbar);
        /** 左上角出現返回鍵 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        "                                                                          WHERE `Account_ID` = 'sdjsddsd@gmail.com') `b`" +
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
}
