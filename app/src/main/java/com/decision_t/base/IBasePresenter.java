package com.decision_t.base;

public interface IBasePresenter<V extends IBaseView> {

    void attech(V view);

    void detech();
}
