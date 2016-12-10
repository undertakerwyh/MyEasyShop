package com.wyh.Myeasyshop;

import com.feicuiedu.apphx.HxBaseApplication;
import com.feicuiedu.apphx.HxModuleInitializer;
import com.feicuiedu.apphx.model.repository.DefaultLocalInviteRepo;
import com.feicuiedu.apphx.model.repository.DefaultLocalUsersRepo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wyh.Myeasyshop.model.CachePreferences;

/**
 * Created by Administrator on 2016/11/21 0021.
 */

public class EasyShopApplication extends HxBaseApplication{

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化ImageLoader
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//开启内存缓存
                .cacheOnDisk(true)//开启硬盘缓存
                .resetViewBeforeLoading(true)//再imageView加载前清除它之前的图片
                .build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(4 * 1024 * 1024)//设置内存缓存的大小（4M）
                .defaultDisplayImageOptions(displayImageOptions)
                .build();
        ImageLoader.getInstance().init(configuration);


        //初始化本地配置
        CachePreferences.init(this);
    }

    //初始化环信模块
    @Override
    protected void initHxModule(HxModuleInitializer initializer) {
        initializer.setLocalInviteRepo(DefaultLocalInviteRepo.getInstance(this))
                .setLocalUsersRepo(DefaultLocalUsersRepo.getInstance(this))
                .setRemoteUsersRepo(new RemoteUserRepo())
                .init();
    }
}
