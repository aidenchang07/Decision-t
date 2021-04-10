package com.decision_t.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity<P extends IBasePresenter<IBaseView>> extends AppCompatActivity implements IBaseView {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private P mPresenter;

    protected abstract P setPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = setPresenter();
        mPresenter.attech(this);
    }

    public P getPresenter() {
        return mPresenter;
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
