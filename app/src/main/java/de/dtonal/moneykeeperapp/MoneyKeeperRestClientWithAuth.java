package de.dtonal.moneykeeperapp;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by dtonal on 02.10.16.
 */

public class MoneyKeeperRestClientWithAuth {

    private static final String BASE_URL = "http://46.101.102.237:3000/api/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params,String mail, String pass,  AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(mail, pass);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, String mail, String pass, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(mail, pass);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url,String mail, String pass, AsyncHttpResponseHandler responseHandler){
        client.setBasicAuth(mail, pass);
        client.delete(getAbsoluteUrl(url), null, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
