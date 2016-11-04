package com.decision_t;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MemberActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private ListView listView;

    /** 以下的資料是測試用，可刪除。2016/10/31 */
    private ArrayAdapter<String> arrayAdapter;
    private String[] items = new String[] { "China", "India", "United States", "Indonesia", "Brazil",
            "Pakistan", "Nigeria", "Bangladesh", "Russia", "Japan", "Mexico", "Philippines",
            "Vietnam", "Ethiopia", "Egypt", "Germany", "Iran", "Turkey",
            "Democratic Republic of the Congo", "Thailand", "France", "United Kingdom", "Italy",
            "South Africa", "Myanmar", "South Korea", "Colombia", "Spain", "Ukraine", "Tanzania",
            "Kenya", "Argentina", "Poland", "Algeria", "Canada" };

    /** 以上的資料是測試用，可刪除。2016/10/31 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        /** 初始化各項元件 */
        toolbar = (Toolbar) findViewById(R.id.toolbar_member);
        listView = (ListView) findViewById(R.id.listView_member) ;

        /** 左上角有返回鍵 */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /** 以下的資料是測試用，可刪除。2016/10/31 */
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(arrayAdapter);
        /** 以上的資料是測試用，可刪除。2016/10/31 */

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
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
        searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /** 按下確認，才會搜索 */
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /** 文字有變更，會即時搜索 */

                /** 過濾文字 */
                arrayAdapter.getFilter().filter(newText);

                return false;
            }
        });

        /** 設置是否顯示確認搜索按鈕 */
        searchView.setSubmitButtonEnabled(true);

        /** 設置搜索框內默認顯示的提示文本 */
        searchView.setQueryHint("搜尋成員");

        return super.onCreateOptionsMenu(menu);
    }

}
