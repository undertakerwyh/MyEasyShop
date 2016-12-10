package com.wyh.Myeasyshop.main.me.goodsupload;


import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface GoodsUpLoadView extends MvpView {

    void showPrb();

    void hidePrb();

    void upLoadSuccess();

    void showMsg(String msg);
}
