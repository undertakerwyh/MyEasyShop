package com.wyh.Myeasyshop.main.shop.details;


import com.hannesdorfmann.mosby.mvp.MvpView;
import com.wyh.Myeasyshop.model.GoodsDetail;
import com.wyh.Myeasyshop.model.User;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/25 0025.
 */

public interface GoodsDetailView extends MvpView {

    void showProgress();

    void hideProgress();

    /*设置图片路径*/
    void setImageData(ArrayList<String> viewList);

    /*设置商品信息*/
    void setData(GoodsDetail data, User goods_user);

    /*商品不存在了*/
    void showError();

    void showMessage(String msg);

    /*删除商品*/
    void deleteEnd();
}
