package com.example.oivheg.resturantbussermaster.FCM;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oivheg.resturantbussermaster.Communication.BusserRestClient;
import com.example.oivheg.resturantbussermaster.MainActivity;
import com.example.oivheg.resturantbussermaster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class FCMLogin extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FCMLogin";
    String MasterKey, ResttName, EMail;
    SharedPreferences prefs = null;
    TextView infoip, msg;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_fcmlogin);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    Toast.makeText(FCMLogin.this, " User is already logged in.",
//                            Toast.LENGTH_LONG).show();
                    if (MasterKey != null) {
                        RequestParams params = new RequestParams();
                        params.put("MasterKey", MasterKey);
                        params.put("AppId", getFCMToken());
                        BusserRestClient.post("MasterAppId", params, new JsonHttpResponseHandler() {
                            public void onSuccess(int statusCode, Header headers[], JSONObject success) {
                                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                                // Handle resulting parsed JSON response here

                                System.out.println("Active usccesessfull push to server    :" +

                                        success);
                            }

                        });

                    } else {
//                        FirebaseAuth.getInstance().signOut();
                    }

                    MainActivity.getInstace().ActiveUsers();
                    MainActivity.getInstace().setMsterKey(MasterKey);
//                    msg = (TextView) findViewById(R.id.msg);
//                    msg.setText( MasterKey);
                    finish();
                    return;
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private String getFCMToken() {

        String tkn = FirebaseInstanceId.getInstance().getToken();
//        Toast.makeText(FCMLogin.this, "Current token [" + tkn + "]",
//                Toast.LENGTH_LONG).show();
        Log.d("Ap:FCM", "Token [" + tkn + "]");
        return tkn;
    }


    @Override
    public void onClick(View v) {
        EditText email = (EditText) findViewById(R.id.field_email);
        EditText pwd = (EditText) findViewById(R.id.field_password);
        EditText rstname = (EditText) findViewById(R.id.field_rstname);
        EMail = email.getText().toString();
        switch (v.getId()) {
            case R.id.btnlogin:
                signIn(EMail, pwd.getText().toString());
                break;
            case R.id.btncreateac:
                createAccount(email.getText().toString(), pwd.getText().toString());
                ResttName = rstname.getText().toString();
                break;
            default:
        }

    }

    public void createAccount(String email, String passwd) {

        mAuth.createUserWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
//                            Toast.makeText(FCMLogin.this, "Login error",
//                                    Toast.LENGTH_SHORT).show();
                        } else {
                            MasterKey = ResttName.substring(0, 3) + getFCMToken().substring(0, 4);

                            prefs.edit().putString("MasterKey", MasterKey).commit();

                            CreateMaster(EMail);

                        }

                        // ...
                    }
                });
    }

    private void CreateMaster(String email) {

        RequestParams params = new RequestParams();

        //params.put("UserId", 11);
        params.put("Resturant", ResttName);
        params.put("MasterKey", MasterKey);
        params.put("AppId", getFCMToken());
        params.put("Email", email);
        // ----------- Fake data as of now -------------//
        params.put("Contact", "øivind S Heggland");
        params.put("OrgNr", "no org");
        params.put("Phone", 92627034);


        BusserPost("CreateMaster", params, "FCMLOGIN:   Created user usccesessfull push to server    :");
    }

    private void BusserPost(String api, RequestParams _params, String message) {

        final String _message = message;

        BusserRestClient.post(api, _params, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header headers[], JSONObject success) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here

                System.out.println(_message +
                        success);
            }

        });
    }


    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
//                            Toast.makeText(FCMLogin.this, "Login error",
//                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        prefs = getSharedPreferences("com.example.oivhe.resturantbusser", MODE_PRIVATE);
        MasterKey = prefs.getString("MasterKey", null);
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
