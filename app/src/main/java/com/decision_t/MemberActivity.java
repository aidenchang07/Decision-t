package com.decision_t;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MemberActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        toolbar = (Toolbar) findViewById(R.id.member_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //創建 toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.member_menu, menu);

        //獲取 SearchView
        MenuItem searchItem = menu.findItem(R.id.member_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //當使用者輸入完成或是點了Enter鍵，就會觸發此方法
                /**
                 * 注意：在按下和鬆開時，會觸發 action_down 和 action_up的方法，所以可加此行
                 * searchView.setIconified(true);
                 * 防止資料讀取2次
                 */
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //監聽輸入框的變化。若使用者正在輸入文字，會持續持行此方法
                return false;
            }
        });
        return true;
    }

}
