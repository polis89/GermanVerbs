package ru.polis.germanverbs.games;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.Random;

import ru.polis.germanverbs.R;
import ru.polis.germanverbs.objects.Verb;

public class TrueFalseGameActivity extends AbstractGameActivity implements View.OnClickListener {
    public static final String LOG_TAG = "TrueFalseGameActivity";
    public static final int RIGHT_ANSWER_PROGRESS = 5;
    public static final int FALSE_ANSWER_PROGRESS = -3;

    public static final int PLAY_TIME = 45;
    public static final int START_SCORE_STEP = 1;

    //Consts for saving recordScore in prefs
    public static final String SHARED_PREF = "prefs";
    public static final String SHARED_PREF_TRUE_FALSE_RECORD = "record";

    private Random random;
    private Question currentQuestion; //Текущий вопрос
    private int score;
    private int scoreStep;
    private int record;
    private TimerAsyncTask time;

    private RelativeLayout relativeLayout;
    private TextView scoreView;
    private TextView multipleView;
    private TextView answerView;
    private TextView chronometerView;
    private RoundCornerProgressBar recordProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Развертка layout
        setContentView(R.layout.true_false_layout);
        addToolbar();

        //Add ads
        layoutWithAdView = (LinearLayout) findViewById(R.id.linear_layout_true_false);

        //Достаем глаголы из интента
        getDataFromIntent();

        score = 0;
        random = new Random(System.currentTimeMillis());
        scoreStep = START_SCORE_STEP;

        relativeLayout = (RelativeLayout) findViewById(R.id.trueFalseLayout);
        scoreView = (TextView) findViewById(R.id.gameTrueFalseScoreView);
        multipleView = (TextView) findViewById(R.id.gameTrueFalseMnozitelView);
        answerView = (TextView) findViewById(R.id.gameTrueFalseAnswerView);
        chronometerView = (TextView) findViewById(R.id.chronometer);
        recordProgressBar = (RoundCornerProgressBar) findViewById(R.id.record_progress_bar);
        TextView recordView = (TextView) findViewById(R.id.textViewRecord);

        record = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
                .getInt(SHARED_PREF_TRUE_FALSE_RECORD, 0);
        recordProgressBar.setMax(record);
        recordProgressBar.setSecondaryProgress(record);
        recordProgressBar.setProgress(0);

        answerView.setTextSize(35);
        answerView.setText(R.string.true_false_start);
        scoreView.setText(getResources().getString(R.string.true_false_score, score));
        multipleView.setText(getResources().getString(R.string.true_false_multiple, scoreStep));
        recordView.setText(getResources().getString(R.string.true_false_record, record));
        chronometerView.setText(String.valueOf(PLAY_TIME));

        relativeLayout.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
        if(time != null) {
            time.cancel(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Проверка если активность запушена после остановки - продолжение игры
        int time_on_start = Integer.parseInt(chronometerView.getText().toString());
        if(time_on_start != Integer.parseInt(String.valueOf(PLAY_TIME))){
            time = new TimerAsyncTask(time_on_start);
            time.execute();
        }
    }

    //Called after user touch screen to start game
    private void startGame() {
        relativeLayout.setOnClickListener(null); //Delete Listener from Layout
        findViewById(R.id.btnNegativeTrueFalseGame).setOnClickListener(this);
        findViewById(R.id.btnPositiveTrueFalseGame).setOnClickListener(this);

        time = new TimerAsyncTask(PLAY_TIME);
        time.execute();

        nextQuestion();
        answerView.setTextSize(20);
    }

    //Called after Time limit
    private void stopGame() {
        //Update record
        if(record < score){
            getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit().putInt(SHARED_PREF_TRUE_FALSE_RECORD, score).commit();
        }
        onStopLesson();
    }

    protected void startResultActivity() {
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        intent.putExtra(ResultActivity.VERBS_INTENT_EXTRA, verbs);
        intent.putExtra(ResultActivity.RESULT_INTENT_EXTRA, results);
        intent.putExtra(ResultActivity.TRUE_FALSE_RECORD_INTENT_EXTRA, record);
        intent.putExtra(ResultActivity.TRUE_FALSE_SCORE_INTENT_EXTRA, score);
        startActivity(intent);
        finish();
    }

    //Called after answer or by startGame
    private void nextQuestion() {
        currentQuestion = getNextQuestion();
        scoreView.setText(getResources().getString(R.string.true_false_score, score));
        multipleView.setText(getResources().getString(R.string.true_false_multiple, scoreStep));
        answerView.setText(currentQuestion.getAnswer());
    }

    //Generate random question
    private Question getNextQuestion() {
        presentVerbNum = random.nextInt(verbs.length);
        int isRight = random.nextInt(2);
        String answer;
        boolean result;
        if(isRight == 1){
            Verb verb = verbs[presentVerbNum];
            answer = verb.getInfinitive() + " - " + verb.getPrateritum() + " - " + verb.getPerfekt();
            result = true;
        }else{
            answer = getWrongAnswerByArrayPosition(presentVerbNum);
            result = false;
        }
        return new Question(answer, result);
    }

    // Return false answer by Vern Nummer in verbs
    private String getWrongAnswerByArrayPosition(int presentVerbNum) {
        Verb verb = verbs[presentVerbNum];
        String ans = verb.getInfinitive() + " - ";
        String[] wrongPrät = new String[]{verb.getPrateritum_1(), verb.getPrateritum_2(), verb.getPrateritum_3()};
        String[] wrongPerfect = new String[]{verb.getPerfekt_1(), verb.getPerfekt_2(), verb.getPerfekt_3()};
        String rightHatIst = verb.getPerfekt().split(" ")[0];
        int vars = 3; //Варианты ошибки, если ist/hat то 2 варианта;
        if(rightHatIst.equals("ist/hat")) vars = 2;
        int rndVarOfMiss = random.nextInt(vars); //В каком месте ошибка
        switch (rndVarOfMiss){
            case 0:
                int rndPrat = random.nextInt(3);
                ans += wrongPrät[rndPrat] + " - " + verb.getPerfekt();
                break;
            case 1:
                int rndPerf = random.nextInt(3);
                ans += verb.getPrateritum() + " - " + rightHatIst + " " + wrongPerfect[rndPerf];
                break;
            case 2:
                String wrongHasIst = (rightHatIst.equals("ist")) ? "hat" : "ist";
                ans += verb.getPrateritum() + " - " + wrongHasIst + " " + verb.getPerfekt().split(" ")[1];
                break;
        }
        return ans;
    }

    // Check an Answer
    private void takeAnswer(boolean answer) {
        if(answer == currentQuestion.getResult()){
            addRightAnswer();
        } else {
            addFalseAnswer();
        }
        nextQuestion();
    }

    //Called after true answer
    private void addRightAnswer() {
        score += scoreStep;
        if(scoreStep != 1){
            scoreStep *= 1.5;
        } else{
            scoreStep = 2;
        }
        results[presentVerbNum].addTrueAnswer();
        results[presentVerbNum].addProgress(RIGHT_ANSWER_PROGRESS);
        recordProgressBar.setProgress(score);
    }

    //Called after false answer
    private void addFalseAnswer() {
        scoreStep = START_SCORE_STEP;
        results[presentVerbNum].addFalseAnswer();
        results[presentVerbNum].addProgress(FALSE_ANSWER_PROGRESS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.trueFalseLayout:
                startGame();
                break;
            case R.id.btnNegativeTrueFalseGame:
                takeAnswer(false);
                break;
            case R.id.btnPositiveTrueFalseGame:
                takeAnswer(true);
                break;
        }
    }

    //Class for timer run
    private class TimerAsyncTask extends AsyncTask<Void, Integer, Void> {
        private int time;

        public TimerAsyncTask(int time) {
            this.time = time;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (time > 0) {
                if (isCancelled()) return null;
                SystemClock.sleep(1000);
                time--;
                publishProgress(time);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            chronometerView.setText(values[0].toString());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stopGame();
        }
    }

    private class Question {
        private String answer;
        private boolean result;


        public Question(String answer, boolean result) {
            this.answer = answer;
            this.result = result;
        }

        public String getAnswer() {
            return answer;
        }

        public boolean getResult() {
            return result;
        }
    }
}
