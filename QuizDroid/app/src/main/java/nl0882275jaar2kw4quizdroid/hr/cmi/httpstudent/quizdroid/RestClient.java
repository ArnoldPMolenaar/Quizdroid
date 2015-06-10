/**
 * Created by Arnold on 19-5-2015.
 */
package nl0882275jaar2kw4quizdroid.hr.cmi.httpstudent.quizdroid;

import android.content.Context;

import com.loopj.android.http.*;

import org.apache.http.entity.StringEntity;

public class RestClient {
    private static String BASE_URL = "http://student.cmi.hr.nl/0882275/jaar2/kw4/quizdroid/";
    private static String GET_USER = BASE_URL + "users/";
    public AsyncHttpClient Client = new AsyncHttpClient();

    public RestClient(){
        Client.addHeader("Accept", "application/json");
        Client.addHeader("Content-Type", "application/json");
    }

    public void updateUser(String id, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        Client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        Client.put(GET_USER + id, params, asyncHttpResponseHandler);
    }

    public void getUsers(AsyncHttpResponseHandler asyncHttpResponseHandler) {
        Client.get(BASE_URL, asyncHttpResponseHandler);
    }

    public void getUser(String id, AsyncHttpResponseHandler asyncHttpResponseHandler){
        Client.get(GET_USER + id, asyncHttpResponseHandler);
    }

    public void postCreatedUser(Context context, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler){
        Client.post(context, BASE_URL, params, asyncHttpResponseHandler);
    }

}
