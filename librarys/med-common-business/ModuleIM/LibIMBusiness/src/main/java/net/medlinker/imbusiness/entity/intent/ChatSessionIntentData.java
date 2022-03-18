package net.medlinker.imbusiness.entity.intent;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author hmy
 * @time 2020/9/22 17:36
 */
public class ChatSessionIntentData  implements Parcelable {
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public ChatSessionIntentData(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sessionId);
    }

    public ChatSessionIntentData() {
    }

    protected ChatSessionIntentData(Parcel in) {
        this.sessionId = in.readString();
    }

    public static final Creator<ChatSessionIntentData> CREATOR = new Creator<ChatSessionIntentData>() {
        @Override
        public ChatSessionIntentData createFromParcel(Parcel source) {
            return new ChatSessionIntentData(source);
        }

        @Override
        public ChatSessionIntentData[] newArray(int size) {
            return new ChatSessionIntentData[size];
        }
    };
}
