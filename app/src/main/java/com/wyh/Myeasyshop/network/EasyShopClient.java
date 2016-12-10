package com.wyh.Myeasyshop.network;

import com.google.gson.Gson;
import com.wyh.Myeasyshop.model.CachePreferences;
import com.wyh.Myeasyshop.model.GoodsUpLoad;
import com.wyh.Myeasyshop.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public class EasyShopClient {

    private static EasyShopClient easyShopClient;

    private OkHttpClient okHttpClient;

    private Gson gson;

    private EasyShopClient() {
        //日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient.Builder()
                //日志拦截器
                .addInterceptor(interceptor)
                .build();

        gson = new Gson();
    }

    public static EasyShopClient getInstance() {
        if (easyShopClient == null) {
            easyShopClient = new EasyShopClient();
        }
        return easyShopClient;
    }

    /**
     * 注册
     * <p>
     * post
     *
     * @param username 用户名
     * @param password 密码
     */
    public Call register(String username, String password) {
        //表单形式构建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        //构建请求
        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.REGISTER)
                .post(requestBody) //ctrl+p查看参数
                .build();

        return okHttpClient.newCall(request);
    }

    /**
     * 登录
     * <p>
     * post
     *
     * @param username 用户名
     * @param password 密码
     */
    public Call login(String username, String password) {
        //表单形式构建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        //构建请求
        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.LOGIN)
                .post(requestBody) //ctrl+p查看参数
                .build();

        return okHttpClient.newCall(request);
    }

    /**
     * 更换昵称
     * <p>
     * post
     *
     * @param user 用户实体类
     */
    public Call uploadUser(User user) {
        //多部分形式构建请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user", gson.toJson(user))
                .build();

        //构建请求
        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.UPDATA)
                .post(requestBody) //ctrl+p查看参数
                .build();

        return okHttpClient.newCall(request);
    }

    /**
     * 更换头像
     * <p>
     * post
     *
     * @param file 要更新的头像文件
     */
    public Call uploadAvatar(File file) {
        //多部分形式构建请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user", gson.toJson(CachePreferences.getUser()))
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(MediaType.parse("image/png"), file))
                .build();

        //构建请求
        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.UPDATA)
                .post(requestBody) //ctrl+p查看参数
                .build();

        return okHttpClient.newCall(request);
    }

    /**
     * 获取所有商品
     * <p>
     * post
     *
     * @param pageNo 商品分页 string
     * @param type   商品类型
     */
    public Call getGoods(int pageNo, String type) {
        //多部分形式构建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("pageNo", String.valueOf(pageNo))
                .add("type", type)
                .build();

        //构建请求
        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.GETGOODS)
                .post(requestBody) //ctrl+p查看参数
                .build();

        return okHttpClient.newCall(request);
    }

    /**
     * 获取商品详情
     *
     * @param goodsUuid 商品表中的商品主键
     */
    public Call getGoodsData(String goodsUuid) {
        RequestBody requestBody = new FormBody.Builder()
                .add("uuid", goodsUuid)
                .build();
        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.DETAIL)
                .post(requestBody)
                .build();

        return okHttpClient.newCall(request);
    }

    /**
     * 删除商品
     *
     * @param goodsUuid 商品表中的商品主键
     */
    public Call deleteGoods(String goodsUuid) {
        RequestBody requestBody = new FormBody.Builder()
                .add("uuid", goodsUuid)
                .build();
        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.DELETE)
                .post(requestBody)
                .build();

        return okHttpClient.newCall(request);
    }

    /**
     * 获取个人商品数据
     * 还是用获取商品的接口(参数不同)
     *
     * @param pageNo 商品分页
     * @param type   商品类型
     * @param master 商品发布者
     */
    public Call getPersonData(int pageNo, String type, String master) {
        RequestBody requestBody = new FormBody.Builder()
                .add("pageNo", String.valueOf(pageNo))
                .add("type", type)
                .add("master", master)
                .build();
        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.GETGOODS)
                .post(requestBody)
                .build();
        return okHttpClient.newCall(request);
    }


    /**
     * 商品上传
     *
     * @param goodsUpLoad 商品上传时对应的实体类
     * @param files       商品图片
     */
    public Call upload(GoodsUpLoad goodsUpLoad, ArrayList<File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("good", gson.toJson(goodsUpLoad));
        //将所有图片文件添加进来
        for (File file : files) {
            builder.addFormDataPart("image", file.getName(),
                    RequestBody.create(MediaType.parse("image/png"), file));
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.UPLOADGOODS)
                .post(requestBody)
                .build();
        return okHttpClient.newCall(request);
    }

    /**
     * 获取好友列表
     *
     * @param ids 环信id数组
     */
    public Call getUsers(List<String> ids) {
        String names = ids.toString();
        //清除list转换后的string中空格
        names = names.replace(" ","");

        RequestBody requestBody = new FormBody.Builder()
                .add("name",names)
                .build();

        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.GET_NAMES)
                .post(requestBody)
                .build();
        return okHttpClient.newCall(request);
    }

    /**
     * 查找好友
     *
     * @param nickname 用户昵称
     */
    public Call getSearchUser(String nickname) {
        RequestBody requestBody = new FormBody.Builder()
                .add("nickname",nickname)
                .build();

        Request request = new Request.Builder()
                .url(EasyShopApi.BASE_URL + EasyShopApi.GET_USER)
                .post(requestBody)
                .build();
        return okHttpClient.newCall(request);
    }
}
