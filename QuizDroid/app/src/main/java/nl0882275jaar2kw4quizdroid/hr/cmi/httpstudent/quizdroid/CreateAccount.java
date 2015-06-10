package nl0882275jaar2kw4quizdroid.hr.cmi.httpstudent.quizdroid;

import android.app.Activity;
import com.loopj.android.http.*;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.http.Header;

import java.net.InetAddress;


public class CreateAccount extends Activity implements OnClickListener {

    private String valName;
    private String valAge;
    private String valGender;
    private String valPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        loadPref();
        ((Button)findViewById(R.id.button_register)).setOnClickListener(this);
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
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
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
            Intent intent = new Intent(CreateAccount.this, SetPreferenceActivity.class);
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
            case R.id.button_register:
                EditText inputName = (EditText)findViewById(R.id.register_name);
                valName = inputName.getText().toString();

                EditText inputAge = (EditText)findViewById(R.id.register_age);
                valAge = inputAge.getText().toString();

                RadioGroup radioGroup = (RadioGroup)findViewById(R.id.register_gender);
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = (RadioButton) findViewById(radioButtonId);
                    valGender = selectedRadioButton.getText().toString();
                }

                EditText inputPassword = (EditText)findViewById(R.id.register_password);
                EditText inputConfirmPassword = (EditText)findViewById(R.id.register_confirm_password);
                valPassword = inputPassword.getText().toString();
                String valConfirmPassword = inputConfirmPassword.getText().toString();

                if(!valName.equals("") && !valConfirmPassword.equals("") && !valPassword.equals("")) {
                    if (valPassword.equals(valConfirmPassword)) {
                        registerAccount(valName, valAge, valGender, valPassword);
                        Toast t = Toast.makeText(this, "Welkom in QuizDroid! Probeer in te loggen", Toast.LENGTH_SHORT);
                        t.show();
                        finish();
                    } else {
                        Toast t = Toast.makeText(this, "Het eerste wachtwoord komt niet over een met de tweede! Vul 2 keer hetzelfde wachtwoord in.", Toast.LENGTH_SHORT);
                        t.show();
                    }
                } else {
                    Toast t = Toast.makeText(this, "U heeft uw naam of wachtwoord niet volledig ingevuld.", Toast.LENGTH_SHORT);
                    t.show();
                }
                break;
        }
    }

    private void registerAccount(String name, String age, String gender, String password) {
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("age", age);
        params.put("gender", gender);
        params.put("password", password);
        params.put("highscore", "0");
        RestClient restClient = new RestClient();
        restClient.Client.cancelRequests(this, true);

        restClient.postCreatedUser(this, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.d("response", new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getApplicationContext(),throwable.toString(),Toast.LENGTH_SHORT).show();
                Log.e("response", throwable.toString());
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
}
