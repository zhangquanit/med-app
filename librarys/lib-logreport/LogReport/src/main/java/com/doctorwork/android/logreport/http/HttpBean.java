package com.doctorwork.android.logreport.http;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author : HuBoChao
 * @date : 2020/11/30
 * @desc :
 */
public class HttpBean implements IHttpBean {

    private String url;
    private byte[] data;
    private String method;
    private CallBackListener callBackListener;
    private HttpURLConnection httpURLConnection;

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setListener(CallBackListener callBackListener) {
        this.callBackListener = callBackListener;
    }

    @Override
    public CallBackListener getListener() {
        return callBackListener;
    }

    @Override
    public void execute() {
        URL url = null;
        try {
            url = new URL(this.url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            //连接超时时间
            httpURLConnection.setConnectTimeout(HttpTool.getInstance().getConnectTimeout());
            //不使用缓存
            httpURLConnection.setUseCaches(HttpTool.getInstance().isUseCaches());
            //响应超时的时间
            httpURLConnection.setReadTimeout(HttpTool.getInstance().getReadTimeout());
            //设置这个连接对否可以写入数据;
            httpURLConnection.setDoInput(true);
            //设置这个连接对否可以输出数据;
            httpURLConnection.setDoOutput(true);
            //设置这个请求的方法
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.connect();

            OutputStream out = httpURLConnection.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);
            bos.write(data);
            bos.flush();
            out.close();
            bos.close();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = httpURLConnection.getInputStream();
                callBackListener.onSuccess(in);
            } else {
                throw new RuntimeException("请求失败："+url.toString() +"---"+ httpURLConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("请求失败："+e.getMessage());
        } finally {
            httpURLConnection.disconnect();
        }
    }
}
