package ateam.com.clean.Data;

/**
 * Created by apple on 12/10/17.
 */

public class UserData {

    String UUID,
            id,
    name,
    phone,
    city,
    state,
    e_mail
    ;

    public UserData(String UUID) {
        this.UUID = UUID;
    }

    public UserData(String id, String name, String phone, String city, String state, String e_mail) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.e_mail = e_mail;
    }
}
