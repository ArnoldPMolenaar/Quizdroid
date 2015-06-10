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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;


public class QuestionActivity extends Activity implements OnClickListener {

    private String goodAnswer;
    protected int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        loadPref();

        Intent thisIntent = getIntent();
        TextView question=(TextView)findViewById(R.id.question);
        question.setText(thisIntent.getStringExtra("question"));
        TextView A=(TextView)findViewById(R.id.A);
        A.setText(thisIntent.getStringExtra("A"));
        TextView B=(TextView)findViewById(R.id.B);
        B.setText(thisIntent.getStringExtra("B"));
        TextView C=(TextView)findViewById(R.id.C);
        C.setText(thisIntent.getStringExtra("C"));

        goodAnswer = thisIntent.getStringExtra("goodAnswer");

        ((Button)findViewById(R.id.button_answer)).setOnClickListener(this);
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
        getMenuInflater().inflate(R.menu.menu_question, menu);
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
            Intent intent = new Intent(QuestionActivity.this, SetPreferenceActivity.class);
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
            case R.id.button_answer:
                RadioGroup radioGroup = (RadioGroup)findViewById(R.id.answer);
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selectedRadioButton = (RadioButton) findViewById(radioButtonId);
                    String valAnswer = selectedRadioButton.getText().toString();
                    if(valAnswer.equals(goodAnswer)){
                        score = 10;
                    } else {
                        score = 0;
                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("score", score);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Kies een antwoord om verder te gaan", Toast.LENGTH_SHORT).show();
                }
                break;
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
