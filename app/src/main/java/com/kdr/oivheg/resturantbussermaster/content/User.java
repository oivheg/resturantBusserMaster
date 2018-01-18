package com.kdr.oivheg.resturantbussermaster.content;

/**
 * Created by oivhe on 08.09.2017.
 */

public class User {
    public final String Name;
    public final boolean isNotified;

    public User(String name, boolean isNotified) {
        Name = name;
        this.isNotified = isNotified;
    }
}
