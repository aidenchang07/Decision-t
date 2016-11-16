package com.decision_t.t_table_tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.decision_t.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends Fragment {

    /** 以下是測試用資料，可刪除 2016/11/16 */
    String[] lists = {"Create",  "different",  "classes",  "extending",  "fragments"};
    /** 以上是測試用資料，可刪除 2016/11/16 */

    public SupportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_support, container, false);
        ListAdapter listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, lists);
        ListView listView = (ListView) rootView.findViewById(R.id.t_table_tab_support_list);
        listView.setAdapter(listAdapter);

        return rootView;
    }

}
