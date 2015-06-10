package nl0882275jaar2kw4quizdroid.hr.cmi.httpstudent.quizdroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;


public class MyProfile extends Activity {

    private String loginId;
    private String loginName;
    public static final int LOGIN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        loadPref();

        Intent thisIntent = getIntent();
        loginId = thisIntent.getStringExtra("loginId");
        getUser(loginId);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isNetworkConnected() && !isInternetAvailable()){
            Toast.makeText(this, "Er is geen internet en netwerk verbinding in de applicatie", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent mainScreen = new Intent(this, MainActivity.class);
        mainScreen.putExtra("loginId", loginId);
        mainScreen.putExtra("name", loginName);
        startActivity(mainScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    private void setLoginName(String name){
        loginName = name;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MyProfile.this, SetPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUser(String loginId){
        final String LoginId = loginId;

        RestClient restClient = new RestClient();
        restClient.getUsers(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String sResponse = new String(bytes);

                try {
                    JSONObject aResponse = new JSONObject(sResponse);
                    JSONArray aUsers = aResponse.getJSONArray("items");

                    boolean login = false;

                    for(int j = 0; j < aUsers.length(); j++){
                        JSONObject obj = aUsers.getJSONObject(j);
                        String dbLoginId = obj.getString("id");
                        if(dbLoginId.equals(LoginId)){
                            login = true;
                            setLoginName(obj.getString("name"));
                            TextView name=(TextView)findViewById(R.id.login_name);
                            name.setText(obj.getString("name"));
                            TextView age=(TextView)findViewById(R.id.login_age);
                            age.setText(obj.getString("age"));
                            TextView gender=(TextView)findViewById(R.id.login_gender);
                            gender.setText(obj.getString("gender"));
                            TextView highscore=(TextView)findViewById(R.id.login_highscore);
                            highscore.setText(obj.getString("highscore"));
                            break;
                        }
                    }

                    if(!login) {
                        goToLogin();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("Throwable", throwable+"");
            }
        });
    }

    private void goToLogin(){
        Intent loginAccountScreen = new Intent(this, LoginAccount.class);
        startActivityForResult(loginAccountScreen, LOGIN_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loadPref();

        switch(requestCode) {
            case (LOGIN_REQUEST): {
                if (resultCode == Activity.RESULT_OK) {
                    loginId = data.getStringExtra("UserId");
                    getUser(loginId);
                }
                break;
            }
        }
    }

    /**
     * load preferences from the settings
     */
    private void loadPref(){
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(mySharedPreferences.getBoolean("checkbox_background", false)){
            RelativeLayout rl = (RelativeLayout)findViewById(R.id.layout);
            rl.setBackgroundColor(Color.LTGRAY);
        } else {
            RelativeLayout rl = (RelativeLayout)findViewById(R.id.layout);
            rl.setBackgroundColor(Color.WHITE);
        }
    }

    /**
     * Checks for a network connection like wifi
     * @return bool
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    /**
     * This one checks if the internet is connected by contacting a webadres google.com
     * @return bool
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }
}
