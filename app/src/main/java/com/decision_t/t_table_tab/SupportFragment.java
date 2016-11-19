package com.decision_t.t_table_tab;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.decision_t.R;

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
        MyAdapter myAdapter = new MyAdapter(getActivity());
        ListView listView = (ListView) rootView.findViewById(R.id.t_table_tab_support_list);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    Toast.makeText(t_tab.getApplicationContext(), "text", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            convertView = myInflater.inflate(R.layout.x_table_item_list_view, null);
            /*
            //產生一個table_list_view的view
            convertView = myInflater.inflate(R.layout.x_table_item_list_view, null);
            //設定元件內容
            TextView itemtitle = (TextView) convertView.findViewById(R.id.x_item_name);
            itemtitle.setText(support_data.get(position)[1]);
            TextView itemaccount = (TextView) convertView.findViewById(R.id.x_item_account);
            itemaccount.setText("建立者:" + support_data.get(position)[6]+"("+support_data.get(position)[5]+")");
            TextView itemscore = (TextView) convertView.findViewById(R.id.x_item_score);
            if(support_data.get(position)[3].equals("null")){
                support_data.get(position)[3] = "0";
            }
            itemscore.setText(support_data.get(position)[3]);
            //如果為進行中(決策桌剛建立)將score隱藏
            if(table_data[5].equals("N") && table_data[6].equals("N")){
                itemscore.setVisibility(View.INVISIBLE);
            }
            */
            return convertView;
        }
    }
}
