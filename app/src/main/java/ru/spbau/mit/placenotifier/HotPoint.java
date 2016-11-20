package ru.spbau.mit.placenotifier;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Should be used to contains user's favourite locations
 */
public class HotPoint implements Parcelable {

    private final String name;
    private final LatLng position;

    public HotPoint(String name, LatLng position) {
        this.name = name;
        this.position = position;
    }

    protected HotPoint(Parcel in) {
        name = in.readString();
        position = in.readParcelable(LatLng.class.getClassLoader());
    }

    public String getName() {
        return name;
    }

    public LatLng getPosition() {
        return position;
    }

    // some magic to do it Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(position, 0);
    }

    public static final Creator<HotPoint> CREATOR = new Creator<HotPoint>() {
        @Override
        public HotPoint createFromParcel(Parcel in) {
            return new HotPoint(in);
        }

        @Override
        public HotPoint[] newArray(int size) {
            return new HotPoint[size];
        }
    };
}
