package com.medlinker.network.retrofit.logreport;

import android.text.TextUtils;

import androidx.annotation.Keep;

import com.doctorwork.android.logreport.LogReport;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
@Keep
public class LogPerformanceInterceptor implements Interceptor {

    private static final String TAG = "LogPerformanceInterceptor";
    private static final String GZIP_ENCODING = "gzip";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        LogNetworkBean networkBean = new LogNetworkBean();
        networkBean.setCreateTime(System.currentTimeMillis());
        String url = request.url().toString();
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        networkBean.setUrl(url);
        networkBean.setMethod(request.method());
        networkBean.setRequestParam(request.url().query());

        Response response;
        // 发送请求，获得回包
        response = chain.proceed(request);

        networkBean.setCostTime(System.currentTimeMillis() - networkBean.getCreateTime());
        // 展示回包信息
        // body信息题
        ResponseBody body = response.body();
        InputStream responseStream = null;
        if (body != null) {
            responseStream = body.byteStream();
        }
        byte[] b = input2byte(responseStream);
        if (LogReport.getInstance().isReportNetWorkPerformance()) {
            if (LogReport.getInstance().getNetworkElapsedTimeExceed() == 0 ||
                    (LogReport.getInstance().getNetworkElapsedTimeExceed() > 0 && LogReport.getInstance().getNetworkElapsedTimeExceed() <= networkBean.getCostTime())) {
                networkBean.setSize(parseBodySize(new ByteArrayInputStream(b), response.header("Content-Encoding")));
                String traceId = response.header("x-trace-id");
                if (TextUtils.isEmpty(traceId)) {
                    traceId = response.header("X-Trace-Id");
                }
                if (!TextUtils.isEmpty(traceId)) {
                    networkBean.setTraceId(traceId);
                }
                networkBean.setStatus(response.code());
                reportPerformance(networkBean);
            }
        }
        if (responseStream != null) {
            response = response.newBuilder()
                    .body(new ForwardingResponseBody(body, new ByteArrayInputStream(b)))
                    .build();
        }
        return response;
    }

    private static class ForwardingResponseBody extends ResponseBody {
        private final ResponseBody mBody;
        private final BufferedSource mInterceptedSource;

        public ForwardingResponseBody(ResponseBody body, InputStream interceptedStream) {
            mBody = body;
            mInterceptedSource = Okio.buffer(Okio.source(interceptedStream));
        }

        @Override
        public MediaType contentType() {
            return mBody.contentType();
        }

        @Override
        public long contentLength() {
            return mBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            return mInterceptedSource;
        }
    }

    private byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 1024)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }

    private int parseBodySize(InputStream inputStream, String contentEncoding) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedReader bufferedReader = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();
            InputStream newStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            if (GZIP_ENCODING.equals(contentEncoding)) {
                GZIPInputStream gzipInputStream = new GZIPInputStream(newStream);
                bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(newStream));
            }
            StringBuilder bodyBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                bodyBuilder.append(line);
            }
            String body = bodyBuilder.toString();
            byteArrayOutputStream.close();
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            return body.getBytes().length;
        } catch (IOException e) {
        }
        return 0;
    }

    private void reportPerformance(LogNetworkBean networkBean) {
        if (networkBean != null) {
            LogReport.getInstance().reportHttpPerformanceEvent(networkBean.getUrl(), networkBean.getRequestParam(), networkBean.getMethod(), networkBean.getStatus(),
                    networkBean.getSize(), networkBean.getTraceId(), (int) networkBean.getCostTime());
        }
    }
}
