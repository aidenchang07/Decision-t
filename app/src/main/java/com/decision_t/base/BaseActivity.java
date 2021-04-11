package com.decision_t.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity<P extends IBasePresenter<IBaseView>, VB extends ViewBinding> extends AppCompatActivity implements IBaseView {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private P mPresenter;
    private VB binding;

    protected abstract P setPresenter();
    public abstract VB getInflatedBinding();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getBinding().getRoot());

        mPresenter = setPresenter();
        mPresenter.attech(this);
    }

    public P getPresenter() {
        return mPresenter;
    }

    public VB getBinding() {
        if (binding == null) binding = getInflatedBinding();
        return binding;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 解綁，避免內存洩漏 --str
        mPresenter.detech();
        mPresenter = null;
        // 解綁，避免內存洩漏 --end
    }
}
