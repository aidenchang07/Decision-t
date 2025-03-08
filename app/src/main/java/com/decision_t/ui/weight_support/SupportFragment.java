package com.decision_t.ui.weight_support;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.decision_t.R;
import com.decision_t.manager.DBConnector;
import com.decision_t.manager.TableFunction;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends Fragment {

    private AppCompatActivity t_tab;
    private String[] user_info;
    private String[] table_data;
    private String[] item_data;
    private ArrayList<String[]>support_data;
    private ListView listView;

    public SupportFragment(AppCompatActivity aa,
                           String[] user_info,
                           String[] table_data,
                           String[] item_data,
                           ArrayList<String[]> support_data)
    {
        this.t_tab = aa;
        this.user_info = user_info;
        this.table_data = table_data;
        this.item_data = item_data;
        this.support_data = support_data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_support, container, false);
        listView = (ListView) rootView.findViewById(R.id.t_table_tab_support_list);
        listView.setAdapter(new MyAdapter(getActivity()));
        listView.setOnItemClickListener(click_item_list);
        listView.setOnItemLongClickListener(long_click_item_list);

        return rootView;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        public MyAdapter(Context c) {
            myInflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            return support_data.size();
        }

        @Override
        public Object getItem(int position) {
            return support_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //產生一個table_list_view的view
            convertView = myInflater.inflate(R.layout.t_table_argument_list_view, null);
            //設定元件內容
            TextView itemtitle = (TextView) convertView.findViewById(R.id.t_argument_name);
            itemtitle.setText(support_data.get(position)[1]);
            TextView itemaccount = (TextView) convertView.findViewById(R.id.t_argument_account);
            itemaccount.setText("建立者:" + support_data.get(position)[7]+"("+support_data.get(position)[6]+")");
            TextView itemscore = (TextView) convertView.findViewById(R.id.t_argument_score);
            if(support_data.get(position)[4].equals("null")){
                support_data.get(position)[4] = "0";
            }
            itemscore.setText(support_data.get(position)[4]);
            //如果為進行中(決策桌剛建立)將score隱藏
            if(table_data[5].equals("N") && table_data[6].equals("N")){
                itemscore.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    //決策桌表按下事件
    private AdapterView.OnItemClickListener click_item_list
            = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            //如果是投票中狀態且尚未投過票，則可以投票
            //先檢查目前狀態
            table_data = TableFunction.table_data(table_data[0]);//更新桌資訊
            if(table_data[5].equals("N")){
                if(table_data[6].equals("Y")){
                    if(table_data[7].equals("") || table_data[7].equals("null")){//確定還沒被主持人關閉即可投票
                        score(position);
                    }
                }//未鎖定之前點擊沒反應
            }else{
                Toast.makeText(t_tab, "決策桌已完結！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //評分
    private void score(final int position) {
        String[] function = {"0分", "1分", "2分", "3分", "4分", "5分"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(t_tab);
        dialog.setTitle("給 " + support_data.get(position)[1] + " 評分");
        dialog.setItems(function, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //which是指選擇的項目在function裡的index所以直接拿來用就行
                String sql = "INSERT INTO `T_argument_score`" +
                        "                   VALUES ('" + support_data.get(position)[0] + "'," +
                        "                                      '" + user_info[0] + "', " +
                        "                                     " + which + ")" +
                        "           ON DUPLICATE KEY UPDATE `Score` = " + which + ";";
                DBConnector.executeQuery(sql);
                support_data.get(position)[4] = String.valueOf(which);
                listView.setAdapter(new MyAdapter(getActivity()));
            }
        });
        dialog.show();
    }

    //項目列長按
    private AdapterView.OnItemLongClickListener long_click_item_list = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder check = new AlertDialog.Builder(t_tab);
            check.setTitle("確定刪除?");
            check.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String sql;
                        //先檢查是否為決策桌主持人
                        if(!table_data[8].equals(user_info[0])){
                            //再檢查是否為該論點創建者
                            sql = "SELECT * FROM `Item_argument` WHERE `ID`='"+support_data.get(position)[0]+"' AND `Account_ID`='"+user_info[0]+"';";
                            String result = DBConnector.executeQuery(sql);
                            JSONArray jsonArray = new JSONArray(result);
                            if(jsonArray.length() == 0) {
                                Toast.makeText(t_tab, "您不能刪除其他人新增的項目", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        //再檢查目前狀態
                        table_data = TableFunction.table_data(table_data[0]);//更新桌資訊
                        if(table_data[5].equals("Y")){
                            Toast.makeText(t_tab, "決策桌已完結", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(table_data[6].equals("Y")){
                            Toast.makeText(t_tab, "目前為評分中狀態", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            sql = "DELETE FROM `Item_argument`" +
                                    "WHERE `ID` = '"+support_data.get(position)[0]+"';";
                            DBConnector.executeQuery(sql);
                            support_data.remove(position);
                            listView.setAdapter(new MyAdapter(getActivity()));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
            check.show();
            return true;
        }
    };

    //供外層去更新資料
    public void reload(ArrayList<String[]> support_data){
        this.support_data = support_data;
        listView.setAdapter(new MyAdapter(getActivity()));
    }
}
