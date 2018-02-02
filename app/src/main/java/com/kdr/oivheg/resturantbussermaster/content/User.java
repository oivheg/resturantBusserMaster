package com.kdr.oivheg.resturantbussermaster.content;

/**
 * Created by oivhe on 08.09.2017.
 */

public class User {
    public final String Name;
    public boolean isNotified;
    public boolean isClicked;

    public User(String name, boolean isNotified, boolean isClicked) {
        Name = name;
        this.isNotified = isNotified;
        this.isClicked = isClicked;
    }
}
