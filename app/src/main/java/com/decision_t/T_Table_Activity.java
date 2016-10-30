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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class T_Table_Activity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_table_activity_main);

        /** 初始化按鈕 */
        tablename_edit = (Button) findViewById(R.id.button_tablename_edit);
        description_edit = (Button) findViewById(R.id.button_description_edit);
        member_edit = (Button) findViewById(R.id.button_member_edit);

        /** 初始化文字框 */
        description = (TextView) findViewById(R.id.textView_description);

        /** 初始化 FloatingActionButton */
        fab_left = (FloatingActionMenu) findViewById(R.id.t_table_fab_menu_left);
        fab_left_start = (FloatingActionButton) findViewById(R.id.t_table_fab_menu_item_start);
        fab_left_end = (FloatingActionButton) findViewById(R.id.t_table_fab_menu_item_end);
        fab_right = (FloatingActionButton) findViewById(R.id.t_table_fab_right);

        //右側欄menu初始化
        navigationView = (NavigationView) findViewById(R.id.t_table_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //按完之後關起來
                drawer = (DrawerLayout) findViewById(R.id.t_table_drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });

        //toolbar初始化
        toolbar = (Toolbar) findViewById(R.id.t_table_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("決策桌名稱");

        drawer = (DrawerLayout) findViewById(R.id.t_table_drawer_layout);

        /** 點旁邊可收合FloatingButton */
        fab_left.setClosedOnTouchOutside(true);

        /** 結束決策按鈕變灰色，無法使用 */
        fab_left_end.setEnabled(false);

        /** 配置右邊FloatingButton的監聽器 */
        fab_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //點擊新增項目
                final View dialog_text = LayoutInflater.from(T_Table_Activity.this).inflate(R.layout.dialog_text, null);
                AlertDialog.Builder newitem = new AlertDialog.Builder(T_Table_Activity.this);
                newitem.setTitle("請輸入新項目");
                newitem.setView(dialog_text);
                newitem.setPositiveButton("新增", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO This 2016-10-30
                    }
                });
                newitem.show();
            }
        });

        /** 配置開始決策的監聽器 */
        fab_left_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Todo this Action 2016-10-30 15:04 */
                Toast.makeText(getApplication(), "開始決策！", Toast.LENGTH_SHORT).show();
                /** 開始決策按鈕關起來，無法使用 */
                fab_left_start.setEnabled(false);
                /** 結束決策按鈕亮起來，已可使用 */
                fab_left_end.setEnabled(true);
            }
        });

        /** 配置結束決策的監聽器 */
        fab_left_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo this Action 2016-10-30 15:05
                Toast.makeText(getApplication(), "結束決策！", Toast.LENGTH_SHORT).show();
                /** 結束決策按鈕變灰色，無法使用 */
                fab_left_end.setEnabled(false);
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
