package com.wyh.Myeasyshop.main.me;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuicuiedu.idedemo.Myeasyshop.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wyh.Myeasyshop.commons.ActivityUtils;
import com.wyh.Myeasyshop.components.AvatarLoadOptions;
import com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadActivity;
import com.wyh.Myeasyshop.main.me.persiongoods.PersonGoodsActivity;
import com.wyh.Myeasyshop.main.me.personInfo.PersonActivity;
import com.wyh.Myeasyshop.model.CachePreferences;
import com.wyh.Myeasyshop.network.EasyShopApi;
import com.wyh.Myeasyshop.user.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    @BindView(R.id.iv_user_head)
    ImageView iv_user_head;
    @BindView(R.id.tv_login)
    TextView tv_login;

    private View view;
    private ActivityUtils activityUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_me, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (CachePreferences.getUser().getName() == null) return;
        if (CachePreferences.getUser().getNick_name() == null){
            tv_login.setText("请输入昵称");
        }else{
            tv_login.setText(CachePreferences.getUser().getNick_name());
        }
        ImageLoader.getInstance()
                .displayImage(EasyShopApi.IMAGE_URL + CachePreferences.getUser().getHead_Image()
                        ,iv_user_head, AvatarLoadOptions.build());
    }

    @OnClick({R.id.iv_user_head, R.id.tv_person_info, R.id.tv_login, R.id.tv_person_goods, R.id.tv_goods_upload})
    public void onClick(View view) {
        if (CachePreferences.getUser().getName() == null){
            activityUtils.startActivity(LoginActivity.class);
            return;
        }
        switch (view.getId()){
            case R.id.iv_user_head:
            case R.id.tv_login:
            case R.id.tv_person_info:
                activityUtils.startActivity(PersonActivity.class);
                break;
            case R.id.tv_person_goods:
                activityUtils.startActivity(PersonGoodsActivity.class);
                break;
            case R.id.tv_goods_upload:
                activityUtils.startActivity(GoodsUpLoadActivity.class);
                break;
        }
    }
}
