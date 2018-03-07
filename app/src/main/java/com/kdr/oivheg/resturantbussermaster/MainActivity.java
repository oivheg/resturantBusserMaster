package com.kdr.oivheg.resturantbussermaster;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kdr.oivheg.resturantbussermaster.communication.BusserRestClient;
import com.kdr.oivheg.resturantbussermaster.communication.ConnectionReceiver;
import com.kdr.oivheg.resturantbussermaster.content.User;
import com.kdr.oivheg.resturantbussermaster.fcm.FCMLogin;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static int NUM_ROWS = 1;
    private static int NUM_COL = 0;
    private static MainActivity ins;
    // String depactiveUsers[] = {"øivind", "Espen", "Linda", "kåre"};
    private final ArrayList<User> tmp_userisactive = new ArrayList<>();
    private final ArrayList<User> lst_userisactive = new ArrayList<>();
    public boolean isloggedin = false;
    TextView msg;
    CircleImageView GeneralUserButton;
    int _ButtonShape = R.drawable.btn_ripple;
    int RetryNetwork = 0;
    // --Commented out by Inspection (31.01.2018 09.07):List<String> btnstateList = new ArrayList<String>();
    private ProgressDialog progressDialog;
    private String MasterKey;
    private final View.OnClickListener refreshListener = new View.OnClickListener() {
        public void onClick(View v) {
            ProgressBar("Laster", "Leter etter brukere, vennligst vent", false);
            refreshTable();


        }
    };
    // List<String> showUsers = new ArrayList();
    private SharedPreferences prefs = null;
    private int UserCounter = 0;
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
        if (!HasNetwork()) {
            ProgressBar("No Network", "Lukk appen, sjekk nettverk og prøv igjen", false);


        } else {
            ProgressBar("Laster", "Leter etter brukere, vennligst vent", false);

        }
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Button btnrefresh = (Button) findViewById(R.id.btnrefresh);
        btnnotifyAll = (Button) findViewById(R.id.btnnotifyAll);

        TextView infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);

//        server = new Server(this);
//        infoip.setText(server.getIpAddress() + ":" + server.getPort());
        prefs = getSharedPreferences("com.kdr.oivhe.resturantbusser", MODE_PRIVATE);
        //HideStatusBar();
//        ActiveUsers();
        btnrefresh.setOnClickListener(refreshListener);
        btnnotifyAll.setOnClickListener(notifyAllListener);

            refreshTable();


    }

    private void ProgressBar(String title, String message, boolean isCancable) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(message); // Setting Message
        progressDialog.setTitle(title); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show();
        // Display Progress Dialog
        progressDialog.setCancelable(isCancable);


    }

    private void NotifyAllUsers(boolean _CancelDinner) {

        RequestParams params = new RequestParams();
//        MasterKey = msg.getText().toString().trim();
        params.put("mstrKey", MasterKey);
        if (_CancelDinner) {
            wasNotified = false;
            ChangeAllButtons(false);
            btnnotifyAll.setText(getString(R.string.btnNotifAll));
            BusserRestClientPost("CancelDinnerForAll?" + params, null);

        } else {
            btnnotifyAll.setText(R.string.cancelDinner);
            wasNotified = true;
            ChangeAllButtons(true);

            BusserRestClientPost("DinnerForAll?" + params, null);

        }
    }

    private void BusserRestClientPost(String apicall, RequestParams params) {

        if (!HasNetwork()) {
            ProgressBar("Laster", "Leter etter brukere, vennligst vent", false);
            return;
        }
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

    private boolean HasNetwork() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
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
                progressDialog.dismiss();
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
                progressDialog.dismiss();
                System.out.print("ERROR" + jsonobject + "  status  " + number + " Header:  " + Arrays.toString(header));
                ProgressBar("Server ERROR", "har desverre ikke kontat med Serveren, vennligst prøv igjen senere", true);

//                if (RetryNetwork <= 5) {
//                    RetryNetwork ++;
//                    Intent i = getBaseContext().getPackageManager()
//                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//                }else {
//                    RetryNetwork = 0;
//
//                }
            }
        });
    }


    public void ActiveUsers() {
        tmp_userisactive.clear();
        Boolean ASYNCisFInished = false;
        for (User _tmpUser : lst_userisactive) {
            boolean tmpstatus = _tmpUser.isClicked;
            if (tmpstatus) {
                tmp_userisactive.add(_tmpUser);
            }
        }

        lst_userisactive.clear();
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

// --Commented out by Inspection START (31.01.2018 09.07):
//    private void HideStatusBar() {
//        View decorView = getWindow().getDecorView();
//// Hide the status bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//// Remember that you should never show the action bar if the
//// status bar is hidden, so hide that too if necessary.
//        ActionBar actionBar = getActionBar();
//        assert actionBar != null;
//        actionBar.hide();
//    }
// --Commented out by Inspection STOP (31.01.2018 09.07)

    // fins each user and sets the sixe of rows and columns
    private void FindUsers() {
//        NUM_COL = 0;

        // error here, that activates when there are more than 3 users,, then there are added an additional row.
        for (int users = 0; users < lst_userisactive.size(); users++) {

            if (users >= 3) {
                double tmp = ((double) lst_userisactive.size() / 3);
                if (tmp == 0) {
                    tmp = 1;
                }
                tmp = Math.ceil(tmp);
                NUM_ROWS = (int) tmp;

            } else if (lst_userisactive.size() != 0) {
                NUM_COL++;
            }


        }
    }

    // adds user to the table, as well as setting columns and rows based on user.

    public void addUser(String name) {
        NUM_ROWS = 1;
        NUM_COL = 0;
        UserCounter = 0;

//        String[] lstbtnInfo = {name, "test2"};
        //  lst_activeUsers.add(lstbtnInfo);
        User tmpUser = new User(name, false, false);
        lst_userisactive.add(tmpUser);
//        FindUsers();
//        PopulateTable();
    }

    private void ChangeAllButtons(final boolean _isBlinking) {


        final View view = this.findViewById(R.id.activity_main);


                    for (User usr : lst_userisactive) {
                        CircleImageView b = view.findViewWithTag(usr.Name.trim());
                        // usr.isClicked = true;
                        if (_isBlinking) {
                            usr.isClicked = false;
                            SetButtonStatus(b, b.getTag().toString());
                        } else {
                            usr.isClicked = true;

                            SetButtonStatus(b, b.getTag().toString());
                        }


                    }



    }


    public void StopbtnBlink(final String user) {
        final View view = this.findViewById(R.id.activity_main);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    CircleImageView b = view.findViewWithTag(user.trim());

                    Object clickedFlag = b.getTag(R.string.BtnClicked);
                    Boolean wasCLicked = IsButtonAlreadyClicked(b);
                    for (User usr : lst_userisactive) {
                        if (usr.Name.trim().equals(b.getTag().toString().trim())) {
                            usr.isNotified = true;
                        }
                    }

                    if (wasCLicked) {

                        b.clearAnimation();
                        b.setBorderWidth(60);
                        b.setBorderColor(Color.GREEN);
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

        // table.setBackgroundColor(Color.GREEN);
        //table.setLayoutParams(lp);
        for (int row = 0; row < NUM_ROWS; row++) {

            TableRow tableRow = new TableRow(this);
            //tableRow.setBackgroundColor(Color.RED);
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
            //tableRow.setBackgroundColor(Color.YELLOW);
//tableRow.setPadding(10,10,10,10);


            if (NUM_COL != 0) {

                table.addView(tableRow);
            }
//            Iterator<User> user = lst_userisactive.iterator();
//
//            while (user.hasNext()) {
//                for (int col = 0; col < NUM_COL; col++) {
//                    if (UserCounter >= lst_userisactive.size()) {
//                   break;
//                }
//                 String FINAL_USER_NAME = user.next().Name  ; //this adds the username to the texbox under the image.
//                final CircleImageView button = CreateUserButton(FINAL_USER_NAME);
//               LinearLayout LL = AddToLayout(row, col, FINAL_USER_NAME, button);
//
//                tableRow.addView(LL);
//
//            }
//            }


            for (int col = 0; col < NUM_COL; col++) {

                if (UserCounter >= lst_userisactive.size()) {
                    break;
                }
                final String FINAL_USER_NAME = lst_userisactive.get(UserCounter).Name; //this adds the username to the texbox under the image.
                final CircleImageView button = CreateUserButton(FINAL_USER_NAME);
                LinearLayout LL = AddToLayout(row, col, FINAL_USER_NAME, button);

                tableRow.addView(LL);

            }
        }

        RestoreUsers();
        //progressDialog.hide();


    }

    @NonNull
    private LinearLayout AddToLayout(int row, int col, String FINAL_USER_NAME, CircleImageView button) {
        LinearLayout LL = new LinearLayout(this);

        TextView tv = new TextView(this);

        tv.setText(FINAL_USER_NAME); // this adds the USername to the textfield under image
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
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
        int _backgorundimage = R.drawable.waiter_no;
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
                Boolean btnClicked;
                btnClicked = IsButtonAlreadyClicked(button);
                SetButtonStatus(button, FINAL_USER_NAME);

                gridButtonClicked(btnClicked, FINAL_USER_NAME);


            }
        });
        return button;
    }

    private void SetButtonStatus(CircleImageView button, String FINAL_USER_NAME) {
        Boolean btnClicked;
        btnClicked = IsButtonAlreadyClicked(button);

        //String clickedFlag  = button.getTag(R.string.BtnClicked).toString();

        if (btnClicked) {
            ChagenBTNList(button.getTag().toString(), false);
            ClearButtonAnimation(button);
            //  ChangeAllButtons(false);
            //button.setTag(R.string.BtnClicked, false);
            button.setPadding(0, 0, 0, 0);
            button.setBorderColor(Color.YELLOW);
            button.setBorderWidth(2);
            //  button.setfoc(false);
//                            return;
        } else {
            ChagenBTNList(button.getTag().toString(), true);
            //button.setTag(R.string.BtnClicked, true);
            //ChangeAllButtons(true);
            CivButtonStartAnimation(button);


        }

    }


    private Boolean IsButtonAlreadyClicked(CircleImageView button) {
//        for (String[] user : lst_activeUsers) {
//            String btnnameTag = button.getTag().toString().trim();
//            String LstbtnNameTag = user[0].trim();
//            String LstbtnState = user[1].trim();
//            if (btnnameTag.equals(LstbtnNameTag) && LstbtnState.equals("true")) {
//                return true;
//            }
//        }

        for (User usr : lst_userisactive) {
            String btnName = button.getTag().toString().trim();
            if (usr.Name.trim().equals(btnName)) {
                return usr.isClicked;
            }
        }
        return false;
    }

    private void ChagenBTNList(String buttonName, boolean value) {
        int lst_counter = 0;
//        for (String[] user : lst_activeUsers) {
//            String btnnameTag = button.getTag().toString().trim();
//            String LstUserTag = user[0].trim();
//
//            if (btnnameTag.equals(LstUserTag)) {
//                String[] btnInfo = {button.getTag().toString(), value};
//                lst_activeUsers.set(lst_counter, btnInfo);
//            }
//            lst_counter++;
//        }

        for (User usr : lst_userisactive) {
            if (usr.Name.trim().equals(buttonName.trim())) {
                usr.isClicked = value;
                usr.isNotified = false;
            }
        }
    }

    private void ClearButtonAnimation(CircleImageView button) {
        button.clearAnimation();
        //button.setBackgroundColor(Color.BLACK);
        //button.setBackgroundResource(_backgorundimage);
    }

    private void RestoreUsers() {
        boolean UserisNotified;
        boolean BtnIsClicked;
        for (final User user : lst_userisactive) {

            for (User _tmpUser : tmp_userisactive) {

                String lstUsername = user.Name.trim();
                if (_tmpUser.isClicked && _tmpUser.Name.trim().equals(lstUsername)) {
                    user.isClicked = _tmpUser.isClicked;
                    user.isNotified = _tmpUser.isNotified;
                }
            }
            UserisNotified = user.isNotified;
            BtnIsClicked = user.isClicked;

            final View view = this.findViewById(R.id.activity_main);
            CircleImageView b = view.findViewWithTag(user.Name.trim());
//            if (b.getAnimation() != null) {
//                if (b.getAnimation().hasStarted()) {
//                    String Temp = "did nothing";
//                }
//            }
            if (BtnIsClicked) {
                CivButtonStartAnimation(b);
            }

            if (UserisNotified) {
                System.out.println("3:Startet refrehsing TABLE");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //GeneralUserButton= view.findViewWithTag(user.Name.trim());
                            StopbtnBlink(user.Name.trim());
                        } catch (Exception e) {
                            System.out.println("MAIN: RestoreUSers ERROR Set Button bacground failed  " + e);
                        }

                    }
                });
            }

        }


    }

    private void CivButtonStartAnimation(CircleImageView button) {
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
            User User = new User(name, false, true);
            lst_userisactive.remove(User);
            //FindUser(name);

        } else {
            //      creates request paramter with user, so that specific user are notified.
            RequestParams params = new RequestParams();
            params.put("UserName", name.trim());
            BusserRestClientPost("DinnerisReady", params);
            wasNotified = true;
            User User = new User(name, false, true);

            // lst_userisactive.add(User);
        }


    }

    private void FindUser(String name) {
        Iterator<User> user = lst_userisactive.iterator();
        while (user.hasNext()) {
            User usr = user.next();
            if (usr.Name.equals(name)) {
                user.remove();
            }
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

        refreshTable();
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

    // --Commented out by Inspection START (31.01.2018 09.07):
    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            //show a No Internet Alert or Dialog
        }
    }
// --Commented out by Inspection STOP (31.01.2018 09.07)

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Bundle extras = intent.getExtras();
//            String state = extras.getString("extra");
            refreshTable();// update your textView in the main layout
        }
    }

////    // Not in Use anymore
//    public class CheckActiveUsers extends AsyncTask<String, String, String> {
//
//String z = "";
//   Boolean isSuccess = false; // used to check wheter the login fails or not
//        Connection con;
//
//        @Override
//        protected void onPreExecute() {
//
//            // progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//
//            super.onPostExecute(s);
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//
//            try {
//                DBHelper connectDb = new DBHelper();
//                con = connectDb.connectionclass();
//                if (con == null) {
//
//                } else {
//
//                    // Continue here, trying to show al info from the USERS Table .
//                    String query = "\n" +
//                            "select * from Users where MasterID = 1 and Active = 'true';";
//
//                    Statement stmt = con.createStatement();
//                    ResultSet rs = stmt.executeQuery(query);
//
//                    while (rs.next()) {
//
//                        String first = rs.getString("UserName");
//                        addUser(first.trim());
//
//
//
//                    }
//                }
//
//            } catch (Exception ex) {
//                isSuccess = false;
//                z = ex.getMessage();
//
//            }
//
//            isSuccess = true;
//            return null;
//        }
//    }
}
