package in.errorlabs.jbtransport.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 7/2/17.
 */

public class LatLngModel implements Parcelable{
    @SerializedName("latlng")
    @Expose
    public LatLng latLng;

    public LatLngModel(Parcel in) {
        latLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<LatLngModel> CREATOR = new Creator<LatLngModel>() {
        @Override
        public LatLngModel createFromParcel(Parcel in) {
            return new LatLngModel(in);
        }

        @Override
        public LatLngModel[] newArray(int size) {
            return new LatLngModel[size];
        }
    };

    public LatLngModel() {

    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(latLng, flags);
    }
}
