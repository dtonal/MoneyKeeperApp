package de.dtonal.moneykeeperapp.connection;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import de.dtonal.moneykeeperapp.user.UserData;

/**
 * Created by dtonal on 02.10.16.
 */

public class MoneyKeeperRestClientWithAuth {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params,AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(UserData.mailAdress, UserData.password);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params,AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(UserData.mailAdress, UserData.password);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url,  AsyncHttpResponseHandler responseHandler){
        client.setBasicAuth(UserData.mailAdress, UserData.password);
        client.delete(getAbsoluteUrl(url), null, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return Urls.URL_BACKEND + relativeUrl;
    }
}
