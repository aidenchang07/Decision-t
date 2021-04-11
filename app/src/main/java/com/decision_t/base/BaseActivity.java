package com.decision_t.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private VB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getBinding().getRoot());
    }

    public VB getBinding() {
        if (binding == null) binding = getInflatedBinding();
        return binding;
    }

    public abstract VB getInflatedBinding();
}
