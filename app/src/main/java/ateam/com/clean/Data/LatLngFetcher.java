package ateam.com.clean.Data;

/**
 * Created by apple on 24/11/17.
 */

public class LatLngFetcher {
String latitude,
    longitude;


    public LatLngFetcher(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLngFetcher() {
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
