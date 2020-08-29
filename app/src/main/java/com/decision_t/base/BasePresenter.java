package com.decision_t.base;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class BasePresenter<V extends IBaseView> implements IBasePresenter {
    private static final String TAG = BasePresenter.class.getSimpleName();
    private SoftReference<IBaseView> mReferenceView;
    private V mProxyView;

    @Override
    public void attech(IBaseView view) {
        this.mReferenceView = new SoftReference<>(view);
        // 動態代理 --str
        mProxyView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(), view.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (mReferenceView == null || mReferenceView.get() == null) {
                    return null;
                }
                return method.invoke(mReferenceView.get(), args);
            }
        });
        // 動態代理 --end
    }

    public V getView() {
        return mProxyView;
    }

    @Override
    public void detech() {
        this.mReferenceView.clear();
        this.mReferenceView = null;
    }
}
