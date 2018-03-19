package ateam.com.clean.Data;

/**
 * Created by apple on 12/10/17.
 */

public class UserData {

    public String UUID,
            id,
    name,
    phone,
    city,
    state,
    e_mail,
    photoURL
    ;
    public Count count;

    /*public UserData(String id, String name, String phone, String city, String state, String e_mail, Count count) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.e_mail = e_mail;
    }*/

    /*public UserData(String UUID, String id, String name, String phone, String city, String state, Count count) {
        this.UUID = UUID;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.e_mail = e_mail;
        this.count = count;
    }*/

    public UserData(String id, String name, String phone, String city, String state, String e_mail, Count count) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.e_mail = e_mail;
        this.count = count;
    }

    public UserData(String id, String name, String phone, String city, String state, String e_mail, String photoURL, Count count) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.e_mail = e_mail;
        this.photoURL = photoURL;
        this.count = count;
    }
}
