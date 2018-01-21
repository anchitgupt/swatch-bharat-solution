package ateam.com.clean.Data;

/**
 * Project Clean2
 * Created by Anchit Gupta on 20/01/18.
 * Under the MIT License
 */

public class AdminData {

    String
            url,
            latlng,
            location,
            key,
            type,
            time,
            desc,
            worker,
            email;

    public AdminData(String url, String latlng, String location, String key, String type, String time, String desc, String worker, String email) {
        this.url = url;
        this.latlng = latlng;
        this.location = location;
        this.key = key;
        this.type = type;
        this.time = time;
        this.desc = desc;
        this.worker = worker;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUrl() {
        return url;
    }

    public String getLatlng() {
        return latlng;
    }

    public String getLocation() {
        return location;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getDesc() {
        return desc;
    }

    public String getWorker() {
        return worker;
    }
}
