package com.decision_t;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import static android.R.attr.id;

public class R_Table_Activity extends AppCompatActivity {

    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_table_activity_main);
        

        navigationView = (NavigationView) findViewById(R.id.r_table_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_r_table_name) {
                    Toast.makeText(getApplicationContext(), "你想修改隨機桌名稱？", Toast.LENGTH_SHORT).show();
                }

                //按完之後關起來
                drawer = (DrawerLayout) findViewById(R.id.r_table_drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });

        toolbar = (Toolbar) findViewById(R.id.r_table_toolbar);
        //toolbar.setLogo(R.drawable.ic_action_arrow_left);//沒有LOGO要的是返回的"按鈕"   20161003 14:09
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);      //同不要LOGO            20161003 14:09
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.r_table_drawer_layout);
        /*這邊拿掉  這頁不要搞漢堡而且還ERROR         20161003 14:09
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/

        //初始化 FloatingActionButton
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //點擊會有反應
                Toast.makeText(getApplicationContext(), "你想新增隨機桌的項目？", Toast.LENGTH_SHORT).show();
            }
        });
        //以下可刪，測試位置是否正確 20161003 14:09
        ListView ll = (ListView) findViewById(R.id.r_table_list);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, new String[]{"sad","sad","sad","sad"});
        ll.setAdapter(ad);
    }

    //創建右上角的 info
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.r_table_toolbar_menu, menu);
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
}
