package com.example.oivheg.resturantbussermaster;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.oivheg.resturantbussermaster.Communication.BusserRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by oivhe on 13.02.2017.
 */

public class ActiveUser {

    public static Boolean ASYNCisFInished = false;
    private static int NUM_ROWS = 1;
    private static int NUM_COL = 0;
    List<String> activeUsers = new ArrayList();
    int UserCounter = 0;

    public void ActiveUsers() {
        NUM_COL = 0;
        ASYNCisFInished = false;
        activeUsers.clear();

        System.out.println("Main: Started looking for users");
// her skal jeg få til å fikse while løkken fungerer ikke nå, den er stuck, hvordan løse dette ?

//        RequestParams params = new RequestParams();
//
//        params.put("MasterID", "1" );
        int Master = 1;
//Gets all active users for this specific Master
        BusserRestClientGet("GetAllActiveusers/" + Master, null);


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

                    for (int i = 0; i < success.length(); i++) {
                        JSONObject jsonobject = success.getJSONObject(i);
                        String UserName = jsonobject.getString("UserName");
                        String AppId = jsonobject.getString("AppId");
                        addUser(UserName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FindUsers();
//                PopulateTable();
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

    // fins each user and sets the sixe of rows and columns
    private void FindUsers() {
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

            } else if (activeUsers.size() != 0) {
                NUM_COL++;
            }


        }
    }

    // adds user to the table, as well as setting columns and rows based on user.
    public void addUser(String name) {
        NUM_ROWS = 1;
        NUM_COL = 0;
        UserCounter = 0;
        activeUsers.add(name);
//        FindUsers();
//        PopulateTable();
    }


}
