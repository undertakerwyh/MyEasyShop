package com.wyh.Myeasyshop.main.me.persiongoods;


import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.wyh.Myeasyshop.main.shop.ShopView;
import com.wyh.Myeasyshop.model.CachePreferences;
import com.wyh.Myeasyshop.model.GoodsResult;
import com.wyh.Myeasyshop.network.EasyShopClient;
import com.wyh.Myeasyshop.network.UICallBack;

import java.io.IOException;

import okhttp3.Call;

/**
 * Created by Damon on 2016/11/27.
 */

public class PersonGoodsPresenter extends MvpNullObjectBasePresenter<ShopView> {

    /**
     * 获取商品时,分页下标
     */
    private int pageInt = 1;

    private Call call;

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (call != null) call.cancel();
    }

    //刷新数据
    public void refreshData(String type) {
        getView().showRefresh();
        //刷新数据分页写死为1，显示最新数据
        call = EasyShopClient.getInstance().getPersonData(1, type, CachePreferences.getUser().getName());
        call.enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                getView().showRefreshError(e.getMessage());
            }

            @Override
            public void onResponseUI(Call call, String body) {
                GoodsResult goodsResult = new Gson().fromJson(body, GoodsResult.class);
                switch (goodsResult.getCode()) {
                    case 1:
                        if (goodsResult.getData().size() == 0) {
                            getView().showRefreshEnd();
                        } else {
                            getView().addRefreshData(goodsResult.getData());
                            getView().hideRefresh();
                        }
                        pageInt = 2;
                        break;
                    default:
                        getView().showRefreshError(goodsResult.getMessage());
                }
            }
        });
    }

    //加载数据
    public void loadData(String type) {
        getView().showLoadMoreLoading();
        if (pageInt == 0) pageInt = 1;
        call = EasyShopClient.getInstance().getPersonData(pageInt, type, CachePreferences.getUser().getName());
        call.enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                getView().showLoadMoreError(e.getMessage());
            }

            @Override
            public void onResponseUI(Call call, String body) {
                GoodsResult goodsResult = new Gson().fromJson(body, GoodsResult.class);
                switch (goodsResult.getCode()) {
                    case 1:
                        if (goodsResult.getData().size() == 0) {
                            getView().showLoadMoreEnd();
                        } else {
                            getView().addMoreData(goodsResult.getData());
                            getView().hideLoadMore();
                        }
                        pageInt++;
                        break;
                    default:
                        getView().showLoadMoreError(goodsResult.getMessage());
                }
            }
        });
    }


}
