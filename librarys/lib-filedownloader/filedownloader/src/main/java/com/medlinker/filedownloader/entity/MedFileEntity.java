package com.medlinker.filedownloader.entity;

import androidx.annotation.Keep;

import java.io.File;

/**
 * @author zhangquan
 */
@Keep
public class MedFileEntity {
    public String url;
    public String parentFile;
    public String fileName;
    public long total;

    @Override
    public String toString() {
        return "MedFileEntity{" +
                "url='" + url + '\'' +
                ", parentFile='" + parentFile + '\'' +
                ", fileName='" + fileName + '\'' +
                ", total=" + total +
                '}';
    }

    public File getFile() {
        return new File(parentFile, fileName);
    }

    public long getBreakpoint() {
        return getFile().length();
    }

    public boolean isCompleted() {
        return total > 0 && getFile().length() == total;
    }
}
