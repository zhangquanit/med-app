package com.medlinker.reactnative.codepush;

/**
 * @author hmy
 */
public class RNCodePushConstants {
    public static final String ASSETS_BUNDLE_PREFIX = "assets://";
    public static final String BASE_BUNDLE_FOLDER = "jsBundles";
    public static final String STATUS_FILE_NAME = "android.meta";
    public static final String DEFAULT_JS_BUNDLE_FILE_NAME = "android.jsbundle";
    public static final String DEFAULT_MANIFEST_FILE_NAME = "android.manifest";
    public static final String TAG_LIST_JSON_FILE_NAME = "tagList.json";

    public static final String CODE_PUSH_ROOT_FOLDER = "RNHotfix"; // RN 热更新根目录
    public static final String CODE_PUSH_FOLDER = "RNCodePush"; // 存储热更新所有文件的目录（加载当前目录下的文件）
    public static final String CODE_PUSH_DOWNLOAD_FOLDER = "RNCodePushDownload"; // 热更文件下载目录
    public static final String CODE_PUSH_BACKUP_FOLDER = "RNCodePushBackup"; // 备份上次可用的RN文件的目录
    public static final String CODE_PUSH_META_FOLDER = "RNMeta"; // 存储用于对比是否热更新的meta文件的目录

    public static final int DOWNLOAD_BUFFER_SIZE = 1024 * 256;

    public static final String CODE_PUSH_PREFERENCES = "RNCodePush";
    public static final String FAILED_UPDATES_KEY = "CODE_PUSH_FAILED_UPDATES";
    public static final String PENDING_UPDATE_KEY = "CODE_PUSH_PENDING_UPDATE";
    public static final String UNLOAD_UPDATE_KEY = "CODE_PUSH_UNLOAD_UPDATE";
    public static final String BASE_META_HASH_KEY = "CODE_BASE_META_HASH";
}
