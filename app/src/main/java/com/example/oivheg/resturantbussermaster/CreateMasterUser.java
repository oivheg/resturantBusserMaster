package com.example.oivheg.resturantbussermaster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreateMasterUser extends AppCompatActivity implements View.OnClickListener {
    private Button reg;
    private TextView Rest, email, phone, contact, OrgNr;

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

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnreg:


                // checking Data and start Sending to server code
                break;
            default:
        }

    }
}
