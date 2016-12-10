package com.wyh.Myeasyshop.main.me.goodsupload;


import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.wyh.Myeasyshop.commons.MyFileUtils;
import com.wyh.Myeasyshop.model.GoodsUpLoad;
import com.wyh.Myeasyshop.model.GoodsUpLoadResult;
import com.wyh.Myeasyshop.model.ImageItem;
import com.wyh.Myeasyshop.network.EasyShopClient;
import com.wyh.Myeasyshop.network.UICallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public class GoodsUpLoadPresenter extends MvpNullObjectBasePresenter<com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadView> implements MvpPresenter<GoodsUpLoadView> {

    private Call call;

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (call != null) call.cancel();
    }

    //商品上传
    public void upLoad(GoodsUpLoad goodsUpLoad, List<ImageItem> list){
        getView().showPrb();
        call = EasyShopClient.getInstance().upload(goodsUpLoad,getFiles(list));
        call.enqueue(new UICallBack() {
            @Override
            public void onFailureUI(Call call, IOException e) {
                getView().hidePrb();
                getView().showMsg(e.getMessage());
            }

            @Override
            public void onResponseUI(Call call, String body) {
                getView().hidePrb();
                GoodsUpLoadResult result = new Gson().fromJson(body,GoodsUpLoadResult.class);
                getView().showMsg(result.getMessage());
                //上传成功
                if (result.getCode() == 1){
                    getView().upLoadSuccess();
                }
            }
        });
    };

    //根据imageItem获取图片文件
    private ArrayList<File> getFiles(List<ImageItem> list){
        ArrayList<File> files = new ArrayList<>();
        for (ImageItem imageItem : list){
            //拿到图片
            File file = new File(MyFileUtils.SD_PATH + imageItem.getImagePath());
            files.add(file);
        }
        return files;
    }

}
