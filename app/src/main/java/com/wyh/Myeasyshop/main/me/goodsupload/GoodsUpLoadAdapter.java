package com.wyh.Myeasyshop.main.me.goodsupload;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.fuicuiedu.idedemo.Myeasyshop.R;
import com.wyh.Myeasyshop.model.ImageItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public class GoodsUpLoadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ImageItem> list = new ArrayList<>();
    private LayoutInflater inflater;

    //alt + insert--->const。。。
    public GoodsUpLoadAdapter(Context context,ArrayList<ImageItem> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    //##########################   逻辑：模式的选择    #####################
    //表示编辑时的模式，1为普通，2为可选
    public static final int MODE_NORMAL = 1;
    public static final int MODE_MULTI_SELECT = 2;
    //代表图片编辑模式
    public int mode;

    //用枚举，表示item类型，有图或者无图（待添加的加号）
    public enum ITEM_TYPE {
        ITEM_NORMAL, ITEM_ADD
    }

    //模式设置
    public void changeMode(int mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    //获取当前模式
    public int getMode() {
        return mode;
    }
    //##########################   逻辑：模式的选择    #####################

    //添加图片
    public void add(ImageItem photo){
        list.add(photo);
    }

    public int getSize(){
        return list.size();
    }

    public ArrayList<ImageItem> getList(){
        return  list;
    }

    //刷新数据
    public void notifyData(){
        notifyDataSetChanged();
    }

    public void notifyDataSet(){
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //判断当前显示的item类型，有图或者待添加，从而选择不同viewholder，不同的布局
        if (viewType == ITEM_TYPE.ITEM_NORMAL.ordinal()){
            //有图的viewholder
            return new ItemSelectViewHolder(inflater.inflate(R.layout.layout_item_recyclerview,parent,false));
        }else {
            //没图，显示加号的viewholder
            return new ItemAddViewHolder(inflater.inflate(R.layout.layout_item_recyclerviewlast,parent,false));
        }
    }

    //获取item类型方法中，去判断item类型，从而加载不同viewholder
    @Override
    public int getItemViewType(int position) {
        //当position与图片数量相同时，则为加号的布局
        if (position == list.size()) return ITEM_TYPE.ITEM_ADD.ordinal();
        return ITEM_TYPE.ITEM_NORMAL.ordinal();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //判断当前的vh是不是ItemSelectViewHolder的实例
        if (holder instanceof ItemSelectViewHolder){
            ImageItem photo = list.get(position);
            final ItemSelectViewHolder item_select = (ItemSelectViewHolder) holder;
            item_select.photo = photo;
            //判断模式
            if (mode == MODE_MULTI_SELECT){
                //可选框可见
                item_select.checkBox.setVisibility(View.VISIBLE);
                //可选款设置监听
                item_select.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //imageItem中已选择属性改变
                        list.get(position).setIsCheck(isChecked);
                    }
                });
                //勾选框改变（根据imageitem的已选择属性）
                item_select.checkBox.setChecked(photo.isCheck());
            }else if (mode == MODE_NORMAL){
                //隐藏可选框
                item_select.checkBox.setVisibility(View.GONE);
            }
            //图片设置
            item_select.ivPhoto.setImageBitmap(photo.getBitmap());
            //图片长按监听
            item_select.ivPhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //模式改为可以勾选
                    mode = MODE_MULTI_SELECT;
                    //更新
                    notifyDataSetChanged();
                    if (mListner != null){
                        mListner.onLongClicked();
                    }
                    return false;
                }
            });
            //图片单击监听
            item_select.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListner != null){
                        mListner.onPhotoClicked(item_select.photo,item_select.ivPhoto);
                    }
                }
            });
        }
        //判断当前vh是不是ItemAddViewHolder的实例
        else if (holder instanceof ItemAddViewHolder){
            ItemAddViewHolder item_add = (ItemAddViewHolder) holder;
            //最多加八张图，判断
            if (position == 8){
                item_add.ib_add.setVisibility(View.GONE);
            }else{
                item_add.ib_add.setVisibility(View.VISIBLE);
            }
            //点击添加的监听
            item_add.ib_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListner != null){
                        mListner.onAddClicked();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //最多八张图
        return Math.min(list.size() + 1,8);
    }

    //图片布局
    public static class ItemSelectViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        @BindView(R.id.cb_check_photo)
        CheckBox checkBox;
        ImageItem photo;

        public ItemSelectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    //添加按钮布局
    public static class ItemAddViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.ib_recycle_add)
        ImageButton ib_add;

        public ItemAddViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //item点击事件
    public interface OnItemClickedListener{
        /**
         * 单击图片的监听事件
         *
         * @param photo
         * @param imageView 点击图片对应IamgeView
         * */
        void onPhotoClicked(ImageItem photo,ImageView imageView);

        //点击添加按钮的监听事件
        void onAddClicked();

        //长按图片的监听事件
        void onLongClicked();
    }

    private OnItemClickedListener mListner;

    //对外公开设置监听的方法
    public void setListener(OnItemClickedListener mListner){
        this.mListner = mListner;
    }
}
