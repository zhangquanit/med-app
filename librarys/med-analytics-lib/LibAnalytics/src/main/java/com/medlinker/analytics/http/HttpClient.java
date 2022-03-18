package com.medlinker.analytics.http;

import android.text.TextUtils;
import android.util.Log;

import com.medlinker.analytics.http.callbacks.HttpErrorCode;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class HttpClient {
    public int CONNECT_TIME_OUT = 8 * 1000;
    private static final String TAG = "HttpClient";

    private HttpClient() {
    }

    private static class SingletonHolder {

        static HttpClient sDefaultHttpClient = new HttpClient();
    }

    private static class SingletonHolderExecutorService {

        static ExecutorService executorService = Executors.newCachedThreadPool();
    }

    public static HttpClient getDefault() {
        return SingletonHolder.sDefaultHttpClient;
    }

    public void request(final Request request, final ResultCallback callback) {
        executorService()
                .execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                doRequest(
                                        request,
                                        new ResultCallback() {
                                            @Override
                                            public void onResponse(String result) {
                                                if (callback != null) {
                                                    callback.onResponse(result);
                                                }
                                            }

                                            @Override
                                            public void onFailure(String errMsg) {
                                                if (callback != null) {
                                                    callback.onFailure(errMsg);
                                                }
                                            }
                                        });
                            }
                        });
    }

    private void doRequest(Request request, ResultCallback callback) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder result = new StringBuilder();
        try {
            urlConnection = createConnection(request);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                InputStream is = urlConnection.getErrorStream();
                String errorMsg = "";
                if (is != null) {
                    byte[] bytes = new byte[is.available()];
                    is.read(bytes);
                    errorMsg = new String(bytes, "utf-8");
                    closeStream(is);
                    Log.v(TAG, "doRequest Failed :" + errorMsg);
                }
                urlConnection.disconnect();
                callback.onFailure("http response code = " + responseCode);
                return;
            }

            inputStream = urlConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            callback.onResponse(result.toString());
        } catch (SocketTimeoutException timeout) {
            Log.v(TAG, "SocketTimeoutException :" + timeout);
            callback.onFailure(HttpErrorCode.HTTP_TIMEOUT_ERROR.getReason());
        } catch (EOFException e) {
            Log.v(TAG, "EOFException :" + e);
            callback.onFailure(HttpErrorCode.HTTP_RESPONSE_ERROR.getReason());
        } catch (Exception e) {
            Log.v(TAG, "exception :" + e);
            callback.onFailure(HttpErrorCode.HTTP_RESPONSE_ERROR.getReason());
        } finally {
            closeStream(inputStream);
            closeStream(inputStreamReader);
            closeStream(bufferedReader);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private static ExecutorService executorService() {
        return SingletonHolderExecutorService.executorService;
    }

    public interface ResultCallback {
        void onResponse(String result);

        void onFailure(String errorMsg);
    }

    private void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "closeStream exception ", e);
        }
    }

    private HttpURLConnection createConnection(Request request) throws IOException {
        URL url;
        HttpURLConnection conn;
        Log.i(TAG, "request url : " + request.url());
        //url 是IP地址情况下，myHost解析出来的是ip
        final String host = getNavHost(request.url());
        if (request.url().toLowerCase().startsWith("https")) {
            url = new URL(request.url());
            conn = (HttpsURLConnection) url.openConnection();
            ((HttpsURLConnection) conn)
                    .setHostnameVerifier(
                            new HostnameVerifier() {
                                @Override
                                public boolean verify(String hostname, SSLSession session) {
                                    return HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
                                }
                            });
        } else {
            url = new URL(request.url());
            conn = (HttpURLConnection) url.openConnection();
        }
        conn.setRequestMethod(request.method());
        conn.setConnectTimeout(CONNECT_TIME_OUT);
        conn.setReadTimeout(CONNECT_TIME_OUT);
        conn.setUseCaches(false);

        if (!TextUtils.isEmpty(host)) {
            conn.setRequestProperty("Host", host);
        }

        conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        if (request.getHeaders() != null && request.getHeaders().getHeaders() != null) {
            Set<String> setKey = request.getHeaders().getHeaders().keySet();
            Iterator<String> iterator = setKey.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = request.getHeaders().getHeaders().get(key);
                conn.setRequestProperty(key, value);
            }
        }

        conn.setDoInput(true);
        if (TextUtils.equals(request.method(), RequestMethod.POST)) {
            conn.setDoOutput(true);
            String body = request.body();
            if (body == null) {
                throw new NullPointerException("Request.body == null");
            }
            OutputStream outputStream = conn.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.write(request.body());
            printWriter.flush();
        }
        return conn;
    }

    private static String getNavHost(String navi) {
        try {
            URL url = new URL(navi);
            String host = url.getHost();
            int port = url.getPort();
            if (port != -1 && (url.getDefaultPort() != url.getPort())) {
                host = host + ":" + port;
            }
            return host;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException ", e);
        }
        return null;
    }
}
