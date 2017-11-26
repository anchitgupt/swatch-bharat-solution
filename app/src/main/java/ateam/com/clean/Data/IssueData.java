package ateam.com.clean.Data;

/**
 * Created by apple on 19/11/17.
 */

public class IssueData {

    String
    url,
    location,
    key,
    type,
    time,
    desc;

    public IssueData() {
    }

    public IssueData(String url, String location, String key, String type, String time, String desc) {
        this.url = url;
        this.location = location;
        this.key = key;
        this.type = type;
        this.time = time;
        this.desc = desc;
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
}
