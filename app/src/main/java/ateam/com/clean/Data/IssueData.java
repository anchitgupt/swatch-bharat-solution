package ateam.com.clean.Data;

/**
 * Created by apple on 19/11/17.
 */

public class IssueData {

    String
    url,
    location,
    key,
    type;

    public IssueData() {
    }

    public IssueData(String url, String location, String key, String type) {
        this.url = url;
        this.location = location;
        this.key = key;
        this.type = type;
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
