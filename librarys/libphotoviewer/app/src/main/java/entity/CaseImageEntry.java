package entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CaseImageEntry implements Parcelable {

    public String fileId;
    public String originPath;
    public String tagFileId;
    public String tagFileUrl;
    public byte state; //0 - 当前显示标注图 1 - 当前显示原图

    protected CaseImageEntry(Parcel in) {
        fileId = in.readString();
        originPath = in.readString();
        tagFileId = in.readString();
        tagFileUrl = in.readString();
        state = in.readByte();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileId);
        dest.writeString(originPath);
        dest.writeString(tagFileId);
        dest.writeString(tagFileUrl);
        dest.writeByte(state);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CaseImageEntry> CREATOR = new Creator<CaseImageEntry>() {
        @Override
        public CaseImageEntry createFromParcel(Parcel in) {
            return new CaseImageEntry(in);
        }

        @Override
        public CaseImageEntry[] newArray(int size) {
            return new CaseImageEntry[size];
        }
    };
}
