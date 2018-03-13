package ateam.com.clean.Data;

/**
 * Project Clean2
 * Created by Anchit Gupta on 13/03/18.
 * Under the MIT License
 */

public class KeyCount {
    public int count;
    public String keys;

    public KeyCount() {
    }

    public KeyCount(int count, String keys) {
        this.count = count;
        this.keys = keys;
    }

    public int getCount() {
        return count;
    }

    public String getKeys() {
        return keys;
    }
}
