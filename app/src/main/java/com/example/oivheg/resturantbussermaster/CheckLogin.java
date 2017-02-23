//package com.example.oivheg.resturantbussermaster;
//
//import android.os.AsyncTask;
//import android.os.StrictMode;
//import android.util.Log;
//import android.view.View;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import static com.example.oivheg.resturantbussermaster.R.id.progressBar;
//import static com.example.oivheg.resturantbussermaster.R.id.txtAnswere;
//
///**
// * Created by oivhe on 21.02.2017.
// */
//
//public class CheckLogin extends AsyncTask<String, String, String> {
//
//
//    Connection con;
//    String un, pass, db, ip;
//    // somehow this should be more secure, but for now this works
//
//    String z = "";
//    Boolean isSuccess = false; // used to check wheter the login fails or not
//
//    @Override
//    protected void onPreExecute() {
//        progressBar.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//        ip = "10.0.0.135:1433";
//        db = "ResturantBusser";
//        un = "android";
//        un = "android";
//        pass = "4bdk0jf2";
//        String username = contact.getText().toString();
//        String _phone = phone.getText().toString();
//        if (username.trim().equals("") || _phone.trim().equals("")) {
//            z = "Please enter Contact and phone";
//        } else {
//            try {
//                con = connectionclass(un, pass, db, ip);
//                if (con == null) {
//
//                } else {
//
//                    // Continue here, trying to show al info from the USERS Table .
//                    String query = "select * from Users";
//                    Statement stmt = con.createStatement();
//                    ResultSet rs = stmt.executeQuery(query);
//                    if (rs.next()) {
//                        z = "Login Successful";
//                        isSuccess = true;
//                        con.close();
//                    } else {
//                        z = "Invalid Credentials";
//                        txtAnswere.setText(rs.toString());
//                        isSuccess = false;
//                    }
//                }
//
//            } catch (Exception ex) {
//                isSuccess = false;
//                z = ex.getMessage();
//            }
//        }
//        return null;
//    }
//}
//
//    public Connection connectionclass(String user, String password, String database, String server) {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        Connection connection = null;
//        String ConnectionURL = null;
//        try {
//            //Code for connecting to database
//            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//            ConnectionURL = "jdbc:jtds:sqlserver://" + server + ";"
//                    + "databaseName=" + database + ";user=" + user + ";password=" + password + ";";
//            connection = DriverManager.getConnection(ConnectionURL);
//        } catch (SQLException se) {
//            Log.e("error here 1 :", se.getMessage());
//        } catch (ClassNotFoundException e) {
//            Log.e("Error here 2 :", e.getMessage());
//        } catch (Exception e) {
//            Log.e("Error here 3 :", e.getMessage());
//        }
//        return connection;
//    }
//}