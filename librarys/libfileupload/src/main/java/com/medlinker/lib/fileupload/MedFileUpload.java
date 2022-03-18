package com.medlinker.lib.fileupload;


public class MedFileUpload {
    private static boolean isDebug;

    public static void init(boolean debug) {
        isDebug = debug;
    }

    public static boolean isDebug() {
        return isDebug;
    }
}
