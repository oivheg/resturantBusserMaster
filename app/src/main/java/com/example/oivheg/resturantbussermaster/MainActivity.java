package com.example.oivheg.resturantbussermaster;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oivheg.resturantbussermaster.Communication.BusserRestClient;
import com.example.oivheg.resturantbussermaster.Communication.DBHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public static Boolean ASYNCisFInished = false;
    private static int NUM_ROWS = 1;
    private static int NUM_COL = 0;
    Server server;
    TextView infoip, msg;
    // String depactiveUsers[] = {"øivind", "Espen", "Linda", "kåre"};
    List<String> activeUsers = new ArrayList();
    // List<String> showUsers = new ArrayList();
    SharedPreferences prefs = null;
    int UserCounter = 0;
    Button btnrefresh, btnnotifyAll;
    View.OnClickListener refreshListener = new View.OnClickListener() {
        public void onClick(View v) {

            ActiveUsers();
        }
    };
    View.OnClickListener notifyAllListener = new View.OnClickListener() {
        public void onClick(View v) {

            NotifyAllUsers();
        }
    };

    private void NotifyAllUsers() {

        BusserRestClientPost("DinnerForAll", null);

    }

    private void BusserRestClientPost(String apicall, RequestParams params) {
        BusserRestClient.post(apicall, params, new JsonHttpResponseHandler() {
            //client1.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray success) {
                System.out.println("All users were notified" +
                        success);


            }

            @Override
            public void onSuccess(int statusCode, Header headers[], JSONObject success) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here

                System.out.println("Active JSON Object repsone    :" +
                        success);


            }


            @Override
            public void onFailure(int number, Header[] header, Throwable trh, JSONObject jsonobject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.print("ERROR" + jsonobject + "  status  " + number + " Header:  " + header);
            }
        });
    }


    private void BusserRestClientGet(String apicall, RequestParams params) {

        BusserRestClient.get(apicall, params, new JsonHttpResponseHandler() {


            //client1.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray success) {
                System.out.println("All users were notified" +
                        success);

                try {
                    JSONObject curr = success.getJSONObject(1);
                    for (int i = 0; i < success.length(); i++) {
                        JSONObject jsonobject = success.getJSONObject(i);
                        String UserName = jsonobject.getString("UserName");
                        String AppId = jsonobject.getString("AppId");
                        addUser(UserName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                findClients();
                PopulateTable();
            }

            @Override
            public void onSuccess(int statusCode, Header headers[], JSONObject success) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here

                System.out.println("Active JSON Object repsone    :" +
                        success);


            }


            @Override
            public void onFailure(int number, Header[] header, Throwable trh, JSONObject jsonobject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.print("ERROR" + jsonobject + "  status  " + number + " Header:  " + header);
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btnrefresh = (Button) findViewById(R.id.btnrefresh);
        btnnotifyAll = (Button) findViewById(R.id.btnnotifyAll);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
//        server = new Server(this);
//        infoip.setText(server.getIpAddress() + ":" + server.getPort());
        prefs = getSharedPreferences("com.example.oivhe.resturantbusser", MODE_PRIVATE);
        //HideStatusBar();
        ActiveUsers();
        btnrefresh.setOnClickListener(refreshListener);
        btnnotifyAll.setOnClickListener(notifyAllListener);

    }

    private void ActiveUsers() {
        ASYNCisFInished = false;
        activeUsers.clear();
        msg.setText("Finding Active USERS");
        System.out.println("Main: Started looking for users");
// her skal jeg få til å fikse while løkken fungerer ikke nå, den er stuck, hvordan løse dette ?

//        RequestParams params = new RequestParams();
//
//        params.put("MasterID", "1" );
        int Master = 1;

        BusserRestClientGet("GetAllActiveusers/" + Master, null);




//        CheckActiveUsers dbcheckUsers = new CheckActiveUsers();
        // dbcheckUsers.execute("");
//        while (!ASYNCisFInished) {
//           // ASYNCisFInished = dbcheckUsers.isSuccess;
//            System.out.println("waiting for async task to be finished");
//        }
//        msg.setText("Users Found");
//        findClients();
//        PopulateTable();
//        dbcheckUsers.cancel(true);

        msg.setText("Sync finsihed");
    }

    private void HideStatusBar() {
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }

    private void findClients() {
//        NUM_COL = 0;

        // error here, that activates when there are more than 3 users,, then there are added an additional row.
        for (int users = 0; users < activeUsers.size(); users++) {

            if (users >= 3) {
                double tmp = ((double) activeUsers.size() / 3);
                if (tmp == 0) {
                    tmp = 1;
                }
                tmp = Math.ceil(tmp);
                NUM_ROWS = (int) tmp;

            } else {
                NUM_COL++;
            }


        }
    }

    public void addUser(String name) {
        NUM_ROWS = 1;
        NUM_COL = 0;
        UserCounter = 0;
        activeUsers.add(name);
//        findClients();
//        PopulateTable();
    }

    private void PopulateTable() {
        TableLayout table = (TableLayout) findViewById(R.id.tableForClients);
        table.removeAllViews();
        for (int row = 0; row < NUM_ROWS; row++) {

            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.RED);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            ));


            table.addView(tableRow);

            for (int col = 0; col < NUM_COL; col++) {
                if (UserCounter >= activeUsers.size()) {
                    break;
                }
                final String FINAL_USER_NAME = activeUsers.get(UserCounter);
                Button button = new Button(this);
                UserCounter++;

                button.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));

                button.setBackgroundColor(Color.BLACK);
                button.setBackgroundResource(R.drawable.waiter_no);
                button.setText(FINAL_USER_NAME + " " + row + " " + col);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gridButtonClicked(FINAL_USER_NAME);
                    }
                });
                tableRow.addView(button);
            }
        }
    }

    public void gridButtonClicked(String name) {
        //Toast message for buttons
        Toast.makeText(this, name + "  Was Clicked", Toast.LENGTH_SHORT).show();
        RequestParams params = new RequestParams();

        params.put("UserName", name);
        BusserRestClientPost("DinnerisReady", params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  server.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (prefs.getBoolean("firstrun", true)) {
//            // Do first run stuff here then set 'firstrun' as false
//            Intent intent = new Intent(this, CreateMasterUser.class);
//            startActivity(intent);
//            this.finish();
//            // using the following line to edit/commit prefs
////            prefs.edit().putBoolean("firstrun", false).commit();
//        }
    }

    public class CheckActiveUsers extends AsyncTask<String, String, String> {

        String z = "";
        Boolean isSuccess = false; // used to check wheter the login fails or not
        Connection con;

        @Override
        protected void onPreExecute() {

            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(String... params) {


            try {
                DBHelper connectDb = new DBHelper();
                con = connectDb.connectionclass();
                if (con == null) {

                } else {

                    // Continue here, trying to show al info from the USERS Table .
                    String query = "\n" +
                            "select * from Users where MasterID = 1 and Active = 'true';";

                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {

                        String first = rs.getString("UserName");
                        addUser(first.trim());


                    }
                }

            } catch (Exception ex) {
                isSuccess = false;
                z = ex.getMessage();
            }

            isSuccess = true;
            return null;
        }
    }
}
