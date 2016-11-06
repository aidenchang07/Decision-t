package com.decision_t.t_table_tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.decision_t.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotSupportFragment extends Fragment {

    public NotSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_not_support, container, false);
    }

}
