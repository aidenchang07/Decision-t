package com.decision_t.ui.member;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.decision_t.R;
import com.decision_t.base.BaseActivity;
import com.decision_t.databinding.ActivityMemberBinding;
import com.decision_t.manager.DBConnector;
import com.decision_t.manager.TableFunction;

import java.util.ArrayList;

/**
 * 搜尋成員頁
 */
public class MemberActivity extends BaseActivity<ActivityMemberBinding> {

    private Toolbar toolbar;
    private SearchView searchView;
    private ListView listView;
    private ArrayList<String[]> member_data;
    private String[] table_data;
    private MemberAdapter memberAdapter;
    //決策桌表按下事件
    private AdapterView.OnItemClickListener click_item_list
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder newitem = new AlertDialog.Builder(MemberActivity.this);
            newitem.setTitle("添加決策桌成員");
            newitem.setMessage("確定將\n" + member_data.get(position)[1] + "(" + member_data.get(position)[0] + ")\n加入此決策桌?");
            newitem.setPositiveButton("新增", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String sql = "INSERT INTO `Decision_tables_member` (`Decision_tables_ID` , `Account_ID` )\n" +
                            "                   VALUES ('" + table_data[0] + "', '" + member_data.get(position)[0] + "');";
                    DBConnector.executeQuery(sql);
                    //新增完更新畫面  直接重刷頁面不用再連一次資料庫
                    member_data.remove(position);
                    memberAdapter = new MemberAdapter(MemberActivity.this);
                    listView.setAdapter(memberAdapter);
                }
            });
            newitem.setNegativeButton("否", null);
            newitem.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        /** 初始化各項元件 */
        toolbar = (Toolbar) findViewById(R.id.toolbar_member);
        listView = (ListView) findViewById(R.id.listView_member);

        /** 左上角有返回鍵 */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //取得傳進來的資料
        table_data = getIntent().getStringArrayExtra("table_data");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /** 對用戶按home icon的處理，本例只需關閉activity，就可返回上一activity，即主activity。 */
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** 以下是初始化SearchView */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.member_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.member_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /** 按下確認，才會搜索 */
                showMemberList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /** 文字有變更，會即時搜索
                 // 過濾文字
                 arrayAdapter.getFilter().filter(newText);
                 */
                return false;
            }
        });

        /** 設置是否顯示確認搜索按鈕 */
        searchView.setSubmitButtonEnabled(true);

        /** 設置搜索框內默認顯示的提示文本 */
        searchView.setQueryHint("搜尋成員");

        return super.onCreateOptionsMenu(menu);
    }

    //顯示member資料
    public void showMemberList(String filter) {
        //取得member資料
        member_data = TableFunction.getNotYetMember(table_data[0], filter);
        //顯示member資料
        memberAdapter = new MemberAdapter(this);
        listView.setAdapter(memberAdapter);
        //設置監聽器
        listView.setOnItemClickListener(click_item_list);
    }

    @Override
    public ActivityMemberBinding getInflatedBinding() {
        return ActivityMemberBinding.inflate(getLayoutInflater());
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
