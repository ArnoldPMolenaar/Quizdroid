package nl0882275jaar2kw4quizdroid.hr.cmi.httpstudent.quizdroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;


public class ResultActivity extends Activity implements LocationListener {

    protected int highScore;
    protected String loginId;
    protected String userLocation;
    protected String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        loadPref();

        Intent thisIntent = getIntent();
        highScore = thisIntent.getIntExtra("highscore", 0);
        loginId = thisIntent.getStringExtra("loginId");

        TextView highscoreField = (TextView)findViewById(R.id.highscore_field);
        highscoreField.setText(highScore+"");

        setAccount(loginId);

        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isNetworkConnected() && !isInternetAvailable()){
            Toast.makeText(this, "Er is geen internet en netwerk verbinding in de applicatie", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(ResultActivity.this, SetPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAccount(String loginId) {
        final String LoginId = loginId;

        RestClient restClient = new RestClient();
        restClient.getUser(LoginId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String sResponse = new String(bytes);
                Log.d("loginId", sResponse);

                try {
                    JSONObject aUser = new JSONObject(sResponse);
                    userName = aUser.getString("name");
                    updateAccount(LoginId, aUser.getString("name"), aUser.getString("age"), aUser.getString("gender"), aUser.getString("password"), highScore + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("Throwable", throwable + "");
            }
        });
    }

    private void updateAccount(String loginId, String userName, String userAge, String userGender, String userPassword, String userHighScore) {
        final String LoginId = loginId;
        final String UserName = userName;
        final String UserGender = userGender;
        final String UserAge = userAge;
        final String UserPassword = userPassword;
        final String UserHighScore = userHighScore;

        RestClient restClient = new RestClient();
        RequestParams params = new RequestParams();
        params.put("name", UserName);
        params.put("age", UserAge);
        params.put("gender", UserGender);
        params.put("password", UserPassword);
        params.put("highscore", UserHighScore);

        if(highScore >= Integer.parseInt(userHighScore)) {
            restClient.updateUser(LoginId, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    String sResponse = new String(bytes);
                    Log.d("ResponsePut", sResponse);
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("Throwable", throwable + "");
                }
            });
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent mainScreen = new Intent(this, MainActivity.class);
        mainScreen.putExtra("loginId", loginId);
        mainScreen.putExtra("name", userName);
        startActivity(mainScreen);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        TextView locationData = (TextView)findViewById(R.id.loc_data);
        locationData.setText("De latitude en longitude is: " + latitude + ", " + longitude + ". Uw locatie wordt geladen!");

        Geocoder gcd = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0){
                userLocation = addresses.get(0).getLocality();
                TextView locationMessage = (TextView)findViewById(R.id.location_message);
                locationMessage.setText("U heeft de Quiz gemaakt vanuit: " + userLocation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loadPref();
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
