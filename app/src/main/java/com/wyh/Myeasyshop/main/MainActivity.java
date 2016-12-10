package com.wyh.Myeasyshop.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.feicuiedu.apphx.presentation.contact.list.HxContactListFragment;
import com.feicuiedu.apphx.presentation.conversation.HxConversationListFragment;
import com.fuicuiedu.idedemo.Myeasyshop.R;
import com.wyh.Myeasyshop.commons.ActivityUtils;
import com.wyh.Myeasyshop.main.me.MeFragment;
import com.wyh.Myeasyshop.main.shop.ShopFragment;
import com.wyh.Myeasyshop.model.CachePreferences;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindViews({R.id.tv_shop,R.id.tv_message,R.id.tv_mail_list,R.id.tv_me})
    TextView[] textViews;

    @BindView(R.id.main_toobar)
    Toolbar toolbar;
    @BindView(R.id.main_title)
    TextView tv_title;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    //点击2次返回，退出程序
    private boolean isExit = false;
    private ActivityUtils activityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        activityUtils = new ActivityUtils(this);
        //设置一下actionbar
        setSupportActionBar(toolbar);
        //设置一下ActionBae标题为空，否则默认显示应用名
        getSupportActionBar().setTitle("");

        init();
    }

    //进入页面数据初始化，默认显示为市场页面
    private void init(){
        //刚进来默认选择市场
        textViews[0].setSelected(true);
        //判断用户是否登录，从而选择不同的适配器
        if (CachePreferences.getUser().getName() == null){
            viewPager.setAdapter(unLoginAdapter);
            viewPager.setCurrentItem(0);
        }else{
            viewPager.setAdapter(loginAdapter);
            viewPager.setCurrentItem(0);
        }

        //viewpager添加滑动事件
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //textView选择处理
                for (TextView textView : textViews){
                    textView.setSelected(false);
                }
                //更改title，设置选择效果
                tv_title.setText(textViews[position].getText());
                textViews[position].setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private FragmentStatePagerAdapter unLoginAdapter = new
            FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            switch (position){
                //市场
                case 0:
                    return new ShopFragment();
                //消息
                case 1:
                    return new com.wyh.Myeasyshop.main.UnLoginFragment();
                //通讯录
                case 2:
                    return new com.wyh.Myeasyshop.main.UnLoginFragment();
                //我的
                case 3:
                    return new MeFragment();
            }
            return null;
        }
        @Override
        public int getCount() {
            return 4;
        }
    };

    private FragmentStatePagerAdapter loginAdapter = new
            FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    switch (position){
                        //市场
                        case 0:
                            return new ShopFragment();
                        //消息
                        case 1:
                            return new HxConversationListFragment();
                        //通讯录
                        case 2:
                            return new HxContactListFragment();
                        //我的
                        case 3:
                            return new MeFragment();
                    }
                    return null;
                }
                @Override
                public int getCount() {
                    return 4;
                }
            };


    //textview点击事件
    @OnClick({R.id.tv_shop,R.id.tv_message,R.id.tv_mail_list,R.id.tv_me})
    public void onClick(TextView view){
        for(int i = 0;i<textViews.length;i++){
            textViews[i].setSelected(false);
            textViews[i].setTag(i);
        }
        //设置选择效果
        view.setSelected(true);
        //参数false代表瞬间切换，而不是平滑过渡
        viewPager.setCurrentItem((Integer) view.getTag(),false);
        //设置一下toolbar的title
        tv_title.setText(textViews[(Integer) view.getTag()].getText());
    }

    //点击2次返回，退出程序
    @Override
    public void onBackPressed() {
        if (!isExit){
            isExit = true;
            activityUtils.showToast("再摁一次退出程序");
            //两秒内再次点击返回则退出
            viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            },2000);
        }else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
