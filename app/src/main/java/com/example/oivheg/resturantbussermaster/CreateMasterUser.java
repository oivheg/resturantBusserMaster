package com.example.oivheg.resturantbussermaster;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateMasterUser extends AppCompatActivity {
    private Button reg;
    public TextView Rest, email, phone, contact, OrgNr, txtAnswere;
    ProgressBar progressBar;

    //SQL Connection variables
    Connection con;
    String un, pass, db, ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_master_user);

        reg = (Button) findViewById(R.id.btnreg);
        Rest = (TextView) findViewById(R.id.txtResturant);
        phone = (TextView) findViewById(R.id.txtphone);
        email = (TextView) findViewById(R.id.txtEmail);
        contact = (TextView) findViewById(R.id.txtcontact);
        OrgNr = (TextView) findViewById(R.id.txtOrgNR);
        txtAnswere = (TextView) findViewById(R.id.txtAnswere);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
// declaring server ip, username, database name and password



        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLogin checkLogin = new CheckLogin();
                checkLogin.execute("");
            }
        });
    }


    public class CheckLogin extends AsyncTask<String, String, String> {

        String z = "";
        Boolean isSuccess = false; // used to check wheter the login fails or not

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String username = contact.getText().toString();
            String _phone = phone.getText().toString();

            System.out.println("Started connecting");
            if (username.trim().equals("") || _phone.trim().equals("")) {
                z = "Please enter Contact and phone";
            } else {
                try {
                    DBHelper connectDb = new DBHelper();
                    con = connectDb.connectionclass();
                    System.out.println("I am Connected");
                    if (con == null) {

                    } else {

                        // Continue here, trying to show al info from the USERS Table .
                        String query = "select * from Users";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        System.out.println("I Got some answers");
                        if (rs.next()) {
                            z = "Login Successful";
                            isSuccess = true;
                            System.out.println("Correct Data entered");
                            //contact.setText("Connecting");
                            con.close();
                        } else {
                            z = "Invalid Credentials";
                            //txtAnswere.setText(rs.toString());
                            System.out.println("Invalid Data entered");
                            isSuccess = false;
                        }
                    }

                } catch (Exception ex) {
                    isSuccess = false;
                    z = ex.getMessage();


                }
            }
            return null;
        }
    }

    public Connection connectionclass(String user, String password, String database, String server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
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


