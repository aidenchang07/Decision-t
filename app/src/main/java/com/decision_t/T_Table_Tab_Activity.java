package com.decision_t;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.decision_t.t_table_tab.NotSupportFragment;
import com.decision_t.t_table_tab.SupportFragment;
import com.decision_t.t_table_tab.ViewPagerAdapter;

public class T_Table_Tab_Activity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_table_tab);

        /** 初始化各元件 */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        drawer = (DrawerLayout) findViewById(R.id.t_table_tab_drawer_layout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        /** 這裡的"支持"與"不支持"字串，是Tab上的String */
        viewPagerAdapter.addFragments(new SupportFragment(), "支持");
        viewPagerAdapter.addFragments(new NotSupportFragment(), "不支持");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //右側欄menu初始化
        navigationView = (NavigationView) findViewById(R.id.t_table_tab_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //按完之後關起來
                drawer.closeDrawer(GravityCompat.END);
                return true;
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

}
