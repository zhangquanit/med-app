package com.doctorwork.android.logreport;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.doctorwork.android.logreport.bean.BaseBean;
import com.doctorwork.android.logreport.bean.CustomBean;
import com.doctorwork.android.logreport.bean.ErrorBean;
import com.doctorwork.android.logreport.bean.EventBean;
import com.doctorwork.android.logreport.request.CallAdapterFactory;
import com.doctorwork.android.logreport.request.HttpCallAdapterFactory;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author : HuBoChao
 * @date : 2020/11/25
 * @desc :日志上报
 */
public class LogReport {

    /**
     * 环境
     */
    public static final int URL_DEV = 0;
    public static final int URL_PROD = 1;
    public static final int URL_CUSTOM = 2;

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef(value = {URL_DEV, URL_PROD, URL_CUSTOM})
    private @interface ReportUrl {
    }

    /**
     * 请求方法
     */
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @StringDef(value = {GET, POST, PUT, DELETE})
    private @interface RequestMethod {
    }

    /**
     * 62位字符集
     */
    private static final char[] DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                    'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
                    'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final HashMap<Character, String> pMap = new HashMap<Character, String>() {
        {
            put('0', "QR");
            put('1', "xa");
            put('2', "cL");
            put('3', "pF");
            put('4', "Oe");
            put('5', "bn");
            put('6', "sM");
            put('7', "yt");
            put('8', "Uv");
            put('9', "ik");
        }
    };

    /**
     * "net"=非200状态码的网络请求
     * "business"=请求成功但业务失败
     * "img"=图片加载失败
     * "custom"=自定义错误
     */
    private static final String HTTP_ERROR = "net";
    private static final String BUSINESS_ERROR = "business";
    private static final String IMG_ERROR = "img";
    private static final String CUSTOM_ERROR = "custom";

    private String mAppId;
    private Context mContext;
    private boolean mIsReportNow;
    private boolean mReportNetWorkPerformance;
    private boolean isTraceId;
    private String traceIdHeaderName;
    /**
     * 默认网络请求耗时数据都上报，但数据量非常大
     * 增加配置，网络请求耗时超过一定值时才上报，单位ms
     * 默认1500ms,设置为0全部上报
     */
    private int networkElapsedTimeExceed;
    private int mFileLimit;
    private String mFilePath;
    private int mRetryCount;
    private String mPhoneNumber;
    private String mChannel;
    private String mUUID;
    private long mAppStartTime;
    private SharedPreferences mSp;
    private int mDigitsSize;
    private long mVersionCode;
    private String mReportUrl;
    private static LogReport logReport;
    private CallAdapterFactory mCallAdapterFactory;

    public static class LogReportBuild {
        private String mAppId;
        private Context mContext;
        private boolean mIsReportNow;
        private int mFileLimit;
        private String mFilePath;
        private int mRetryCount;
        private String mChannel;
        private int mEnv;
        private String customUrl;
        private String baseUrl;
        private boolean mReportNetWorkPerformance = true;
        private int networkElapsedTimeExceed = 1500;
        private boolean isTraceId = true;
        private String traceIdHeaderName;
        private CallAdapterFactory mCallAdapterFactory;

        private LogReportBuild(Context context, String appId, String channel, int env) {
            mAppId = appId;
            mContext = context;
            mFileLimit = 1;
            mFilePath = "logreport.txt";
            mRetryCount = 3;
            mChannel = channel;
            mEnv = env;
        }

        public LogReportBuild setRetryCount(int retryCount) {
            mRetryCount = retryCount;
            return this;
        }

        public LogReportBuild setReportNow(boolean isReportNow) {
            mIsReportNow = isReportNow;
            return this;
        }

        public LogReportBuild setFileLimit(int fileLimit) {
            mFileLimit = fileLimit;
            return this;
        }

        public LogReportBuild setFilePath(String filePath) {
            mFilePath = filePath;
            return this;
        }

        public LogReportBuild setCustomUrl(String customUrl) {
            this.customUrl = customUrl;
            return this;
        }

        public LogReportBuild setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public LogReportBuild setReportNetWorkPerformance(boolean reportNetWorkPerformance) {
            this.mReportNetWorkPerformance = reportNetWorkPerformance;
            return this;
        }

        public LogReportBuild setNetworkElapsedTimeExceed(int networkElapsedTimeExceed) {
            this.networkElapsedTimeExceed = networkElapsedTimeExceed;
            return this;
        }

        public LogReportBuild enableTraceId(boolean isTraceId) {
            this.isTraceId = isTraceId;
            return this;
        }

        public LogReportBuild setTraceIdHeaderName(String traceIdHeaderName) {
            this.traceIdHeaderName = traceIdHeaderName;
            return this;
        }

        public LogReportBuild addCallAdapterFactory(CallAdapterFactory adapterFactory) {
            this.mCallAdapterFactory = adapterFactory;
            return this;
        }

        public void build() {
            if (logReport != null) {
                logReport = null;
            }
            logReport = new LogReport(this);
        }

    }

    private LogReport(LogReportBuild build) {
        mContext = build.mContext;
        mSp = mContext.getSharedPreferences(Constant.ABOUT_LOG_REPORT_SP, Context.MODE_PRIVATE);
        mAppId = build.mAppId;
        mIsReportNow = build.mIsReportNow;
        mFileLimit = build.mFileLimit;
        mFilePath = Constant.localDirPath + File.separator + build.mFilePath;
        mRetryCount = build.mRetryCount;
        mPhoneNumber = getPhoneNumber();
        mAppStartTime = System.currentTimeMillis();
        mChannel = build.mChannel;
        mDigitsSize = DIGITS.length;
        mReportNetWorkPerformance = build.mReportNetWorkPerformance;
        networkElapsedTimeExceed = build.networkElapsedTimeExceed;
        isTraceId = build.isTraceId;
        traceIdHeaderName = build.traceIdHeaderName;
        //确定环境
        if (build.mEnv == URL_DEV) {
            mReportUrl = Constant.BASE_URL_DEV + Constant.LOG_REPORT;
        } else if (build.mEnv == URL_PROD) {
            mReportUrl = Constant.BASE_URL_PROD + Constant.LOG_REPORT;
        } else if (build.mEnv == URL_CUSTOM) {
            if (!TextUtils.isEmpty(build.customUrl)) {
                mReportUrl = build.customUrl;
            } else if (!TextUtils.isEmpty(build.baseUrl)) {
                mReportUrl = build.baseUrl + Constant.LOG_REPORT;
            }
        } else {
            mReportUrl = Constant.BASE_URL_DEV + Constant.LOG_REPORT;
        }
        //获取app版本号
        try {
            PackageInfo packageInfo = mContext.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mVersionCode = packageInfo.getLongVersionCode();
            } else {
                mVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            mVersionCode = 0;
            e.printStackTrace();
        }
        mCallAdapterFactory = build.mCallAdapterFactory;
        if (null == mCallAdapterFactory) {
            mCallAdapterFactory = HttpCallAdapterFactory.create();
        }

        //检查必须权限
//        String[] permissions = new String[]{
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//        };
//        Constant.isGranted = true;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (Environment.isExternalStorageManager()) {
//                Constant.isGranted = true;
//            } else {
//                Constant.isGranted = false;
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            for (String permission : permissions) {
//                int ps = ContextCompat.checkSelfPermission(mContext, permission);
//                if (ps != PackageManager.PERMISSION_GRANTED) {
//                    Constant.isGranted = false;
//                    break;
//                }
//            }
//        } else {
//            for (String permission : permissions) {
//                int ps = PermissionChecker.checkSelfPermission(mContext, permission);
//                if (ps != PackageManager.PERMISSION_GRANTED) {
//                    Constant.isGranted = false;
//                    break;
//                }
//            }
//        }
//        if (!Constant.isGranted) {
//            Intent intent = new Intent(mContext, PermissionsActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);
//        }
    }

    private String getPhoneNumber() {
        return mSp.getString(Constant.SP_PHONE, "");
    }

    public LogReport setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
        StringBuffer stringBuffer = new StringBuffer();
        int count = mPhoneNumber.toCharArray().length;
        for (int i = 0; i < count; i++) {
            stringBuffer.append(pMap.get(mPhoneNumber.charAt(i)));
        }
        mPhoneNumber = stringBuffer.toString().trim();
        mSp.edit().putString(Constant.SP_PHONE, phoneNumber);
        return logReport;
    }

    public static LogReportBuild init(Context context, String appId, String channel, @ReportUrl int env) {
        if (context == null) {
            throw new RuntimeException("context is null");
        }
        if (TextUtils.isEmpty(appId)) {
            throw new RuntimeException("appid不能为空");
        }
        return new LogReportBuild(context, appId, channel, env);
    }

    public static LogReport getInstance() {
        if (logReport == null) {
            throw new RuntimeException("未调用init方法");
        }
        return logReport;
    }

    public void reportHttpError(String url, Object params, @RequestMethod String method,
                                int code) {
        reportHttpError(url, params, method, code, "", "", mIsReportNow);
    }

    public void reportHttpError(String url, Object params, @RequestMethod String method,
                                int code, Object msg) {
        reportHttpError(url, params, method, code, msg, "", mIsReportNow);
    }

    public void reportHttpError(String url, Object params, @RequestMethod String method,
                                int code, Object msg, String page) {
        reportHttpError(url, params, method, code, msg, page, mIsReportNow);
    }

    /**
     * 业务方主动控制是否立即上报，第一版暂不提供
     */
    private void reportHttpError(String url, Object params, @RequestMethod String method,
                                 int code, Object msg, String page, boolean isReportNow) {
        reportData(initErrorBeans(HTTP_ERROR, url, params, method, code + "", msg, page), null);
    }

    public void reportBusinessError(String url, Object params, @RequestMethod String method,
                                    int code) {
        reportBusinessError(url, params, method, code, "", "", mIsReportNow);
    }

    public void reportBusinessError(String url, Object params, @RequestMethod String method,
                                    int code, Object msg) {
        reportBusinessError(url, params, method, code, msg, "", mIsReportNow);
    }

    public void reportBusinessError(String url, Object params, @RequestMethod String method,
                                    int code, Object msg, String page) {
        reportBusinessError(url, params, method, code, msg, page, mIsReportNow);
    }

    /**
     * 业务方主动控制是否立即上报，第一版暂不提供
     */
    private void reportBusinessError(String url, Object params, @RequestMethod String method,
                                     int code, Object msg, String page, boolean isReportNow) {
        reportData(initErrorBeans(BUSINESS_ERROR, url, params, method, code + "", msg, page), null);
    }

    public void reportImageError(String url, Object msg) {
        reportImageError(url, msg, "", mIsReportNow);
    }

    public void reportImageError(String url, Object msg, String page) {
        reportImageError(url, msg, page, mIsReportNow);
    }

    /**
     * 业务方主动控制是否立即上报，第一版暂不提供
     */
    private void reportImageError(String url, Object msg, String page, boolean isReportNow) {
        reportData(initErrorBeans(IMG_ERROR, url, null,
                null, null, msg, page), null);
    }

    public void reportCustomsError(Object msg) {
        reportCustomsError(msg, "", mIsReportNow);
    }

    public void reportCustomsError(Object msg, String page) {
        reportCustomsError(msg, page, mIsReportNow);
    }

    /**
     * 业务方主动控制是否立即上报，第一版暂不提供
     */
    private void reportCustomsError(Object msg, String page, boolean isReportNow) {
        reportData(initErrorBeans(CUSTOM_ERROR, null, null, null, null,
                msg, page), null);
    }

    public void reportPointLog(String key, Object msg) {
        reportPointLog(key, msg, mIsReportNow);
    }


    /**
     * 上报冷启动，热启动事件数据
     *
     * @param isHot    是否是热启动
     * @param page     页面
     * @param duration 耗时，单位ms
     */
    public void reportStartUpEvent(boolean isHot, String page, int duration) {
        EventBean eventBean = new EventBean();
        long currentTime = System.currentTimeMillis();
        eventBean.setDuration(duration);
        eventBean.setCreateTime(currentTime);
        eventBean.setPage(page);
        eventBean.setEvent(isHot ? EventBean.HOT_START : EventBean.COLD_START);
        LogReport.getInstance().reportPerformanceEvent(eventBean);
    }

    /**
     * 上报自定义事件。
     * reportCustomsError相关方法后端已准备废弃
     *
     * @param event 自定义事件，任意对象，由SDK统一转化为String
     */
    public void reportCustomEvent(Object event) {
        EventBean eventBean = new EventBean();
        long currentTime = System.currentTimeMillis();
        eventBean.setCreateTime(currentTime);
        String jsonStr = new Gson().toJson(event);
        eventBean.setReqParam(jsonStr);
        eventBean.setEvent(EventBean.CUSTOM_EVENT);
        LogReport.getInstance().reportPerformanceEvent(eventBean);
    }

    /**
     * 上报网络性能数据
     *
     * @param url              http请求url
     * @param requestParameter http url参数
     * @param requestMethod    http请求方法
     * @param responseCode     http response code
     * @param responseSize     http response 大小，单位字节
     * @param responseTraceId  http response header里携带的traceId
     * @param duration         耗时，单位ms
     */
    public void reportHttpPerformanceEvent(String url, String requestParameter,
                                           String requestMethod, int responseCode,
                                           int responseSize, String responseTraceId, int duration) {
        if (!mReportNetWorkPerformance) return;
        if (networkElapsedTimeExceed != 0 && duration <= networkElapsedTimeExceed) return;
        EventBean eventBean = new EventBean();
        eventBean.setEvent(EventBean.HTTP_PERF);
        eventBean.setReqUrl(url);
        eventBean.setReqMethod(requestMethod);
        eventBean.setReqParam(requestParameter);
        eventBean.setDuration(duration);
        eventBean.setCreateTime(System.currentTimeMillis());
        eventBean.setResSize(responseSize + "");
        eventBean.setResCode(responseCode);
        if (!TextUtils.isEmpty(responseTraceId) && isTraceId) {
            eventBean.setResTid(responseTraceId);
        }
        LogReport.getInstance().reportPerformanceEvent(eventBean);
    }

    /**
     * 上报性能统计数据
     */
    private void reportPerformanceEvent(EventBean eventBean) {
        BaseBean baseBean = initBaseInfo();
        ArrayList<EventBean> eventBeans = new ArrayList<>();
        eventBeans.add(eventBean);
        baseBean.setEvents(eventBeans);
        sendReportData(baseBean);
    }

    public boolean isReportNetWorkPerformance() {
        return mReportNetWorkPerformance;
    }

    public int getNetworkElapsedTimeExceed() {
        return networkElapsedTimeExceed;
    }

    public boolean isTraceId() {
        return isTraceId;
    }

    public String getTraceIdHeaderName() {
        return traceIdHeaderName;
    }

    /**
     * 业务方主动控制是否立即上报，第一版暂不提供
     */
    private void reportPointLog(String key, Object msg, boolean isReportNow) {
        ArrayList<CustomBean> customBeans = new ArrayList<>();
        CustomBean customBean = new CustomBean();
        customBean.setCreateTime(System.currentTimeMillis());
        customBean.setName(key);
        customBean.setContent(msg);
        customBeans.add(customBean);
        reportData(null, customBeans);
    }

    private ArrayList<ErrorBean> initErrorBeans(String type, String url, Object params, String method,
                                                String code, Object msg, String page) {
        ArrayList<ErrorBean> errorBeans = new ArrayList<>();
        ErrorBean errorBean = new ErrorBean();
        errorBean.setType(type);
        errorBean.setCreateTime(System.currentTimeMillis());
        errorBean.setPage(page);
        errorBean.setUrl(url);
        errorBean.setMethod(method);
        errorBean.setParams(params);
        errorBean.setCode(code);
        errorBean.setMsg(msg);
        errorBean.setPage(page);
        errorBeans.add(errorBean);
        return errorBeans;
    }

    /**
     * 统一请求发送
     *
     * @param errorBeans  错误列表
     * @param customBeans 埋点列表
     */
    private void reportData(ArrayList<ErrorBean> errorBeans, ArrayList<CustomBean> customBeans) {
        BaseBean baseBean = initBaseInfo();
        baseBean.setErrors(errorBeans);
        baseBean.setCustoms(customBeans);
        sendReportData(baseBean);
    }

    private void sendReportData(BaseBean baseBean) {
        String jsonStr = new Gson().toJson(baseBean);
        mCallAdapterFactory.call(mReportUrl, jsonStr);
    }

    private BaseBean initBaseInfo() {
        BaseBean baseBean = new BaseBean();
        baseBean.setAppId(mAppId);
        baseBean.setUuid(getUUID());
        baseBean.setChannel(mChannel);
        baseBean.setP(mPhoneNumber);
        baseBean.setModel(Build.MODEL);
        baseBean.setBrand(Build.BRAND);
        baseBean.setSystem("Android");
        baseBean.setSystemV(Build.VERSION.RELEASE);
        baseBean.setAppV(mVersionCode + "");
        baseBean.setAppStartTime(mAppStartTime);
        return baseBean;
    }

    /**
     * 获取UUID，优先sp中拿；没有会生成保存到文件，没有权限保存到sp
     *
     * @return
     */
    private String getUUID() {
        if (TextUtils.isEmpty(mUUID)) {
            if (Constant.isGranted) {
                mUUID = readFileUUID(Constant.uuidFile);
                if (TextUtils.isEmpty(mUUID)) {
                    mUUID = mSp.getString(Constant.SP_UUID, "");
                    if (TextUtils.isEmpty(mUUID)) {
                        mUUID = createUUID();
                    }
                    creatUUIDFile(Constant.uuidFile, mUUID);
                }
            } else {
                mUUID = mSp.getString(Constant.SP_UUID, "");
                if (TextUtils.isEmpty(mUUID)) {
                    mUUID = createUUID();
                    mSp.edit().putString(Constant.SP_UUID, mUUID).commit();
                }
            }
        }
        return mUUID;
    }

    /**
     * 生成uuid
     *
     * @return
     */
    private String createUUID() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 5; i++) {
            int index = (int) Math.floor(Math.random() * (double) mDigitsSize);
            buffer.append(DIGITS[index]);
        }
        buffer.append(to62RadixString(System.currentTimeMillis()));
        return buffer.toString();
    }

    /**
     * 转换为62位缩短长度
     *
     * @param seq
     * @return
     */
    private static String to62RadixString(long seq) {
        StringBuilder sBuilder = new StringBuilder();
        while (true) {
            int remainder = (int) (seq % 62);
            sBuilder.append(DIGITS[remainder]);
            seq = seq / 62;
            if (seq == 0) {
                break;
            }
        }
        return sBuilder.toString();
    }

    /**
     * 把UUID写入文件
     *
     * @param uuidF
     */
    private void writeFileUUID(File uuidF, String content) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(uuidF.getAbsolutePath());
            fileOutputStream.write(content.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取UUID
     */
    private String readFileUUID(String fileName) {
        String uuidStr = "";
        if (Build.VERSION.SDK_INT < 30) {
            try {
                File uuidF = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        fileName);
                FileInputStream fis = new FileInputStream(uuidF);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                uuidStr = br.readLine();
                br.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media._ID
            };
            ContentResolver contentResolver = mContext.getContentResolver();
            String selection = MediaStore.Images.Media.DISPLAY_NAME + "=" + "'" + fileName + "'";
            Cursor mCursor = contentResolver.query(mImageUri, projection, selection, null, null);
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    int id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
                    Uri fileUri = ContentUris.withAppendedId(mImageUri, id);
                    try {
                        ParcelFileDescriptor fielDescriptor = contentResolver.openFileDescriptor(fileUri, "r", null);
                        FileInputStream inputStream = new FileInputStream(fielDescriptor.getFileDescriptor());
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        uuidStr = br.readLine();
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(uuidStr)) {
                        break;
                    }
                }
                mCursor.close();
            }
        }
        return uuidStr;
    }

    /**
     * 把uuid写入文件
     *
     * @param fileName
     * @param uuidStr
     */
    private void creatUUIDFile(String fileName, String uuidStr) {
        if (Build.VERSION.SDK_INT < 30) {
            File uuidF = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    fileName);
            if (!uuidF.exists()) {
                writeFileUUID(uuidF, uuidStr);
            }
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            Uri fileUri = mContext.getApplicationContext()
                    .getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (fileUri == null) {
                return;
            }
            OutputStream outputStream = null;
            try {
                outputStream = mContext.getContentResolver().openOutputStream(fileUri);
                if (outputStream != null) {
                    outputStream.write(uuidStr.getBytes());
                    outputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
                mContext.getContentResolver().delete(fileUri, null, null);
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }
}
