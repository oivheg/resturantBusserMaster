package com.kdr.oivheg.resturantbussermaster;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kdr.oivheg.resturantbussermaster.communication.BusserRestClient;
import com.kdr.oivheg.resturantbussermaster.communication.DBHelper;
import com.kdr.oivheg.resturantbussermaster.content.User;
import com.kdr.oivheg.resturantbussermaster.fcm.FCMLogin;
import com.kdr.oivheg.resturantbussermaster.fcm.FCMMessageService;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static Boolean ASYNCisFInished = false;
    private static int NUM_ROWS = 1;
    private static int NUM_COL = 0;
    private static MainActivity ins;
    // String depactiveUsers[] = {"øivind", "Espen", "Linda", "kåre"};
    private final List<String[]> lst_activeUsers = new ArrayList();
    private final ArrayList<User> lst_userisactive = new ArrayList<>();
    //int _ButtonShape = R.drawable.round_button;
    private final int _backgorundimage = R.drawable.waiter_no;
    List<String> btnstateList = new ArrayList<String>();
    TextView msg;
    int _ButtonShape = R.drawable.btn_ripple;
    private TextView infoip;
    private String MasterKey;
    private final View.OnClickListener refreshListener = new View.OnClickListener() {
        public void onClick(View v) {

            refreshTable();


        }
    };
    // List<String> showUsers = new ArrayList();
    private SharedPreferences prefs = null;
    private int UserCounter = 0;
    private Button btnrefresh;
    private Button btnnotifyAll;
    private BroadcastReceiver receiver;
    private boolean allisnotified = false;
    private boolean wasNotified = false;
    private final View.OnClickListener notifyAllListener = new View.OnClickListener() {

        public void onClick(View v) {
            if (allisnotified) {
                btnnotifyAll.clearAnimation();
                btnnotifyAll.setBackgroundColor(Color.parseColor("#0000ff"));

                allisnotified = false;
                NotifyAllUsers(true);
                refreshTable();
            } else {
                btnNotifiedAnimation(btnnotifyAll);
                NotifyAllUsers(false);
                allisnotified = true;

            }


        }
    };
    private FCMMessageService myService;
    private boolean bound = false;

    public static MainActivity getInstace() {
        return ins;
    }

    public void refreshTable() {
        NUM_COL = 0;
        System.out.println("1:Startet refrehsing TABLE");
        ActiveUsers();
        System.out.println("2: finished refrehsing TABLE");

    }

    public void setMsterKey(String masterKey) {

        MasterKey = masterKey;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    msg.setText(MasterKey);
                } catch (Exception e) {
                    System.out.println("MAIN: ERROR Set Text MasterKey  " + e);
                }

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ins = this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.my.app.onMessageReceived");
        receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, intentFilter);

        Intent activeUser = new Intent(MainActivity.this, FCMLogin.class);
        this.startActivity(activeUser);
//        FirebaseMessaging.getInstance().subscribeToTopic("test");

        setContentView(R.layout.activity_main);

        btnrefresh = (Button) findViewById(R.id.btnrefresh);
        btnnotifyAll = (Button) findViewById(R.id.btnnotifyAll);

        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);

//        server = new Server(this);
//        infoip.setText(server.getIpAddress() + ":" + server.getPort());
        prefs = getSharedPreferences("com.kdr.oivhe.resturantbusser", MODE_PRIVATE);
        //HideStatusBar();
//        ActiveUsers();
        btnrefresh.setOnClickListener(refreshListener);
        btnnotifyAll.setOnClickListener(notifyAllListener);

    }

    private void NotifyAllUsers(boolean _CancelDinner) {

        RequestParams params = new RequestParams();
//        MasterKey = msg.getText().toString().trim();
        params.put("mstrKey", MasterKey);
        if (_CancelDinner) {
            wasNotified = false;
            ChangeButtons(false);
            btnnotifyAll.setText(getString(R.string.btnNotifAll));
            BusserRestClientPost("CancelDinnerForAll?" + params, null);

        } else {
            btnnotifyAll.setText("CancelDinner");
            wasNotified = true;
            ChangeButtons(true);

            BusserRestClientPost("DinnerForAll?" + params, null);

        }
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
                System.out.print("ERROR" + jsonobject + "  status  " + number + " Header:  " + Arrays.toString(header));
            }
        });
    }

    private void BusserRestClientGet(RequestParams params) {

        BusserRestClient.get("GetAllActiveusers/", params, new JsonHttpResponseHandler() {


            //client1.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray success) {
                System.out.println("All users were notified" +
                        success);
                try {

                    for (int i = 0; i < success.length(); i++) {
                        JSONObject jsonobject = success.getJSONObject(i);
                        String UserName = jsonobject.getString("UserName");
//                        String AppId = jsonobject.getString("AppId");
                        addUser(UserName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FindUsers();
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
                System.out.print("ERROR" + jsonobject + "  status  " + number + " Header:  " + Arrays.toString(header));
            }
        });
    }

    public void ActiveUsers() {

        ASYNCisFInished = false;
        lst_activeUsers.clear();
//        msg.setText("MAIN: Finding Active USERS");
        System.out.println("Main: Started looking for users");

        String tkn = FirebaseInstanceId.getInstance().getToken();
        RequestParams params = new RequestParams();
//        MasterKey = msg.getText().toString().trim();
        params.put("Appid", tkn);
        int Master = 1;
//Gets all active users for this specific Master
        BusserRestClientGet(params);


        msg.setText(MasterKey);
    }

    private void HideStatusBar() {
        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.hide();
    }

    // fins each user and sets the sixe of rows and columns
    private void FindUsers() {
//        NUM_COL = 0;

        // error here, that activates when there are more than 3 users,, then there are added an additional row.
        for (int users = 0; users < lst_activeUsers.size(); users++) {

            if (users >= 3) {
                double tmp = ((double) lst_activeUsers.size() / 3);
                if (tmp == 0) {
                    tmp = 1;
                }
                tmp = Math.ceil(tmp);
                NUM_ROWS = (int) tmp;

            } else if (lst_activeUsers.size() != 0) {
                NUM_COL++;
            }


        }
    }

    // adds user to the table, as well as setting columns and rows based on user.

    public void addUser(String name) {
        NUM_ROWS = 1;
        NUM_COL = 0;
        UserCounter = 0;

        String[] lstbtnInfo = {name, "test2"};
        lst_activeUsers.add(lstbtnInfo);
//        FindUsers();
//        PopulateTable();
    }

    private void ChangeButtons(final boolean _isBlinking) {


        final View view = this.findViewById(R.id.activity_main);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 0;
                    for (String[] user : lst_activeUsers) {

                        CircleImageView b = view.findViewWithTag(user[0].trim());
//                    b.setText("Melding Motatt");
                        if (_isBlinking) {
                            CivNotifiedAnimation(b);
                            String[] button = {user[0], "true"};
                            lst_activeUsers.set(i, button);
                        } else {
                            b.clearAnimation();
                            String[] button = {user[0], "false"};
                            lst_activeUsers.set(i, button);

                        }
                        i++;
                    }
                } catch (Exception e) {
                    System.out.println("MAIN: ERROR Could not StopBlink" + e);
                }

            }
        });


    }


    public void StopbtnBlink(final String user) {
        final View view = this.findViewById(R.id.activity_main);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    CircleImageView b = view.findViewWithTag(user.trim());

                    Object clickedFlag = b.getTag(R.string.BtnClicked);
                    Boolean tmp_wasNotified = IsButtonAlreadyClicked(b);


                    if (tmp_wasNotified) {

//                    b.setText("Melding Motatt");
                        //b.setPadding(30, 30, 30, 30);
//                    b.setAlpha(0.4f);
//                    b.setBackgroundResource(0);
                        // b.setBackgroundColor(Color.GREEN);
                        //b.setImageResource(_backgorundimage);
                        //  b.setBackgroundResource(_backgorundimage);
                        b.clearAnimation();
                        b.setBorderWidth(60);
                        b.setBorderColor(Color.GREEN);
                    } else {

                    }

                } catch (Exception e) {
                    System.out.println("MAIN: ERROR Could not StopBlink" + e);
                }

            }
        });


    }

    // Populates the table with the buttons for each user.
    private void PopulateTable() {

        TableLayout table = (TableLayout) findViewById(R.id.tableForClients);
        //  table.setPadding(0,10,0,0);
        table.removeAllViews();

        table.setBackgroundColor(Color.GREEN);
        //table.setLayoutParams(lp);
        for (int row = 0; row < NUM_ROWS; row++) {

            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.RED);
//           tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT, 1.0F
            );
////            lp.setMargins(10,10,10,10);
////            lp.rightMargin = 10;
            tableRow.setWeightSum(3);
            tableRow.setLayoutParams(lp);
            tableRow.setEnabled(false);
            tableRow.setBackgroundColor(Color.YELLOW);
//tableRow.setPadding(10,10,10,10);


            if (NUM_COL == 0) {

            } else {
                table.addView(tableRow);
            }


            for (int col = 0; col < NUM_COL; col++) {
                if (UserCounter >= lst_activeUsers.size()) {
                    break;
                }
                final String FINAL_USER_NAME = lst_activeUsers.get(UserCounter)[0]; //this adds the username to the texbox under the image.
                final CircleImageView button = CreateUserButton(FINAL_USER_NAME);
                LinearLayout LL = AddToLayout(row, col, FINAL_USER_NAME, button);

                tableRow.addView(LL);

            }
        }

        RestoreUsers();
    }

    @NonNull
    private LinearLayout AddToLayout(int row, int col, String FINAL_USER_NAME, CircleImageView button) {
        LinearLayout LL = new LinearLayout(this);

        TextView tv = new TextView(this);

        tv.setText(FINAL_USER_NAME); // this adds the USername to the textfield under image
        tv.setGravity(Gravity.CENTER);
        // tv.setLayoutParams(tblParams);
        LL.setOrientation(LinearLayout.VERTICAL);
        // LL.setBackgroundColor(Color.BLUE);


//                attributLayoutParams.gravity = Gravity.CENTER;
//

        Display display = getWindowManager().getDefaultDisplay();

//        Point size = new Point();
//        display.getSize(size);
//        int ParentWidth = size.x;
//        int ParentHeight = size.y;
//        ParentWidth = ParentWidth / 3;
//        ParentHeight = ParentHeight / 3;

        LL.setLayoutParams(new TableRow.LayoutParams(
                0, // width to 0, makes the setWeightSum og the TableRow work properly. so that each "columnd" inside the row, takes equal space, even if one is removed.
                TableRow.LayoutParams.MATCH_PARENT, 1.0F));
        // LL.setGravity(Gravity.CENTER);

        LL.setEnabled(false);
        LL.addView(button);
        LL.addView(tv);

        return LL;
    }

    @NonNull
    private CircleImageView CreateUserButton(final String FINAL_USER_NAME) {
        final CircleImageView button = new CircleImageView(this);
        UserCounter++;

        LinearLayout.LayoutParams tblParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0F);


        button.setLayoutParams(tblParams);
        // tableRow.setOrientation(LinearLayout.VERTICAL);

        //button.setScaleType(CircularImageView.ScaleType.CENTER_INSIDE);
//                button.setLayoutParams(tblParams);
        button.setImageResource(_backgorundimage);
        // button.setBackgroundColor(Color.RED);
        button.setBorderWidth(3);
        button.setFocusableInTouchMode(false);
        button.setBorderColor(Color.YELLOW);
        //button.setId("test");
        button.setPadding(10, 0, 0, 0);
        //button.setBackgroundResource(_backgorundimage);
        //button.setText(FINAL_USER_NAME + " " + row + " " + col);
        button.setTag(FINAL_USER_NAME.trim());
        button.setFocusableInTouchMode(false);
        // button.setTag(R.string.BtnClicked,"true");
        //button.setDefaultFocusHighlightEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean btnClicked = false;
                btnClicked = IsButtonAlreadyClicked(button);

                //String clickedFlag  = button.getTag(R.string.BtnClicked).toString();

                if (btnClicked) {
                    ChagenBTNList(button, "false");
                    ClearButtonAnimation(button);
                    //button.setTag(R.string.BtnClicked, false);
                    button.setPadding(0, 0, 0, 0);
                    button.setBorderColor(Color.YELLOW);
                    button.setBorderWidth(2);
                    //  button.setfoc(false);
//                            return;
                } else {
                    ChagenBTNList(button, "true");
                    CivNotifiedAnimation(button);
                    //button.setTag(R.string.BtnClicked, true);

                }
                gridButtonClicked(btnClicked, FINAL_USER_NAME);
            }
        });
        return button;
    }

    private Boolean IsButtonAlreadyClicked(CircleImageView button) {
        for (String[] user : lst_activeUsers) {
            String btnnameTag = button.getTag().toString().trim();
            String LstbtnNameTag = user[0].trim();
            String LstbtnState = user[1].trim();
            if (btnnameTag.equals(LstbtnNameTag) && LstbtnState.equals("true")) {
                return true;
            }
        }
        return false;
    }

    private void ChagenBTNList(CircleImageView button, String value) {
        int lst_counter = 0;
        for (String[] user : lst_activeUsers) {
            String btnnameTag = button.getTag().toString().trim();
            String LstUserTag = user[0].trim();

            if (btnnameTag.equals(LstUserTag)) {
                String[] btnInfo = {button.getTag().toString(), value};
                lst_activeUsers.set(lst_counter, btnInfo);
            }
            lst_counter++;
        }
    }

    private void ClearButtonAnimation(CircleImageView button) {
        button.clearAnimation();
        //button.setBackgroundColor(Color.BLACK);
        //button.setBackgroundResource(_backgorundimage);
    }

    private void RestoreUsers() {

        for (final User user : lst_userisactive) {
            final View view = this.findViewById(R.id.activity_main);
            if (user.isNotified) {
                System.out.println("3:Startet refrehsing TABLE");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Button b = view.findViewWithTag(user.Name.trim());
                            b.setBackgroundColor(Color.GREEN);
                        } catch (Exception e) {
                            System.out.println("MAIN: RestoreUSers ERROR Set Button bacground failed  " + e);
                        }

                    }
                });
            }

        }

    }

    private void CivNotifiedAnimation(CircleImageView button) {
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btnblinking_animation);

        button.setBorderWidth(20);
        button.startAnimation(startAnimation);
        button.setBorderColor(Color.rgb(255, 165, 0));
        // button.setBackgroundColor(Color.rgb(255, 165, 0));
    }

    private void btnNotifiedAnimation(Button button) {
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btnblinking_animation);
        button.startAnimation(startAnimation);
        button.setBackgroundColor(Color.rgb(255, 165, 0));
    }

    private void gridButtonClicked(boolean ClikedTag, String name) {
        //Toast message for buttons
//        Toast.makeText(this, name + "  Was Clicked", Toast.LENGTH_SHORT).show();
        if (ClikedTag) {

            RequestParams params = new RequestParams();
            params.put("UserName", name.trim());
            BusserRestClientPost("CancelDinner", params);
            wasNotified = false;
            Iterator<User> user = lst_userisactive.iterator();
            while (user.hasNext()) {
                User usr = user.next();
                if (usr.Name.equals(name)) {
                    user.remove();
                }
            }

        } else {
            //      creates request paramter with user, so that specific user are notified.
            RequestParams params = new RequestParams();
            params.put("UserName", name.trim());
            BusserRestClientPost("DinnerisReady", params);
            wasNotified = true;
            User User = new User(name, true);
            lst_userisactive.add(User);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  server.onDestroy();

        try {
            if (receiver != null)
                unregisterReceiver(receiver);
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // this is going to be removed, as everything will be handled my appID and response will come from api
        prefs = getSharedPreferences("com.kdr.oivhe.resturantbusser", MODE_PRIVATE);
        MasterKey = prefs.getString("MasterKey", null);
        MainActivity.getInstace().setMsterKey(MasterKey);


    }


    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Bundle extras = intent.getExtras();
//            String state = extras.getString("extra");
            refreshTable();// update your textView in the main layout
        }
    }

    // Not in Use anymore
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
