package ateam.com.clean.Data;

/**
 * Created by apple on 19/11/17.
 */

public class IssueData {

    String
    email,
    url,
    latlng,
    location,
    key,
    type,
    time,
    desc,
    status;

    public IssueData() {
    }

    public String getLatlng() {
        return latlng;
    }

    public IssueData(String email, String url, String latlng, String location, String key, String type, String time, String desc, String status) {
        this.email = email;
        this.url = url;
        this.latlng = latlng;
        this.location = location;
        this.key = key;
        this.type = type;
        this.time = time;
        this.desc = desc;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
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

    public String getEmail() {
        return email;
    }
}
