package com.example.oivheg.resturantbussermaster;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int NUM_ROWS = 1;
    private static int NUM_COL = 0;
    Server server;
    TextView infoip, msg;
    String depactiveUsers[] = {"øivind", "Espen", "Linda", "kåre"};
    List<String> activeUsers= new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        server = new Server(this);
        infoip.setText(server.getIpAddress() + ":" + server.getPort());


//        findClients();
//        populateClients();
    }

    private void findClients() {
//        NUM_COL = 0;
        for (int users = 0; users < activeUsers.size(); users++) {
            NUM_COL++;
        }
    }

    public void addUser(String name){
        NUM_ROWS= 1;
        NUM_COL = 0;
        activeUsers.add("First");
        activeUsers.add(name);
        findClients();
        populateClients();
    }

    private void populateClients() {
        TableLayout table = (TableLayout) findViewById(R.id.tableForClients);
        table.removeAllViews();
        for (int row = 0; row < NUM_ROWS; row++) {

            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.BLACK);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f

            ));

            table.addView(tableRow);
            for (int col = 0; col < NUM_COL; col++) {
                final String FINAL_USER_NAME = activeUsers.get(col);
                Button button = new Button(this);


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

    private void gridButtonClicked(String name) {
        //Toast message for buttons
        Toast.makeText(this, name + "  Was Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }

}
