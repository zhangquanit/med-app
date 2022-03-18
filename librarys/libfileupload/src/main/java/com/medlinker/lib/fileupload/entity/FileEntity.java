
package com.medlinker.lib.fileupload.entity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.heaven7.adapter.ISelectable;

import net.medlinker.base.entity.DataEntity;

import java.util.ArrayList;
import java.util.List;

public class FileEntity extends DataEntity implements Parcelable, Cloneable, ISelectable {

    /**
     * 客户端生成的唯一标识，用与和本地图片对应
     */
    @SerializedName("uniqueKey")
    private String uniqueKey;
    /**
     * 图片ID
     */
    @SerializedName("fileId")
    @Expose
    private long fileId;
    /**
     * 图片地址
     */
    @SerializedName("fileUrl")
    @Expose
    private String fileUrl;


    @SerializedName("fileName")
    @Expose
    private String fileName;

    /**
     * 缓存缩略图
     */
    private String smallFileUrl;
    /**
     * 图片宽度
     */
    @SerializedName("width")
    @Expose
    private int width;
    /**
     * 图片高度
     */
    @SerializedName("height")
    @Expose
    private int height;

    /**
     * 文本大小 小于1000K时显示k 整数  否则显示M 一位小数
     */
    @SerializedName("fileSize")
    @Expose
    private String fileSize;

    /*
     * 图片是否支持原图0正常压缩，1支持原图
     * */
    @SerializedName("isSuportOriginal")
    @Expose
    private int isSuportOriginal;

    /**
     * 音频时长
     */
    @SerializedName("duration")
    @Expose
    private int duration;

    @SerializedName("url")
    private String inquiryUrl;

    private int fileTag;

    private Bitmap bitmap;

    private String fileUuid;

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    private boolean mSelected;

    @Override
    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    @Override
    public boolean isSelected() {
        return mSelected;
    }


    /**
     * 本地标识 是否已经查看了原图
     */
    @Expose(serialize = false, deserialize = false)
    private int hasShowedOriginalPic;

    private String tokenEntity;

    public int getIsSuportOriginal() {
        return isSuportOriginal;
    }

    public void setIsSuportOriginal(int isSuportOriginal) {
        this.isSuportOriginal = isSuportOriginal;
    }

    public int getHasShowedOriginalPic() {
        return hasShowedOriginalPic;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public void setHasShowedOriginalPic(int hasShowedOriginalPic) {
        this.hasShowedOriginalPic = hasShowedOriginalPic;
    }

    public boolean supportShowOriginal() {
        return hasShowedOriginalPic == 1;
    }

    /**
     * @return The fileId
     */
    public long getFileId() {
        return fileId;
    }

    /**
     * @param fileId The fileId
     */
    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    /**
     * @return The fileUrl
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * @param fileUrl The fileUrl
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSmallFileUrl() {
        return smallFileUrl;
    }

    public void setSmallFileUrl(String smallFileUrl) {
        this.smallFileUrl = smallFileUrl;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileEntity() {
    }

    public FileEntity(int id, String path) {
        this.fileId = id;
        this.fileUrl = path;
    }

    public FileEntity(String id, String path) {
        this.fileUuid = id;
        this.fileUrl = path;
    }

    public FileEntity(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getTokenEntity() {
        return tokenEntity;
    }

    public void setTokenEntity(String tokenEntity) {
        this.tokenEntity = tokenEntity;
    }

    public String getInquiryUrl() {
        return inquiryUrl;
    }

    public void setInquiryUrl(String inquiryUrl) {
        this.inquiryUrl = inquiryUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "fileId=" + fileId +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", smallFileUrl='" + smallFileUrl + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", fileSize='" + fileSize + '\'' +
                ", isSuportOriginal=" + isSuportOriginal +
                ", duration=" + duration +
                ", inquiryUrl='" + inquiryUrl + '\'' +
                ", fileTag=" + fileTag +
                ", hasShowedOriginalPic=" + hasShowedOriginalPic +
                ", tokenEntity='" + tokenEntity + '\'' +
                ", fileUuid='" + fileUuid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        if (fileUuid != that.fileUuid) return false;
        return !(fileUrl != null ? !fileUrl.equals(that.fileUrl) : that.fileUrl != null);
    }

    @Override
    public int hashCode() {
        long result = fileId;
        result = 31 * result + (fileUrl != null ? fileUrl.hashCode() : 0);
        return (int) result;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public Object clone() {
        FileEntity entity = null;
        try {
            entity = (FileEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return entity;
    }


    /**
     * strlist 2 fileList
     *
     * @param stringList
     * @return
     */
    public static List<FileEntity> str2fileList(List<String> stringList) {
        List<FileEntity> fileEntities = new ArrayList<>();
        for (String str : stringList) {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileUrl(str);
            fileEntities.add(fileEntity);
        }
        return fileEntities;
    }

    public int getFileTag() {
        return fileTag;
    }

    public void setFileTag(int fileTag) {
        this.fileTag = fileTag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.fileId);
        dest.writeString(this.fileUrl);
        dest.writeString(this.fileName);
        dest.writeString(this.smallFileUrl);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.fileSize);
        dest.writeInt(this.isSuportOriginal);
        dest.writeInt(this.duration);
        dest.writeString(this.inquiryUrl);
        dest.writeInt(this.fileTag);
        dest.writeParcelable(this.bitmap, flags);
        dest.writeInt(this.hasShowedOriginalPic);
        dest.writeString(this.tokenEntity);
        dest.writeString(this.fileUuid);
        dest.writeByte(this.mSelected ? (byte) 1 : (byte) 0);
    }

    protected FileEntity(Parcel in) {
        super.readFromParcel(in);
        this.fileId = in.readLong();
        this.fileUrl = in.readString();
        this.fileName = in.readString();
        this.smallFileUrl = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.fileSize = in.readString();
        this.isSuportOriginal = in.readInt();
        this.duration = in.readInt();
        this.inquiryUrl = in.readString();
        this.fileTag = in.readInt();
        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.hasShowedOriginalPic = in.readInt();
        this.tokenEntity = in.readString();
        this.fileUuid = in.readString();
        this.mSelected = in.readByte() != 0;
    }

    public static final Creator<FileEntity> CREATOR = new Creator<FileEntity>() {
        @Override
        public FileEntity createFromParcel(Parcel source) {
            return new FileEntity(source);
        }

        @Override
        public FileEntity[] newArray(int size) {
            return new FileEntity[size];
        }
    };
}
