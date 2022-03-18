package com.medlinker.debugtools.fun.capture;

import android.text.TextUtils;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.util.DokitUtil;
import com.medlinker.debugtools.config.DTConfig;
import com.medlinker.debugtools.entity.DTNetworkCaptureEntity;
import com.medlinker.debugtools.storage.PreferencesKey;
import com.medlinker.debugtools.storage.PreferencesManager;

import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
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
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 3:43 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTNetworkCaptureInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (DTConfig.instance().getNetworkCaptureConfig().isNotCapture(request.url().host())){
            return chain.proceed(request);
        }

        boolean b = PreferencesManager.getBoolean(DoraemonKit.APPLICATION, PreferencesKey.MED_API_CAPTURE, true);
        if (!b) {
            return chain.proceed(request);
        }

        RequestBody requestBody = request.body();
        HttpUrl originUrl = request.url();

        if (!MedApiCaptureManager.instance().isCapture(originUrl.host())){
            return chain.proceed(request);
        }

        boolean hasRequestBody = requestBody != null;

        String path = URLDecoder.decode(originUrl.encodedPath(), "utf-8");
        DTNetworkCaptureEntity entity = new DTNetworkCaptureEntity();
        entity.requestUrl = originUrl.toString();
        entity.time = System.currentTimeMillis();
        entity.path = path;
        String method = request.method();
        entity.method = method;
        Connection connection = chain.connection();
        String requestStartMessage = ""
                + method
                + ' ' + originUrl
                + (connection != null ? " " + connection.protocol() : "");

        if (hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)\n";
        }
        if (connection != null) {
            entity.protocol = connection.protocol().toString();
        }
        entity.requestStartMessage = requestStartMessage;

        String queries = originUrl.query();
        entity.queries = transformQuery(queries);
        entity.requestBody = transformRequestBody(request.body());
        entity.headers = request.headers().toString();
        // 开始封装响应数据
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e;
        }

        ResponseBody responseBody = response.body();
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";

        entity.responseCode = "" + response.code();
        entity.responseMsg = response.message().isEmpty() ? ""
                : ' ' + response.message() + " (" + tookMs + "ms" + (", " + bodySize + " body")
                        + ')';
        entity.responseUrl = response.request().url().toString();

        Headers headers = response.headers();
        StringBuffer headerBuffer = new StringBuffer();
        for (int i = 0, count = headers.size(); i < count; i++) {
            headerBuffer.append(buildHtmlStr(headers.name(i) + "：", headers.value(i)));
        }
        entity.responseHeaders = headerBuffer.toString();

        if (!HttpHeaders.hasBody(response)) {
            entity.responseBody = "END HTTP";
        } else if (bodyHasUnknownEncoding(response.headers())) {
            entity.responseBody = "END HTTP (encoded body omitted)";
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();

            Long gzippedLength = null;
            if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
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
                entity.responseBody = "END HTTP (binary " + buffer.size() + "-byte body omitted)";
                return response;
            }

            if (contentLength != 0) {
                entity.responseBody = buffer.clone().readString(charset);
            }

            if (gzippedLength != null) {
                entity.responseBody =
                        entity.responseBody + "   END HTTP (" + buffer.size() + "-byte, "
                                + gzippedLength + "-gzipped-byte body)";
            } else {
                entity.responseBody =
                        entity.responseBody + "   END HTTP (" + buffer.size() + "-byte body)";
            }
        }
        MedApiCaptureManager.instance().addData(entity);
        return response;
    }

    private String transformQuery(String query) {
        String json = "";
        if (TextUtils.isEmpty(query)) {
            return json;
        }

        try {
            //query 类似 ccc=ccc&ddd=ddd
            json = DokitUtil.param2Json(query);
            //测试是否是json字符串
            new JSONObject(json);
        } catch (Exception e) {
            json = DokitDbManager.IS_NOT_NORMAL_QUERY_PARAMS;
        }

        return json;
    }

    /**
     * 将request body 转化成json字符串
     *
     * @return
     */
    private String transformRequestBody(RequestBody requestBody) {
        //form :"application/x-www-form-urlencoded"
        //json :"application/json;"
        String json = "";
        if (requestBody == null || requestBody.contentType() == null) {
            return json;
        }

        try {
            String strBody = DokitUtil.requestBodyToString(requestBody);
            if (TextUtils.isEmpty(strBody)) {
                return "";
            }

            if (requestBody.contentType().toString().toLowerCase().contains(DokitDbManager.MEDIA_TYPE_FORM)) {
                String form = DokitUtil.requestBodyToString(requestBody);
                //类似 ccc=ccc&ddd=ddd
                json = DokitUtil.param2Json(form);
                //测试是否是json字符串
                new JSONObject(json);
            } else if (requestBody.contentType().toString().toLowerCase().contains(DokitDbManager.MEDIA_TYPE_JSON)) {
                json = DokitUtil.requestBodyToString(requestBody);
                //测试是否是json字符串
                new JSONObject(json);
            } else if (requestBody instanceof FormBody){
                StringBuilder sb = new StringBuilder();
                FormBody body = (FormBody) requestBody;
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                json = sb.toString();
            }else {
                json = DokitDbManager.IS_NOT_NORMAL_BODY_PARAMS;
            }
        } catch (Exception e) {
            json = "";
        }

        return json;
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }

    static boolean isPlaintext(Buffer buffer) {
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

    private String buildHtmlStr(String title, String content) {
        return "<b><font color=\"blue\">" + title + "</font></b>" + content + "<br>";
    }
}
