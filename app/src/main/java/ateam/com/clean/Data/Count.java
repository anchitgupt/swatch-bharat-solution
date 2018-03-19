package ateam.com.clean.Data;

/**
 * Project Clean2
 * Created by Anchit Gupta on 18/03/18.
 * Under the MIT License
 */

public class Count {

    public long
    garbage,
    pit,
    child,
    log;

    public Count(long garbage, long pit, long child, long log) {
        this.garbage = garbage;
        this.pit = pit;
        this.child = child;
        this.log = log;
    }

    public Count() {
    }

    public long getGarbage() {
        return garbage;
    }

    public long getPit() {
        return pit;
    }

    public long getChild() {
        return child;
    }

    public long getLog() {
        return log;
    }
}
