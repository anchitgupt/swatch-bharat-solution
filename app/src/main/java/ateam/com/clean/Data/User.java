package ateam.com.clean.Data;

/**
 * Created by apple on 26/11/17.
 */

public class User {
    public User() {
    }

    public String getUserID(String userName){
        String name = userName.replace(".","");
        return name;
    }
}
