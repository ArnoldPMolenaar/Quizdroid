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

import java.net.InetAddress;


public class QuizActivity extends Activity {

    public static final int QUESTION_TEXT_REQUEST = 1;
    public static final int QUESTION_SOUND_REQUEST = 2;

    private String[][] Questions = {
        { "Wat gebeurt er als je mee doet met een survival cursus - en je haalt het niet?", "Je moet naar huis", "Je overleeft het niet", "Je bent de weg kwijt in het bos", "Je bent de weg kwijt in het bos" },
        { "Wanneer er nieuwe hondenvoer is die een beter smaak heeft. Wie proeft het?", "De hond", "Een respondent", "De maker", "De maker" }
    };

    private String[][] SoundQuestions = {
        { "Raad de volgorde waarin de muziek afgespeeld wordt!", "Calvin Harris - Outside, Alesso - Heroes, Echosmith - Cool Kids, John Legend - All of me", "Alesso - Heroes, Calvin Harris - Outside, Echosmith - Cool Kids, John Legend - All of me", "Echosmith - Cool Kids, Calvin Harris - Outside, Alesso - Heroes, John Legend - All of me", "Alesso - Heroes, Calvin Harris - Outside, Echosmith - Cool Kids, John Legend - All of me", "1" },
        { "Raad de volgorde waarin de muziek afgespeeld wordt!", "Wiz Khalifa - See You Again, Carly Rae Jepsen - I Really Like You, Alesso - cool, Ellie Goulding - Love Me Like You Do", "Wiz Khalifa - See You Again, Ellie Goulding - Love Me Like You Do, Alesso - cool, Carly Rae Jepsen - I Really Like You", "Alesso - cool, Carly Rae Jepsen - I Really Like You, Wiz Khalifa - See You Again, Ellie Goulding - Love Me Like You Do", "Wiz Khalifa - See You Again, Carly Rae Jepsen - I Really Like You, Alesso - cool, Ellie Goulding - Love Me Like You Do", "2" }
    };

    private int QuestionNumber;
    private int QuestionSeries;
    private int score;
    protected String loginId;
    protected Intent questionScreen;
    protected Intent questionSoundScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        loadPref();

        Intent thisIntent = getIntent();
        loginId = thisIntent.getStringExtra("loginId");
        if(loginId.equals("null")){
            Toast.makeText(this, "U moet eerst inloggen", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            QuestionNumber = 0;
            QuestionSeries = 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isNetworkConnected() && !isInternetAvailable()){
            Toast.makeText(this, "Er is geen internet en netwerk verbinding in de applicatie", Toast.LENGTH_LONG).show();
        }

        if(QuestionNumber <= 1) {
            switch (QuestionSeries) {
                case 0:
                    questionScreen = new Intent(this, QuestionActivity.class);
                    questionScreen.putExtra("question", Questions[QuestionNumber][0]);
                    questionScreen.putExtra("A", Questions[QuestionNumber][1]);
                    questionScreen.putExtra("B", Questions[QuestionNumber][2]);
                    questionScreen.putExtra("C", Questions[QuestionNumber][3]);
                    questionScreen.putExtra("goodAnswer", Questions[QuestionNumber][4]);
                    startActivityForResult(questionScreen, QUESTION_TEXT_REQUEST);
                    QuestionSeries++;
                    break;
                case 1:
                    questionSoundScreen = new Intent(this, QuestionSoundActivity.class);
                    questionSoundScreen.putExtra("question", SoundQuestions[QuestionNumber][0]);
                    questionSoundScreen.putExtra("A", SoundQuestions[QuestionNumber][1]);
                    questionSoundScreen.putExtra("B", SoundQuestions[QuestionNumber][2]);
                    questionSoundScreen.putExtra("C", SoundQuestions[QuestionNumber][3]);
                    questionSoundScreen.putExtra("goodAnswer", SoundQuestions[QuestionNumber][4]);
                    questionSoundScreen.putExtra("sound", SoundQuestions[QuestionNumber][5]);
                    startActivityForResult(questionSoundScreen, QUESTION_SOUND_REQUEST);
                    QuestionSeries = 0;
                    QuestionNumber++;
                    break;
            }
        } else {
            Intent resultScreen = new Intent(this, ResultActivity.class);
            resultScreen.putExtra("highscore", score);
            resultScreen.putExtra("loginId", loginId);
            startActivity(resultScreen);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
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
            Intent intent = new Intent(QuizActivity.this, SetPreferenceActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loadPref();

        switch(requestCode) {
            case (QUESTION_TEXT_REQUEST): {
                if (resultCode == Activity.RESULT_OK) {
                    score += data.getIntExtra("score", 0);
                    Log.d("Score", score+"");
                }
                break;
            }
            case (QUESTION_SOUND_REQUEST): {
                if (resultCode == Activity.RESULT_OK) {
                    score += data.getIntExtra("score", 0);
                    Log.d("Score", score+"");
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
