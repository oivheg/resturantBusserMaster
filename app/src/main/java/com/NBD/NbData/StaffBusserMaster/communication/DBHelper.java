package com.NBD.NbData.StaffBusserMaster.communication;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by oivhe on 23.02.2017.
 */

public class DBHelper {

    public Connection connectionclass() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL;
        String pcLocal = "10.0.0.135:1433";
        String Ipserver = "91.192.221.155:1433";
        String server, database, user, password;
        server = pcLocal;
        database = "ResturantBusser";
        user = "android";
        password = "4bdk0jf2";

        try {
            //Code for connecting to database
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + ";"
                    + "databaseName=" + database + ";user=" + user + ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException se) {
            Log.e("error here 1 :", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("Error here 2 :", e.getMessage());
        } catch (Exception e) {
            Log.e("Error here 3 :", e.getMessage());
        }
        return connection;
    }
}
