package ru.polis.germanverbs.games;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import ru.polis.germanverbs.PracticeFragment;
import ru.polis.germanverbs.R;
import ru.polis.germanverbs.objects.Result;
import ru.polis.germanverbs.objects.Verb;

public class TypeWordGameActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {
    public static final String LOG_TAG = "TypeWordGameActivity";
    private static final int TRUE_ANSWER_PROGRESS = 6; //Очки прогресса за правильный ответ
    private static final int FALSE_ANSWER_PROGRESS = -4; //Очки прогресса за не правильный ответ
    private static final int RIGHT_ANSWER_DELAY_MS = 1800;
    private static final String EMPTY_POSITION_STRING = ".....";

//    //Consts for onSaveInstance
    public static final String SAVE_KEY_VERBS = "verbs";
    public static final String SAVE_KEY_RESULTS = "results";
    public static final String SAVE_KEY_PRESENT_VERB_NUM = "presentVerbNum";
    public static final String SAVE_KEY_QUESTION_STRING = "presentVerbNum";

    private Verb[] verbs; //Все глаголы для изучения
    private Result[] results; //Результаты изучения глагола
    private int presentVerbNum; //номер текущего глагола
    private int empty_place;

    private TextView questionTextView;
    private EditText answerEditText;
    private ImageView btnImageView;

    private boolean pause; //Пауза на время отображения правильного ответа

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.type_word_game_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        questionTextView = (TextView) findViewById(R.id.type_word_question_text_view);
        answerEditText = (EditText) findViewById(R.id.type_word_answer_edit_text);
        answerEditText.setOnEditorActionListener(this);
        btnImageView = (ImageView) findViewById(R.id.type_word_answer_button);

        findViewById(R.id.type_word_answer_button).setOnClickListener(this);

        //Show keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        //Достаем глаголы из интента
        Parcelable[] parcelableArrayExtra = getIntent().getParcelableArrayExtra(PracticeFragment.RANDOM_VERB_INTENT_EXTRA);
        verbs = new Verb[parcelableArrayExtra.length];
        results = new Result[parcelableArrayExtra.length];
        for (int i = 0; i < parcelableArrayExtra.length; i++) {
            verbs[i] = (Verb) parcelableArrayExtra[i];
            results[i] = new Result();
        }

        //Перемешивание глаголов
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < verbs.length; i++) {
            int rndInt = random.nextInt(verbs.length);
            Verb temp = verbs[i];
            verbs[i] = verbs[rndInt];
            verbs[rndInt] = temp;
        }

        //Отобразить первый глагол
        nextVerb();

        Log.i(LOG_TAG, "lenght verb = " + verbs.length);
    }

    //Показать следующий глагол
    private void nextVerb() {
        //Проверка есть ли еще глаголы
        btnImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_answer));
        if(presentVerbNum != verbs.length){
            pause = false;
            Random rnd = new Random(System.currentTimeMillis());
            empty_place = rnd.nextInt(3);
            questionTextView.setText(getQuestionString(empty_place));
        } else {
            //Старт активити с результатом
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra(ResultActivity.VERBS_INTENT_EXTRA, verbs);
            intent.putExtra(ResultActivity.RESULT_INTENT_EXTRA, results);
            startActivity(intent);
            finish();
        }
    }

    private String getQuestionString(int empty_place) {
        Verb verb = verbs[presentVerbNum];
        String question = "";
        switch (empty_place){
            case 0:
                question += EMPTY_POSITION_STRING + " - " + verb.getPrateritum() + " - " + verb.getPerfekt();
                break;
            case 1:
                question += verb.getInfinitive() + " - " + EMPTY_POSITION_STRING + " - " + verb.getPerfekt();
                break;
            case 2:
                question += verb.getInfinitive() + " - " + verb.getPrateritum() + " - " + EMPTY_POSITION_STRING;
                break;
        }
        return question;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(!pause) {
            onAnswer();
        }
        return true;
    }

    private void onAnswer() {
        if(answerEditText.getText().toString().equalsIgnoreCase(getShortAnswer())){
            //TRUE
            results[presentVerbNum].addTrueAnswer();
            results[presentVerbNum].addProgress(TRUE_ANSWER_PROGRESS);
            setRightAnswer(true);
        } else {
            //FALSE
            results[presentVerbNum].addFalseAnswer();
            results[presentVerbNum].addProgress(FALSE_ANSWER_PROGRESS);
            setRightAnswer(false);
        }
    }

    private void setRightAnswer(boolean trueAnswer) {
        String shortAnswer = getShortAnswer();
        String str = questionTextView.getText().toString();
        String newStr = str.replace(EMPTY_POSITION_STRING, shortAnswer);
        int start = str.indexOf(EMPTY_POSITION_STRING);
        int end = start + shortAnswer.length();
        Spannable spannable = new SpannableString(newStr);
        if(trueAnswer){
            btnImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_true_answer));
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorTrueAnswer)), start, end,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            btnImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_false_answer));
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.true_false_no)), start, end,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        questionTextView.setText(spannable);

        pause = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                answerEditText.setText("");
                nextVerb();
            }
        }, RIGHT_ANSWER_DELAY_MS);
        presentVerbNum++;
    }

    private String getShortAnswer() {
        switch (empty_place){
            case 0:
                return verbs[presentVerbNum].getInfinitive();
            case 1:
                return verbs[presentVerbNum].getPrateritum();
            case 2:
                return verbs[presentVerbNum].getPerfekt();
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if(!pause) {
            switch (v.getId()) {
                case R.id.type_word_answer_button:
                    onAnswer();
                    break;
            }
        }
    }
}