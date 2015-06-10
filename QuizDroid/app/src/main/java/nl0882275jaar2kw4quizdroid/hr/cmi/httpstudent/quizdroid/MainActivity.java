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
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

public class MainActivity extends Activity implements OnClickListener {

    public static final int LOGIN_REQUEST = 1;
    public String loginId;
    public String loginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPref();
        loginId = "null";

        Intent thisIntent = getIntent();
        String sLoginId = thisIntent.getStringExtra("loginId");
        String sLoginName = thisIntent.getStringExtra("name");

        if(sLoginId != null){
            loginId = sLoginId;
            TextView welkomMessage = (TextView)findViewById(R.id.welkom_message);
            welkomMessage.setText("Welkom " + sLoginName);
        }

        ((Button)findViewById(R.id.button_create_account)).setOnClickListener(this);
        ((Button)findViewById(R.id.button_login)).setOnClickListener(this);
        ((Button)findViewById(R.id.start_quiz)).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SetPreferenceActivity.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.action_profile:
                Intent myProfileScreen = new Intent(this, MyProfile.class);
                myProfileScreen.putExtra("loginId", loginId);
                startActivity(myProfileScreen);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isNetworkConnected() && !isInternetAvailable()){
            Toast.makeText(this, "Er is geen internet en netwerk verbinding in de applicatie", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_create_account:
                Intent createAccountScreen = new Intent(this, CreateAccount.class);
                startActivity(createAccountScreen);
                break;
            case R.id.button_login:
                Intent loginAccountScreen = new Intent(this, LoginAccount.class);
                startActivityForResult(loginAccountScreen, LOGIN_REQUEST);
                break;
            case R.id.start_quiz:
                Intent mainQuizScreen = new Intent(this, QuizActivity.class);
                mainQuizScreen.putExtra("loginId", loginId);
                startActivity(mainQuizScreen);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loadPref();

        switch(requestCode) {
            case (LOGIN_REQUEST): {
                if (resultCode == Activity.RESULT_OK) {
                    loginId = data.getStringExtra("UserId");
                    loginName = data.getStringExtra("Name");
                    TextView welkomMessage = (TextView)findViewById(R.id.welkom_message);
                    welkomMessage.setText("Welkom " + loginName);
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
