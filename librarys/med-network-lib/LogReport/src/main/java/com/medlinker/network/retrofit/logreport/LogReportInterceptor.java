package com.medlinker.network.retrofit.logreport;

import android.text.TextUtils;

import androidx.annotation.Keep;

import com.doctorwork.android.logreport.LogReport;
import com.medlinker.network.retrofit.BusinessCodeEntity;
import com.medlinker.network.retrofit.INetworkConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

/**
 * @author hmy
 * @time 2020/12/28 10:15
 */
@Keep
public class LogReportInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private INetworkConfig mNetworkConfig;

    public LogReportInterceptor(INetworkConfig networkConfig) {
        mNetworkConfig = networkConfig;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String requestParam = transformRequestBody(chain);

        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        long contentLength = 0;
        if (responseBody != null) {
            contentLength = responseBody.contentLength();
        }

        String responseBodyStr;
        String responseBodyValidStr = "";
        if (!HttpHeaders.hasBody(response)) {
            responseBodyStr = "END HTTP(have not body)";
        } else if (bodyHasUnknownEncoding(response.headers())) {
            responseBodyStr = "END HTTP (encoded body omitted)";
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Long gzippedLength = null;
            if ("gzip".equalsIgnoreCase(response.headers().get("Content-Encoding"))) {
                gzippedLength = buffer.size();
                GzipSource gzippedResponseBody = null;
                try {
                    gzippedResponseBody = new GzipSource(buffer.clone());
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                } finally {
                    if (gzippedResponseBody != null) {
                        gzippedResponseBody.close();
                    }
                }
            }

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (!isPlaintext(buffer)) {
                responseBodyStr = "END HTTP (binary " + buffer.size() + "-byte body omitted)";
            } else {
                if (contentLength != 0 && charset != null) {
                    responseBodyValidStr = buffer.clone().readString(charset);
                }

                if (gzippedLength != null) {
                    responseBodyStr = "END HTTP (" + buffer.size() + "-byte, "
                            + gzippedLength + "-gzipped-byte body)";
                } else {
                    responseBodyStr = "END HTTP (" + buffer.size() + "-byte body)";
                }
            }
        }

        int code = response.code();
        if (code != 200) {
            LogReport.getInstance().reportHttpError(getUrlPath(request),
                    requestParam,
                    request.method(),
                    code,
                    getErrMsg(!TextUtils.isEmpty(responseBodyValidStr) ? responseBodyValidStr : responseBodyStr),
                    mNetworkConfig.getPage());
        } else if (!TextUtils.isEmpty(responseBodyValidStr)) {
            try {
                JSONObject json = new JSONObject(responseBodyValidStr);
                String responseJson = json.toString();
                BusinessCodeEntity codeEntity = mNetworkConfig.getBusinessCode(responseJson);
                if (!codeEntity.isSuccess()) {
                    LogReport.getInstance().reportBusinessError(getUrlPath(request),
                            requestParam,
                            request.method(),
                            codeEntity.getCode(),
                            getErrMsg(responseBodyValidStr),
                            mNetworkConfig.getPage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    private String getUrlPath(Request request) {
        return request.url().toString();
    }

    private String getErrMsg(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.optString(mNetworkConfig.getErrMsgKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    /**
     * 将request body 转化成json字符串
     *
     * @return
     */
    private String transformRequestBody(Chain chain) {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        if (requestBody == null || requestBody.contentType() == null) {
            return "";
        }
        try {
            String strBody = requestBodyToString(requestBody);
            if (TextUtils.isEmpty(strBody)) {
                return "";
            } else {
                return strBody;
            }
        } catch (Exception ignored) {
        }

        return "";
    }


    private String requestBodyToString(RequestBody requestBody) {
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }

    private boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }
}
