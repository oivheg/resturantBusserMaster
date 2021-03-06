package com.NBD.NbData.StaffBusserMaster.communication;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by oivhe on 02.03.2017.
 */


public class BusserRestClient {
    private static final String Server = "http://91.189.171.231/restbusserv/api/UserAPI/";
    private static final String BASE_URL = Server;
    private static final AsyncHttpClient client = new AsyncHttpClient();
    // --Commented out by Inspection (31.01.2018 09.07):static String pcLocal = "http://10.0.0.159:51080/api/UserAPI/";

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}

