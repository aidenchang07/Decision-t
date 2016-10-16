package com.decision_t;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class V_Table_Activity extends AppCompatActivity {

    private Button tablename_edit;
    private Button description_edit;
    private Button member_edit;

    private TextView description;

    private DrawerLayout drawer;
    private FloatingActionMenu fab_left;
    private FloatingActionButton fab_left_start;
    private FloatingActionButton fab_left_end;
    private FloatingActionButton fab_right;
    private NavigationView navigationView;
    private Toolbar toolbar;

    /** 以下是測試ListView用，不用可刪除 */
    private ListView testListView;
    private String[] list = {"Aiden", "Luke", "Alice",  "Belgium", "France", "France", "Italy", "Germany", "Spain"};
    private ArrayAdapter<String> testListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_table_activity_main);

        /** 初始化按鈕 */
        tablename_edit = (Button) findViewById(R.id.button_tablename_edit);
        description_edit = (Button) findViewById(R.id.button_description_edit);
        member_edit = (Button) findViewById(R.id.button_member_edit);

        /** 初始化文字框 */
        description = (TextView) findViewById(R.id.textView_description);
        /** 測試，不用可刪除 */
        description.setText("qqq\nqq\nqqq\nqq\nq\nq\nq\nq\nq\nq\nqqqqqqqqqqqqqqq");

        //右側欄menu初始化
        navigationView = (NavigationView) findViewById(R.id.v_table_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //按完之後關起來
                drawer = (DrawerLayout) findViewById(R.id.v_table_drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });

        //toolbar初始化
        toolbar = (Toolbar) findViewById(R.id.v_table_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("投票桌名稱");

        drawer = (DrawerLayout) findViewById(R.id.v_table_drawer_layout);

        //初始化右邊的 FloatingActionButton
        fab_right = (FloatingActionButton) findViewById(R.id.v_table_fab_right);
        fab_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //點擊新增項目
                final View dialog_text = LayoutInflater.from(V_Table_Activity.this).inflate(R.layout.dialog_text, null);
                AlertDialog.Builder newitem = new AlertDialog.Builder(V_Table_Activity.this);
                newitem.setTitle("請輸入新項目");
                newitem.setView(dialog_text);
                newitem.setPositiveButton("新增", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO This 2016-10-10
                    }
                });
                newitem.show();
            }
        });

        //初始化左邊 FloatingActionButton 的開始投票
        fab_left = (FloatingActionMenu) findViewById(R.id.v_table_fab_menu_left) ;
        /** 點旁邊可收合FloatingButton */
        fab_left.setClosedOnTouchOutside(true);
        fab_left_start = (FloatingActionButton) findViewById(R.id.v_table_fab_menu_item_start);
        fab_left_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //開始投票
                /* Todo this Action 2016-10-10 16:49 */
                Toast.makeText(getApplication(), "開始投票！", Toast.LENGTH_SHORT).show();
            }
        });

        //初始化左邊 FloatingActionButton 的結束投票
        fab_left_end = (FloatingActionButton) findViewById(R.id.v_table_fab_menu_item_start);
        fab_left_end = (FloatingActionButton) findViewById(R.id.v_table_fab_menu_item_end);
        fab_left_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //開始投票
                // Todo this Action 2016-10-10 16:49
                Toast.makeText(getApplication(), "結束投票！", Toast.LENGTH_SHORT).show();
            }
        });

        //以下是測試側欄的ListView的效果如何，不用可刪除
        // TODO This 20161010 00:55
        View v = findViewById(R.id.v_table_nav_right);
        testListView = (ListView) v.findViewById(R.id.listView_member);
        testListAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        testListView.setAdapter(testListAdapter);
        testListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "你選擇的是" + list[position], Toast.LENGTH_SHORT).show();
            }
        });

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

}
