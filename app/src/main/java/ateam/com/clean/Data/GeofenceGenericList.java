package ateam.com.clean.Data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Project Clean2
 * Created by Anchit Gupta on 13/03/18.
 * Under the MIT License
 */

public class GeofenceGenericList {

    public LatLng latlng;
    public String name;

    public GeofenceGenericList() {
    }

    public GeofenceGenericList(LatLng latlng, String name) {
        this.latlng = latlng;
        this.name = name;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public String getName() {
        return name;
    }
}
