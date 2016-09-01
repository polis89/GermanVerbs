package ru.polis.germanverbs.games;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.polis.germanverbs.R;

public class FullWordTypeGameActivity extends AbstractGameActivity implements TextView.OnEditorActionListener, View.OnClickListener {
    public static final String LOG_TAG = "FullWordTypeGameActiv";
    private static final int TRUE_ANSWER_PROGRESS = 30; //Очки прогресса за правильный ответ
    private static final int FALSE_ANSWER_PROGRESS = -10; //Очки прогресса за не правильный ответ
    private static final int RIGHT_ANSWER_DELAY_MS = 1200;
    private static final String EMPTY_POSITION_STRING = ".....";

    private int presentPart;
    private boolean pause; //Пауза на время отображения правильного ответа

    private TextView tranlateTextView;
    private TextView questionTextView;
    private EditText answerEditText;
    private ImageView btnImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_word_type_game_layout);
        addToolbar();

        //Add ads
        layoutWithAdView = (LinearLayout) findViewById(R.id.linear_layout_full_type_words);
        loadAd();

        tranlateTextView = (TextView) findViewById(R.id.type_word_translate_text_view);
        questionTextView = (TextView) findViewById(R.id.type_word_question_text_view);
        answerEditText = (EditText) findViewById(R.id.type_word_answer_edit_text);
        answerEditText.setOnEditorActionListener(this);
        btnImageView = (ImageView) findViewById(R.id.type_word_answer_button);

        findViewById(R.id.type_word_answer_button).setOnClickListener(this);

        //Достаем глаголы из интента
        getDataFromIntent();

        //Перемешивание глаголов
        mixVerbs();

        //Отобразить первый глагол
        nextVerb();

        Log.i(LOG_TAG, "lenght verb = " + verbs.length);
    }

    //Показать следующий глагол
    private void nextVerb() {
        //Проверка есть ли еще глаголы
        if(presentVerbNum != verbs.length){
            tranlateTextView.setText(verbs[presentVerbNum].getTranslate());
            presentPart = 1;
            nextPart();
        } else {
            //Старт активити с результатом
            onStopLesson();
        }
    }

    protected void startResultActivity() {
        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        intent.putExtra(ResultActivity.VERBS_INTENT_EXTRA, verbs);
        intent.putExtra(ResultActivity.RESULT_INTENT_EXTRA, results);
        startActivity(intent);
        finish();
    }

    private void nextPart() {
        btnImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_check_answer));
        pause = false;
        switch (presentPart){
            case 1:
                questionTextView.setText(EMPTY_POSITION_STRING + " - " + EMPTY_POSITION_STRING + " - " + EMPTY_POSITION_STRING);
                answerEditText.setHint(R.string.hint_presens);
                return;
            case 2:
                answerEditText.setHint(R.string.hint_prateritum);
                return;
            case 3:
                answerEditText.setHint(R.string.hint_perfect);
                return;
            case 4:
                presentVerbNum++;
                nextVerb();
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void onAnswer() {
        String yourTry = answerEditText.getText().toString();
        String answer = getPresentAnswer();
        int start;
        int end;
        String[] showingString = questionTextView.getText().toString().split(" ");
        if(yourTry.equalsIgnoreCase(answer)){
            //TRUE
            results[presentVerbNum].addTrueAnswer();
            results[presentVerbNum].addProgress(TRUE_ANSWER_PROGRESS);
            btnImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_true_answer));
            switch (presentPart){
                case 1:
                    start = 0;
                    end = verbs[presentVerbNum].getInfinitive().length();
                    showingString[0] = verbs[presentVerbNum].getInfinitive();
                    break;
                case 2:
                    start = verbs[presentVerbNum].getInfinitive().length() + 3;
                    end = start + verbs[presentVerbNum].getPrateritum().length();
                    showingString[2] = verbs[presentVerbNum].getPrateritum();
                    break;
                case 3:
                    start = verbs[presentVerbNum].getInfinitive().length() + verbs[presentVerbNum].getPrateritum().length() + 6;
                    end = start + verbs[presentVerbNum].getPerfekt().length();
                    showingString[4] = verbs[presentVerbNum].getPerfekt();
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            String replaceString = showingString[0] + " " + showingString[1] + " "  + showingString[2] + " "  + showingString[3] + " "  + showingString[4];
            Spannable spannable = new SpannableString(replaceString);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.true_false_yes)), start, end,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            questionTextView.setText(spannable);
        } else {
            //FALSE
            results[presentVerbNum].addFalseAnswer();
            results[presentVerbNum].addProgress(FALSE_ANSWER_PROGRESS);
            btnImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_false_answer));
            switch (presentPart){
                case 1:
                    start = 0;
                    end = verbs[presentVerbNum].getInfinitive().length();
                    showingString[0] = verbs[presentVerbNum].getInfinitive();
                    break;
                case 2:
                    start = verbs[presentVerbNum].getInfinitive().length() + 3;
                    end = start + verbs[presentVerbNum].getPrateritum().length();
                    showingString[2] = verbs[presentVerbNum].getPrateritum();
                    break;
                case 3:
                    start = verbs[presentVerbNum].getInfinitive().length() + verbs[presentVerbNum].getPrateritum().length() + 6;
                    end = start + verbs[presentVerbNum].getPerfekt().length();
                    showingString[4] = verbs[presentVerbNum].getPerfekt();
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            String replaceString = showingString[0] + " " + showingString[1] + " "  + showingString[2] + " "  + showingString[3] + " "  + showingString[4];
            Spannable spannable = new SpannableString(replaceString);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.true_false_no)), start, end,  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            questionTextView.setText(spannable);
        }
        pause = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presentPart++;
                answerEditText.setText("");
                nextPart();
            }
        }, RIGHT_ANSWER_DELAY_MS);
    }

    private String getPresentAnswer() {
        switch (presentPart){
            case 1:
                return verbs[presentVerbNum].getInfinitive();
            case 2:
                return verbs[presentVerbNum].getPrateritum();
            case 3:
                return verbs[presentVerbNum].getPerfekt();
            default:
                throw new UnsupportedOperationException();
        }
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(!pause) {
            onAnswer();
        }
        return true;
    }
}
