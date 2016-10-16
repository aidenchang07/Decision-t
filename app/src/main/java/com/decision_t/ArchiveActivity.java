package com.decision_t;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ArchiveActivity extends AppCompatActivity {

    private Toolbar toolbar;

    /** 以下是測試ListView用，不用可刪除 */
    private ListView testListView;
    private String[] list = {"Aiden", "Luke", "Alice",  "Belgium", "France", "France", "Italy", "Germany", "Spain"};
    private ArrayAdapter<String> testListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        /** 初始化toolbar */
        toolbar = (Toolbar) findViewById(R.id.toolbar_archive);

        /** 初始化ListView，測試用可刪除 */
        testListView = (ListView) findViewById(R.id.listView_archive);

        /** 左上角有返回鍵 */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /** 測試用，可刪除 */
        testListAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        testListView.setAdapter(testListAdapter);
        testListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "你選擇的是" + list[position], Toast.LENGTH_SHORT).show();
            }
        });

    }
}
