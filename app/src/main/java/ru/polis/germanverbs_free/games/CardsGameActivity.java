package ru.polis.germanverbs_free.games;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import ru.polis.germanverbs_free.R;
import ru.polis.germanverbs_free.objects.Result;
import ru.polis.germanverbs_free.objects.Verb;

/**
 *
 * Created by Dmitrii on 07.05.2016.
 */
public class CardsGameActivity extends AbstractGameActivity{
    public static final String LOG_TAG = "CardsGameActivity";
    private static final int TRUE_ANSWER_PROGRESS = 5; //Очки прогресса за правильный ответ
    private static final int FALSE_ANSWER_PROGRESS = -5; //Очки прогресса за не правильный ответ

    //Consts for onSaveInstance
    public static final String SAVE_KEY_PRESENT_VERB_NUM = "presentVerbNum";
    public static final String SAVE_KEY_PRESENT_PART = "presentPart";
    public static final String SAVE_KEY_VERBS = "verbs";
    public static final String SAVE_KEY_RESULTS = "results";
    private static final String SAVE_KEY_VARIANT_1 = "variant1";
    private static final String SAVE_KEY_VARIANT_2 = "variant2";
    private static final String SAVE_KEY_VARIANT_3 = "variant3";
    private static final String SAVE_KEY_VARIANT_4 = "variant4";
    private static final String SAVE_KEY_ANSWERS = "answers";
    private boolean[] answers; //For saving color of Answers if reCreate

    private int presentPart; //Текущий этап (0 - инфинитив, 1 - претеритум, 2 - has/ist, 3 - перфект, 4 - если все глаголы повторены)

    private TextView translateTextView;
    private TextView verbFormsTextView;

    private TextView var1TextView;
    private TextView var2TextView;
    private TextView var3TextView;
    private TextView var4TextView;

    private CardView var1CardView;
    private CardView var2CardView;
    private CardView var3CardView;
    private CardView var4CardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Развертка layout
        setContentView(R.layout.cards_game_layout);
        addToolbar();

        //Add ads
        layoutWithAdView = (LinearLayout) findViewById(R.id.linear_layout_cards_game);
        loadAd();

        var1CardView = (CardView) findViewById(R.id.variant1_card_view);
        var2CardView = (CardView) findViewById(R.id.variant2_card_view);
        var3CardView = (CardView) findViewById(R.id.variant3_card_view);
        var4CardView = (CardView) findViewById(R.id.variant4_card_view);
        try {
            var1CardView.setOnClickListener(new AnswerListener());
            var2CardView.setOnClickListener(new AnswerListener());
            var3CardView.setOnClickListener(new AnswerListener());
            var4CardView.setOnClickListener(new AnswerListener());
        } catch (NullPointerException e){
            Toast.makeText(this, "ERROR Answer Init", Toast.LENGTH_SHORT).show();
        }
        var1TextView = (TextView) findViewById(R.id.variant1_text_view);
        var2TextView = (TextView) findViewById(R.id.variant2_text_view);
        var3TextView = (TextView) findViewById(R.id.variant3_text_view);
        var4TextView = (TextView) findViewById(R.id.variant4_text_view);
        translateTextView = (TextView) findViewById(R.id.translate_text_view);
        verbFormsTextView = (TextView) findViewById(R.id.verb_formen);
        answers = new boolean[4];

        if(savedInstanceState == null) {
            //Достаем глаголы из интента
            getDataFromIntent();

            //Перемешивание глаголов
            mixVerbs();

            //Отобразить первый глагол
            startVerb();
        } else {
            //Re-create activity
            presentVerbNum = savedInstanceState.getInt(SAVE_KEY_PRESENT_VERB_NUM);
            presentPart = savedInstanceState.getInt(SAVE_KEY_PRESENT_PART);
            verbs = (Verb[]) savedInstanceState.getParcelableArray(SAVE_KEY_VERBS);
            results = (Result[]) savedInstanceState.getParcelableArray(SAVE_KEY_RESULTS);
            var1TextView.setText(savedInstanceState.getString(SAVE_KEY_VARIANT_1));
            var2TextView.setText(savedInstanceState.getString(SAVE_KEY_VARIANT_2));
            var3TextView.setText(savedInstanceState.getString(SAVE_KEY_VARIANT_3));
            var4TextView.setText(savedInstanceState.getString(SAVE_KEY_VARIANT_4));
            verbFormsTextView.setText(getTextForResultTextView(presentPart - 1));
            translateTextView.setText(verbs[presentVerbNum].getTranslate());
            answers = savedInstanceState.getBooleanArray(SAVE_KEY_ANSWERS);
            setColors();
        }

        Log.i(LOG_TAG, "lenght verb = " + verbs.length);
    }

    private void setColors() {
        int colorFalse = getResources().getColor(R.color.colorFalseAnswer);
        for(int i = 0; i < 4; i++){
            if (answers[i]){
                switch (i){
                    case 0:
                        var1CardView.setCardBackgroundColor(colorFalse);
                        break;
                    case 1:
                        var2CardView.setCardBackgroundColor(colorFalse);
                        break;
                    case 2:
                        var3CardView.setCardBackgroundColor(colorFalse);
                        break;
                    case 3:
                        var4CardView.setCardBackgroundColor(colorFalse);
                        break;
                }
            }
        }
    }

    //Показать следующий глагол
    private void startVerb() {
        answers = new boolean[4];
        if(presentVerbNum != verbs.length){ //Проверка есть ли еще глаголы
            int colorWhite = getResources().getColor(R.color.colorTextOrIcons); //Бедый цвет для не нажатых вариантов
            var1CardView.setCardBackgroundColor(colorWhite);
            var2CardView.setCardBackgroundColor(colorWhite);
            var3CardView.setCardBackgroundColor(colorWhite);
            var4CardView.setCardBackgroundColor(colorWhite);

            translateTextView.setText(verbs[presentVerbNum].getTranslate());
            verbFormsTextView.setText("");

            //Генерация выриантов ответов
            String[] variants = getInfinitiveVariants();

            //Отображение вариантов в Layout
            var1TextView.setText(variants[0]);
            var2TextView.setText(variants[1]);
            var3TextView.setText(variants[2]);
            var4TextView.setText(variants[3]);

            //Отметка текущего этапа
            presentPart = 0;
        } else {
            presentPart++;

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

    //Показать следущий этап (Для нового глагола - startVerb)
    private void showNextPart() {
        answers = new boolean[4];
        int colorWhite = getResources().getColor(R.color.colorTextOrIcons); //Бедый цвет для не нажатых вариантов
        String[] variants = new String[4]; //Варианты ответа
        String[] mixVars; //перемешанные варианты
        switch (presentPart){
            case 0:
                var1CardView.setCardBackgroundColor(colorWhite);
                var2CardView.setCardBackgroundColor(colorWhite);
                var3CardView.setCardBackgroundColor(colorWhite);
                var4CardView.setCardBackgroundColor(colorWhite);
                verbFormsTextView.setText(getTextForResultTextView(presentPart));
                variants[0] = verbs[presentVerbNum].getPrateritum(); //Добавление правильного варианта
                variants[1] = verbs[presentVerbNum].getPrateritum_1(); //Добавление не правильного варианта
                variants[2] = verbs[presentVerbNum].getPrateritum_2(); //Добавление не правильного варианта
                variants[3] = verbs[presentVerbNum].getPrateritum_3(); //Добавление не правильного варианта
                mixVars = mixStrings(variants); //перетасовка вариантов
                var1TextView.setText(mixVars[0]);//Установка вариантов в Текст Вьюс
                var2TextView.setText(mixVars[1]);
                var3TextView.setText(mixVars[2]);
                var4TextView.setText(mixVars[3]);
                presentPart++; // Изменение текущего этапа
                break;
            case 1:
                var1CardView.setCardBackgroundColor(colorWhite);
                var2CardView.setCardBackgroundColor(colorWhite);
                var3CardView.setCardBackgroundColor(colorWhite);
                var4CardView.setCardBackgroundColor(colorWhite);
                verbFormsTextView.setText(getTextForResultTextView(presentPart));
                variants[0] = "ist";
                variants[1] = "hat";
                variants[2] = "-";
                variants[3] = "ist/hat";
                var1TextView.setText(variants[0]);//Установка вариантов в Текст Вьюс
                var2TextView.setText(variants[1]);
                var3TextView.setText(variants[2]);
                var4TextView.setText(variants[3]);
                presentPart++; // Изменение текущего этапа
                break;
            case 2:
                var1CardView.setCardBackgroundColor(colorWhite);
                var2CardView.setCardBackgroundColor(colorWhite);
                var3CardView.setCardBackgroundColor(colorWhite);
                var4CardView.setCardBackgroundColor(colorWhite);
                verbFormsTextView.setText(getTextForResultTextView(presentPart));
                variants[0] = verbs[presentVerbNum].getPerfekt().split(" ")[1]; //Добавление правильного варианта
                variants[1] = verbs[presentVerbNum].getPerfekt_1(); //Добавление не правильного варианта
                variants[2] = verbs[presentVerbNum].getPerfekt_2(); //Добавление не правильного варианта
                variants[3] = verbs[presentVerbNum].getPerfekt_3(); //Добавление не правильного варианта
                mixVars = mixStrings(variants); //перетасовка вариантов
                var1TextView.setText(mixVars[0]);//Установка вариантов в Текст Вьюс
                var2TextView.setText(mixVars[1]);
                var3TextView.setText(mixVars[2]);
                var4TextView.setText(mixVars[3]);
                presentPart++; // Изменение текущего этапа
                break;
            case 3:
                presentVerbNum++;
                startVerb();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private String getTextForResultTextView(int presentPart) {
        switch (presentPart){
            case 0:
                return verbs[presentVerbNum].getInfinitive();
            case 1:
                return verbs[presentVerbNum].getInfinitive() + " - " + verbs[presentVerbNum].getPrateritum();
            case 2:
                return verbs[presentVerbNum].getInfinitive() + " - " + verbs[presentVerbNum].getPrateritum() + " - " + verbs[presentVerbNum].getPerfekt().split(" ")[0];
            case 3:
                return (verbs[presentVerbNum].getInfinitive() + " - " +
                        verbs[presentVerbNum].getPrateritum() + " - " +
                        verbs[presentVerbNum].getPerfekt().split(" ")[0] + " " +
                        verbs[presentVerbNum].getPerfekt().split(" ")[1]);
        }
        return "";
    }

    private String[] getInfinitiveVariants() {
        String[] vars = new String[4];
        vars[0] = verbs[presentVerbNum].getInfinitive(); // Добавление правильного варианта
        Random random = new Random(System.currentTimeMillis());
        for(int i = 1; i < 4;) {//добавление трех не правильных вариантов
            Verb rndVerb = verbs[random.nextInt(verbs.length)];
            boolean passt = true;
            for(int j = 0; j < i; j++){ //Проверка если такой вариант уже добавлен
                if(vars[j].equals(rndVerb.getInfinitive())){
                    passt = false;
                    break;
                }
            }
            if(passt){
                vars[i] = rndVerb.getInfinitive();
                i++;
            }
        }
        return mixStrings(vars);
    }

    //Перемешивает строкив массиве
    private String[] mixStrings(String[] vars) {
        String[] mixedVars = new String[vars.length];
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < mixedVars.length;){ //Перемешивание вариантов
            int rndInt = random.nextInt(mixedVars.length);
            if(!vars[rndInt].equals("EMPTY")){
                mixedVars[i] = vars[rndInt];
                vars[rndInt] = "EMPTY";
                i++;
            }
        }
        return mixedVars;
    }

    public void setFalseAnswerArray(int falseAnswerCardViewID) {
        if(falseAnswerCardViewID == var1CardView.getId()){
            answers[0] = true;
        }else if(falseAnswerCardViewID == var2CardView.getId()){
            answers[1] = true;
        }else if(falseAnswerCardViewID == var3CardView.getId()){
            answers[2] = true;
        }else if(falseAnswerCardViewID == var4CardView.getId()){
            answers[3] = true;
        }
    }

    private class AnswerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextView answer; //Нажатое текст вью
            switch (v.getId()) {
                case R.id.variant1_card_view:
                    answer = var1TextView;
                    break;
                case R.id.variant2_card_view:
                    answer = var2TextView;
                    break;
                case R.id.variant3_card_view:
                    answer = var3TextView;
                    break;
                case R.id.variant4_card_view:
                    answer = var4TextView;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            if(presentPart == 4) {
                return; //Срабатывает если все глаголы повторены
            }
            boolean result = checkResult(answer.getText().toString());
            CardView cardAnswerView = (CardView) v;
            if (result) {
                results[presentVerbNum].addTrueAnswer();
                results[presentVerbNum].addProgress(TRUE_ANSWER_PROGRESS);
                cardAnswerView.setCardBackgroundColor(getResources().getColor(R.color.colorTrueAnswer));
                if(presentPart == 3){ //Перед переходом к следующему глаголу показывает правильное спряжение
                    getTextForResultTextView(presentPart);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showNextPart();
                    }
                }, 500);
            } else {
                results[presentVerbNum].addFalseAnswer();
                results[presentVerbNum].addProgress(FALSE_ANSWER_PROGRESS);
                setFalseAnswerArray(cardAnswerView.getId());
                cardAnswerView.setCardBackgroundColor(getResources().getColor(R.color.colorFalseAnswer));
            }
        }

        //Метод для проверки ответа
        private boolean checkResult(String answer) {
            String rightAnswer = null;
            switch (presentPart) {
                case 0:
                    rightAnswer = verbs[presentVerbNum].getInfinitive();
                    break;
                case 1:
                    rightAnswer = verbs[presentVerbNum].getPrateritum();
                    break;
                case 2:
                    rightAnswer = verbs[presentVerbNum].getPerfekt().split(" ")[0];
                    break;
                case 3:
                    rightAnswer = verbs[presentVerbNum].getPerfekt().split(" ")[1];
                    break;
            }
            return answer.equals(rightAnswer);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_KEY_PRESENT_VERB_NUM, presentVerbNum);
        outState.putInt(SAVE_KEY_PRESENT_PART, presentPart);
        outState.putParcelableArray(SAVE_KEY_VERBS, verbs);
        outState.putParcelableArray(SAVE_KEY_RESULTS, results);
        outState.putString(SAVE_KEY_VARIANT_1, var1TextView.getText().toString());
        outState.putString(SAVE_KEY_VARIANT_2, var2TextView.getText().toString());
        outState.putString(SAVE_KEY_VARIANT_3, var3TextView.getText().toString());
        outState.putString(SAVE_KEY_VARIANT_4, var4TextView.getText().toString());
        outState.putBooleanArray(SAVE_KEY_ANSWERS, answers);
    }
}
