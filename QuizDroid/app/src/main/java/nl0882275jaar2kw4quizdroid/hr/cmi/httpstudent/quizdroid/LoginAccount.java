package nl0882275jaar2kw4quizdroid.hr.cmi.httpstudent.quizdroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

public class LoginAccount extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_account);

        loadPref();

        ((Button)findViewById(R.id.button_login)).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_account, menu);
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
            Intent intent = new Intent(LoginAccount.this, SetPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loadPref();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_login:
                EditText inputName = (EditText)findViewById(R.id.login_name);
                String valName = inputName.getText().toString();

                EditText inputPassword = (EditText)findViewById(R.id.login_password);
                String valPassword = inputPassword.getText().toString();

                if(!valName.equals("") && !valPassword.equals("")){
                    loginAccount(valName, valPassword);
                } else {
                    Toast.makeText(this, "Uw naam of wachtwoord is niet ingevuld", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void loginAccount(String name, String password){
        final String Name = name;
        final String Password = password;

        RestClient restClient = new RestClient();
        restClient.getUsers(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String sResponse = new String(bytes);

                try {
                    JSONObject aResponse = new JSONObject(sResponse);
                    JSONArray aUsers = aResponse.getJSONArray("items");

                    boolean ifTrue = false;

                    for(int j = 0; j < aUsers.length(); j++){
                        JSONObject obj = aUsers.getJSONObject(j);
                        String dbName = obj.getString("name");
                        String dbPassword = obj.getString("password");
                        if(dbName.equals(Name) && dbPassword.equals(Password)){
                            ifTrue = true;
                            doMessage("Welkom "+dbName);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("UserId", obj.getString("id"));
                            resultIntent.putExtra("Name", dbName);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                            break;
                        }
                    }

                    if(!ifTrue) {
                        doMessage("Uw wachtwoord of gebruikersnaam komt niet overeen!");
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

    private void doMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
