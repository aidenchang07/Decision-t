package com.decision_t;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String[] user_info;
    private ArrayList<String[]>data;
    private MyAdapter myAdapter;
    private ListView table_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        //取得使用者資料
        user_info = getIntent().getStringArrayExtra("user_info");

        /** 初始化toolbar */
        toolbar = (Toolbar) findViewById(R.id.toolbar_archive);

        /** 左上角有返回鍵 */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //table_list初始化
        table_list = (ListView) findViewById(R.id.listView_archive);
        table_list.setOnItemClickListener(click_table_list);
        table_list.setOnItemLongClickListener(long_click_table_list);

        //顯示決策桌列表(已封存)
        getTableList(user_info[0]);
    }
    public void getTableList(String user_id){
        //先清空資料
        data = new ArrayList<String[]>();
        try {
            //顯示封存的決策桌
            String sql = "SELECT a.*" +
                    "  FROM `Decision_tables` `a` left join `Decision_tables_member` `b`" +
                    "    ON `a`.`ID` = `b`.`Decision_tables_ID`" +
                    " WHERE (`a`.`Account_ID` = '" + user_id + "'" +
                    "                    OR `b`.`Account_ID` = '" + user_id + "')"+
                    "        AND  EXISTS (SELECT *" +
                    "                                        FROM `Decision_tables_archive`" +
                    "                                     WHERE `Decision_tables_ID`=`a`.`ID`" +
                    "                                           AND `Account_ID`='"+user_id+"')" +
                    "  ORDER BY `ID` DESC; ";
            String result = DBConnector.executeQuery(sql);
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                data.add(new String[] {
                        jsonData.getString("ID"),
                        jsonData.getString("Name"),
                        jsonData.getString("Type"),
                        jsonData.getString("Info"),
                        jsonData.getString("Private"),
                        jsonData.getString("Complete"),
                        jsonData.getString("Lock"),
                        jsonData.getString("Final_decision"),
                        jsonData.getString("Account_ID")});
            }

            myAdapter = new MyAdapter(this);
            table_list.setAdapter(myAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        public MyAdapter(Context c) {
            myInflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position)[1];
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        //重點：產生每一列的view
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //產生一個table_list_view的view
            // TODO Auto-generated method stub
            convertView = myInflater.inflate(R.layout.table_list_view, null);

            //設定元件內容
            TextView name = (TextView) convertView.findViewById(R.id.item_name);
            TextView id = (TextView) convertView.findViewById(R.id.table_id);
            ImageView table_status = (ImageView) convertView.findViewById(R.id.img_table_status);


            //塞資料
            String[] dd = data.get(position);
            name.setText(dd[1]);//決策桌名
            id.setText("ID:" + dd[0]);
            //若是成員而不是主持人則顯示圖片6
            if(!dd[8].equals(user_info[0])){
                table_status.setImageResource(R.drawable.table_list_shared);
            }
            return convertView;
        }
    }

    //決策桌表按下事件
    private AdapterView.OnItemClickListener click_table_list
            = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            switch (data.get(position)[2]){
                case "R":
                    Intent rTable = new Intent(ArchiveActivity.this, R_Table_Activity.class);
                    rTable.putExtra("user_info", user_info);
                    rTable.putExtra("table_data", data.get(position));
                    startActivityForResult(rTable, 1);
                    break;
                case "V":
                    Intent vTable = new Intent(ArchiveActivity.this, V_Table_Activity.class);
                    vTable.putExtra("user_info", user_info);
                    vTable.putExtra("table_data", data.get(position));
                    startActivityForResult(vTable, 1);
                    break;
                case "T":
                    break;
            }
        }
    };
    //決策桌表長按下事件
    private AdapterView.OnItemLongClickListener long_click_table_list = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            String[] function = {"取消封存", "刪除"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(ArchiveActivity.this);
            dialog.setTitle("操作");
            dialog.setItems(function, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            TableFunction.unarchive(data.get(position)[0], user_info[0]);
                            Toast.makeText(ArchiveActivity.this, "取消封存", Toast.LENGTH_SHORT).show();
                            getTableList(user_info[0]);
                            break;
                        case 1:
                            AlertDialog.Builder check = new AlertDialog.Builder(ArchiveActivity.this);
                            check.setTitle("確定刪除?");
                            check.setMessage("決策桌資料刪除後將不可挽回");
                            check.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(TableFunction.delete(data.get(position)[0], user_info[0])){
                                        Toast.makeText(ArchiveActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(ArchiveActivity.this, "您不是主持人", Toast.LENGTH_SHORT).show();
                                    }
                                    getTableList(user_info[0]);
                                }
                            });
                            check.show();
                            break;
                    }
                }
            });
            dialog.show();
            return true;
        }
    };
}