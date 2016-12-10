package com.wyh.Myeasyshop.main.me.goodsupload;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuicuiedu.idedemo.Myeasyshop.R;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.wyh.Myeasyshop.commons.ActivityUtils;
import com.wyh.Myeasyshop.commons.ImageUtils;
import com.wyh.Myeasyshop.commons.MyFileUtils;
import com.wyh.Myeasyshop.components.PicWindow;
import com.wyh.Myeasyshop.components.ProgressDialogFragment;
import com.wyh.Myeasyshop.model.CachePreferences;
import com.wyh.Myeasyshop.model.GoodsUpLoad;
import com.wyh.Myeasyshop.model.ImageItem;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoodsUpLoadActivity extends MvpActivity<GoodsUpLoadView, GoodsUpLoadPresenter> implements com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.et_goods_name)
    EditText et_goods_name;
    @BindView(R.id.et_goods_price)
    EditText et_goods_price;
    @BindView(R.id.et_goods_describe)
    EditText et_goods_describe;
    @BindView(R.id.tv_goods_type)
    TextView tv_goods_type;
    @BindView(R.id.tv_goods_delete)
    TextView tv_goods_delete;
    @BindView(R.id.btn_goods_load)
    Button btn_goods_load;

    private final String[] goods_type = {"家用", "电子", "服饰", "玩具", "图书", "礼品", "其它"};
    /*商品种类为自定义*/
    private final String[] goods_type_num = {"household", "electron", "dress", "toy", "book", "gift", "other"};

    private ActivityUtils activityUtils;
    private String str_goods_name;
    private String str_goods_price;
    private String str_goods_type = goods_type_num[0];
    private String str_goods_describe;

    //模式：普通1
    public static final int MODE_DONE = 1;
    //模式：删除2
    public static final int MODE_DELETE = 2;
    private int title_mode = MODE_DONE;
    private ArrayList<ImageItem> list = new ArrayList<>();
    private com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadAdapter adapter;
    private PicWindow picWindow;
    private ProgressDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_up_load);
        ButterKnife.bind(this);
        activityUtils = new ActivityUtils(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        viewContent();
    }

    @NonNull
    @Override
    public com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadPresenter createPresenter() {
        return new com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadPresenter();
    }

    //picWindow和recyclerView初始化
    private void initView() {
        //picWindow
        picWindow = new PicWindow(this, listener);
        //recyclerview
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        //设置默认动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置固定大小
        recyclerView.setHasFixedSize(true);

        //获取缓存文件夹中文件
        list = getFilePhoto();
        adapter = new com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setListener(itemClickedListener);
    }

    //获取商品名称价格描述信息并监听
    private void viewContent() {
        et_goods_name.addTextChangedListener(textWatcher);
        et_goods_price.addTextChangedListener(textWatcher);
        et_goods_describe.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            str_goods_name = et_goods_name.getText().toString();
            str_goods_price = et_goods_price.getText().toString();
            str_goods_describe = et_goods_describe.getText().toString();
            //判断上传按钮是否可点击
            boolean can_save = !(TextUtils.isEmpty(str_goods_name) || TextUtils.isEmpty(str_goods_price)
                    || TextUtils.isEmpty(str_goods_describe));
            btn_goods_load.setEnabled(can_save);

        }
    };

    //图片选择弹窗内的监听事件
    private PicWindow.Listener listener = new PicWindow.Listener() {
        @Override
        public void toGallery() {
            //相册
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            Intent intent = CropHelper.buildCropFromGalleryIntent(cropHandler.getCropParams());
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        }

        @Override
        public void toCamera() {
            //相机
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            Intent intent = CropHelper.buildCaptureIntent(cropHandler.getCropParams().uri);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
        }
    };

    //图片裁剪
    private CropHandler cropHandler = new CropHandler() {
        @Override
        public void onPhotoCropped(Uri uri) {
            //需求：裁剪完成后把bitmap保存到SD卡中，并且显示出来
            //文件名，就用系统的当前时间，不重复
            String fileName = String.valueOf(System.currentTimeMillis());
            //通过IamgeUtiles工具类，拿到bitmap
            Bitmap bitmap = ImageUtils.readDownsampledImage(uri.getPath(), 1080, 1920);
            //将小图保存到SD卡中
            MyFileUtils.saveBitmap(bitmap, fileName);
            //将item添加到适配器中
            ImageItem take_photo = new ImageItem();
            take_photo.setImagePath(fileName + ".JPEG");
            take_photo.setBitmap(bitmap);
            adapter.add(take_photo);
            adapter.notifyDataSet();
        }

        @Override
        public void onCropCancel() {
        }

        @Override
        public void onCropFailed(String message) {
        }

        @Override
        public CropParams getCropParams() {
            CropParams cropParams = new CropParams();
            cropParams.aspectX = 400;
            cropParams.aspectY = 400;
            return cropParams;
        }

        @Override
        public Activity getContext() {
            return GoodsUpLoadActivity.this;
        }
    };

    //当activtiy拿到返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //crophelper帮助我们去处理结果
        CropHelper.handleResult(cropHandler, requestCode, resultCode, data);
    }

    //获取缓存文件夹中的文件
    private ArrayList<ImageItem> getFilePhoto() {
        ArrayList<ImageItem> imageItems = new ArrayList<>();
        //拿到所有图片文件
        File[] files = new File(MyFileUtils.SD_PATH).listFiles();
        if (files != null) {
            for (File file : files) {
                //解码file拿到bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(MyFileUtils.SD_PATH + file.getName());
                ImageItem item = new ImageItem();
                item.setImagePath(file.getName());
                item.setBitmap(bitmap);
                imageItems.add(item);
            }
        }
        return imageItems;
    }

    //图片点击事件监听
    private com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadAdapter.OnItemClickedListener itemClickedListener = new com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadAdapter.OnItemClickedListener() {
        @Override
        public void onPhotoClicked(ImageItem photo, ImageView imageView) {
            //跳转到详情页
            Intent intent = new Intent(GoodsUpLoadActivity.this, com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadImageShowActivity.class);
            intent.putExtra("images", photo.getBitmap());
            intent.putExtra("width",imageView.getWidth());
            intent.putExtra("height",imageView.getHeight());
            startActivity(intent);
        }

        @Override
        public void onAddClicked() {
            //展示图片来源选择的pop
            if (picWindow != null && picWindow.isShowing()) {
                picWindow.dismiss();
            } else if (picWindow != null) {
                picWindow.show();
            }
        }

        @Override
        public void onLongClicked() {
            //模式改为可删除模式
            title_mode = MODE_DELETE;
            //删除的tv可见
            tv_goods_delete.setVisibility(View.VISIBLE);
        }
    };

    //toolbar返回要实现的方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    //重写返回方法，实现点击改变模式
    @Override
    public void onBackPressed() {
        if (title_mode == MODE_DONE) {
            //删除缓存
            deleteCache();
            finish();
        } else if (title_mode == MODE_DELETE) {
            changModeActivity();
        }
    }

    //删除缓存文件夹中的文件
    private void deleteCache() {
        for (int i = 0; i < adapter.getList().size(); i++) {
            MyFileUtils.delFile(adapter.getList().get(i).getImagePath());
        }
    }

    //按返回键改变布局模式
    private void changModeActivity() {
        //判断，根据adapter判断当前模式是否是可选删除模式
        if (adapter.getMode() == com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadAdapter.MODE_MULTI_SELECT) {
            //删除tv不可见
            tv_goods_delete.setVisibility(View.GONE);
            //模式改变
            title_mode = MODE_DONE;
            //adapter模式改变
            adapter.changeMode(com.wyh.Myeasyshop.main.me.goodsupload.GoodsUpLoadAdapter.MODE_NORMAL);
            for (int i = 0; i < adapter.getList().size(); i++) {
                adapter.getList().get(i).setIsCheck(false);
            }
        }
    }

    //点击删除，类型选择，点击上传监听
    @OnClick({R.id.tv_goods_delete, R.id.btn_goods_type, R.id.btn_goods_load})
    public void onClick(View view) {
        switch (view.getId()) {
            //商品类型选择
            case R.id.btn_goods_type:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("商品类型");
                builder.setItems(goods_type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_goods_type.setText(goods_type[which]);
                        str_goods_type = goods_type_num[which];
                    }
                });
                builder.create().show();
                break;
            //点击删除
            case R.id.tv_goods_delete:
                ArrayList<ImageItem> del_list = adapter.getList();
                int num = del_list.size();
                for (int i = num - 1; i >= 0; i--) {
                    if (del_list.get(i).isCheck()){
                        //删除缓存文件夹中文件
                        MyFileUtils.delFile(del_list.get(i).getImagePath());
                        del_list.remove(i);
                    }
                }
                this.list = del_list;
                adapter.notifyData();
                changModeActivity();
                title_mode = MODE_DONE;
                break;
            //点击上传监听
            case R.id.btn_goods_load:
                if (adapter.getSize() == 0){
                    activityUtils.showToast("最少有一张商品图片！");
                    return;
                }
                presenter.upLoad(setGoodsInfo(),list);
                break;
        }
    }

    //对商品信息初始化
    private GoodsUpLoad setGoodsInfo(){
        GoodsUpLoad goodsLoad = new GoodsUpLoad();
        goodsLoad.setName(str_goods_name);
        goodsLoad.setPrice(str_goods_price);
        goodsLoad.setDescribe(str_goods_describe);
        goodsLoad.setType(str_goods_type);
        goodsLoad.setMaster(CachePreferences.getUser().getName());
        return goodsLoad;
    }


    @Override
    public void showPrb() {
        if (dialogFragment == null) dialogFragment = new ProgressDialogFragment();
        if (dialogFragment.isVisible()) return;
        dialogFragment.show(getSupportFragmentManager(),"fragment_dialog");
    }

    @Override
    public void hidePrb() {
        dialogFragment.dismiss();
    }

    @Override
    public void upLoadSuccess() {
        finish();
    }

    @Override
    public void showMsg(String msg) {
        activityUtils.showToast(msg);
    }
}
