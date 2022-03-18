package entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author : HuBoChao
 * @date : 2021/5/18
 * @desc :
 */
public class FileEntity implements Parcelable {

    private String fileUrl;

    public FileEntity() {
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileUrl);
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

    public FileEntity(Parcel parcel) {
        fileUrl = parcel.readString();
    }
}
