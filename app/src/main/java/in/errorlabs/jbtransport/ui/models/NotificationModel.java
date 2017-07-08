package in.errorlabs.jbtransport.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 7/7/17.
 */

public class NotificationModel implements Parcelable{

    @SerializedName("Heading")
    @Expose
    public String Heading;
    @SerializedName("Meassage")
    @Expose
    public String Message;
    @SerializedName("TimeStamp")
    @Expose
    public String TimeStamp;

    public NotificationModel(Parcel in) {
        Heading = in.readString();
        Message = in.readString();
        TimeStamp = in.readString();
    }

    public static final Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel in) {
            return new NotificationModel(in);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };

    public NotificationModel() {

    }

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Heading);
        dest.writeString(Message);
        dest.writeString(TimeStamp);
    }
}
