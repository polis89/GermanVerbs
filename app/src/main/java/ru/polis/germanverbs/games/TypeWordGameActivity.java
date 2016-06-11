package ru.polis.germanverbs.games;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

import ru.polis.germanverbs.PracticeFragment;
import ru.polis.germanverbs.R;
import ru.polis.germanverbs.objects.Result;
import ru.polis.germanverbs.objects.Verb;

public class TypeWordGameActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {
    public static final String LOG_TAG = "TypeWordGameActivity";
    private static final int TRUE_ANSWER_PROGRESS = 6; //Очки прогресса за правильный ответ
    private static final int FALSE_ANSWER_PROGRESS = -1; //Очки прогресса за не правильный ответ
    private static final int DONT_KNOW_PROGRESS = -3; //Очки прогресса за не знание
    private static final int RIGHT_ANSWER_DELAY_MS = 1000;

//    //Consts for onSaveInstance
//    public static final String SAVE_KEY_PRESENT_VERB_NUM = "presentVerbNum";
//    public static final String SAVE_KEY_PRESENT_PART = "presentPart";
//    public static final String SAVE_KEY_VERBS = "verbs";
//    public static final String SAVE_KEY_RESULTS = "results";
//    private static final String SAVE_KEY_VARIANT_1 = "variant1";
//    private static final String SAVE_KEY_VARIANT_2 = "variant2";
//    private static final String SAVE_KEY_VARIANT_3 = "variant3";
//    private static final String SAVE_KEY_VARIANT_4 = "variant4";
//    private static final String SAVE_KEY_ANSWERS = "answers";

    private Verb[] verbs; //Все глаголы для изучения
    private Result[] results; //Результаты изучения глагола
    private int presentVerbNum; //номер текущего глагола
    private int empty_place;

    private TextView questionTextView;
    private EditText answerEditText;
    private CardView answerCardView;
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
        answerCardView = (CardView) findViewById(R.id.answer_card_view);

        findViewById(R.id.type_word_unknown_button).setOnClickListener(this);
        findViewById(R.id.type_word_answer_button).setOnClickListener(this);

        //Show keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        if(savedInstanceState == null) {
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
        } else {
            //Re-create activity
//            presentVerbNum = savedInstanceState.getInt(SAVE_KEY_PRESENT_VERB_NUM);
//            verbs = (Verb[]) savedInstanceState.getParcelableArray(SAVE_KEY_VERBS);
//            results = (Result[]) savedInstanceState.getParcelableArray(SAVE_KEY_RESULTS);
//            var1TextView.setText(savedInstanceState.getString(SAVE_KEY_VARIANT_1));
//            var2TextView.setText(savedInstanceState.getString(SAVE_KEY_VARIANT_2));
//            var3TextView.setText(savedInstanceState.getString(SAVE_KEY_VARIANT_3));
//            var4TextView.setText(savedInstanceState.getString(SAVE_KEY_VARIANT_4));
//            verbFormsTextView.setText(getTextForResultTextView(presentPart - 1));
//            translateTextView.setText(verbs[presentVerbNum].getTranslate());
        }
        Log.i(LOG_TAG, "lenght verb = " + verbs.length);
    }

    //Показать следующий глагол
    private void nextVerb() {
        //Проверка есть ли еще глаголы
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
                question += "..... - " + verb.getPrateritum() + " - " + verb.getPerfekt();
                break;
            case 1:
                question += verb.getInfinitive() + " - ..... - " + verb.getPerfekt();
                break;
            case 2:
                question += verb.getInfinitive() + " - " + verb.getPrateritum() + " - .....";
                break;
        }
        return question;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
//        outState.putInt(SAVE_KEY_PRESENT_VERB_NUM, presentVerbNum);
//        outState.putInt(SAVE_KEY_PRESENT_PART, presentPart);
//        outState.putParcelableArray(SAVE_KEY_VERBS, verbs);
//        outState.putParcelableArray(SAVE_KEY_RESULTS, results);
//        outState.putString(SAVE_KEY_VARIANT_1, var1TextView.getText().toString());
//        outState.putString(SAVE_KEY_VARIANT_2, var2TextView.getText().toString());
//        outState.putString(SAVE_KEY_VARIANT_3, var3TextView.getText().toString());
//        outState.putString(SAVE_KEY_VARIANT_4, var4TextView.getText().toString());
//        outState.putBooleanArray(SAVE_KEY_ANSWERS, answers);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(!pause) {
            onAnswer();
        }
        return true;
    }

    private void onAnswer() {
        if(checkAnswer(answerEditText.getText().toString())){
            //TRUE
            results[presentVerbNum].addTrueAnswer();
            results[presentVerbNum].addProgress(TRUE_ANSWER_PROGRESS);
            setRightAnswer();
        } else {
            //FALSE
            results[presentVerbNum].addFalseAnswer();
            results[presentVerbNum].addProgress(FALSE_ANSWER_PROGRESS);
        }
    }

    private void setRightAnswer() {
        Verb verb = verbs[presentVerbNum];
        questionTextView.setText(verb.getInfinitive() + " - " + verb.getPrateritum() + " - " + verb.getPerfekt());
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

    private void onDontKnow() {
        results[presentVerbNum].addFalseAnswer();
        results[presentVerbNum].addProgress(DONT_KNOW_PROGRESS);
        setRightAnswer();
    }

    private boolean checkAnswer(String s) {
        switch (empty_place){
            case 0:
                return s.equalsIgnoreCase(verbs[presentVerbNum].getInfinitive());
            case 1:
                return s.equalsIgnoreCase(verbs[presentVerbNum].getPrateritum());
            case 2:
                return s.equalsIgnoreCase(verbs[presentVerbNum].getPerfekt());
        }
        return false;
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
                case R.id.type_word_unknown_button:
                    onDontKnow();
                    break;
            }
        }
    }
}