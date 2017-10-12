package ateam.com.clean.Data;

/**
 * Created by apple on 12/10/17.
 */

public class UserData {

    String UUID,
    name,
    age,
    city,
    state,
    e_mail
    ;

    public UserData(String UUID) {
        this.UUID = UUID;
    }

    public UserData(String name, String age, String city, String state, String e_mail) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.state = state;
        this.e_mail = e_mail;
    }
}
