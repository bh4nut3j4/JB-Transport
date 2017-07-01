package in.errorlabs.jbtransport.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 6/29/17.
 */

public class RouteSelectModel implements Parcelable{
    @SerializedName("routeNumber")
    @Expose
    private String routeNumber;
    @SerializedName("fcmRouteID")
    @Expose
    private String fcmRouteID;
    @SerializedName("routeStartPoint")
    @Expose
    private String routeStartPoint;
    @SerializedName("routeEndPoint")
    @Expose
    private String routeEndPoint;
    @SerializedName("routeViaPoint")
    @Expose
    private String routeViaPoint;
    @SerializedName("routeFullPath")
    @Expose
    private String routeFullPath;



    public RouteSelectModel(Parcel in) {
        routeNumber = in.readString();
        fcmRouteID = in.readString();
        routeStartPoint = in.readString();
        routeEndPoint = in.readString();
        routeViaPoint = in.readString();
        routeFullPath = in.readString();
    }

    public static final Creator<RouteSelectModel> CREATOR = new Creator<RouteSelectModel>() {
        @Override
        public RouteSelectModel createFromParcel(Parcel in) {
            return new RouteSelectModel(in);
        }

        @Override
        public RouteSelectModel[] newArray(int size) {
            return new RouteSelectModel[size];
        }
    };

    public RouteSelectModel() {

    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getFcmRouteID() {
        return fcmRouteID;
    }

    public void setFcmRouteID(String fcmRouteID) {
        this.fcmRouteID = fcmRouteID;
    }

    public String getRouteStartPoint() {
        return routeStartPoint;
    }

    public void setRouteStartPoint(String routeStartPoint) {
        this.routeStartPoint = routeStartPoint;
    }

    public String getRouteEndPoint() {
        return routeEndPoint;
    }

    public void setRouteEndPoint(String routeEndPoint) {
        this.routeEndPoint = routeEndPoint;
    }

    public String getRouteViaPoint() {
        return routeViaPoint;
    }

    public void setRouteViaPoint(String routeViaPoint) {
        this.routeViaPoint = routeViaPoint;
    }

    public String getRouteFullPath() {
        return routeFullPath;
    }

    public void setRouteFullPath(String routeFullPath) {
        this.routeFullPath = routeFullPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(routeNumber);
        dest.writeString(fcmRouteID);
        dest.writeString(routeStartPoint);
        dest.writeString(routeEndPoint);
        dest.writeString(routeViaPoint);
        dest.writeString(routeFullPath);
    }
}
