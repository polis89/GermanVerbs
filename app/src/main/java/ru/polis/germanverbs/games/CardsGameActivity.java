package ru.polis.germanverbs.games;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import ru.polis.germanverbs.PracticeFragment;
import ru.polis.germanverbs.R;
import ru.polis.germanverbs.objects.Verb;

/**
 *
 * Created by Dmitrii on 07.05.2016.
 */
public class CardsGameActivity extends AppCompatActivity{
    public static final String LOG_TAG = "CardsGameActivity";

    private Verb[] verbs; //Все глаголы для изучения
    private int presentVerbNum; //номер текущего глагола
    private int presentPart; //Текущий этап (0 - инфинитив, 1 - претеритум, 2 - has/ist, 3 - перфект)

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Достаем глаголы из интента
        Parcelable[] parcelableArrayExtra = getIntent().getParcelableArrayExtra(PracticeFragment.RANDOM_VERB_INTENT_EXTRA);
        verbs = new Verb[parcelableArrayExtra.length];
        for (int i = 0; i < parcelableArrayExtra.length; i++){
            verbs[i] = (Verb) parcelableArrayExtra[i];
        }

        //Перемешивание глаголов
        Random random = new Random(System.currentTimeMillis());
        for(int i = 0; i < verbs.length; i++){
            int rndInt = random.nextInt(verbs.length);
            Verb temp = verbs[i];
            verbs[i] = verbs[rndInt];
            verbs[rndInt] = temp;
        }

        //Инициализация кардвью
        var1CardView = (CardView) findViewById(R.id.variant1_card_view);
        var2CardView = (CardView) findViewById(R.id.variant2_card_view);
        var3CardView = (CardView) findViewById(R.id.variant3_card_view);
        var4CardView = (CardView) findViewById(R.id.variant4_card_view);

        //Инициализация слушания ответов
        try {
            var1CardView.setOnClickListener(new AnswerListener());
            var2CardView.setOnClickListener(new AnswerListener());
            var3CardView.setOnClickListener(new AnswerListener());
            var4CardView.setOnClickListener(new AnswerListener());
        } catch (NullPointerException e){
            Toast.makeText(this, "ERROR Answer Init", Toast.LENGTH_SHORT).show();
        }

        //Инициализация текствью
        var1TextView = (TextView) findViewById(R.id.variant1_text_view);
        var2TextView = (TextView) findViewById(R.id.variant2_text_view);
        var3TextView = (TextView) findViewById(R.id.variant3_text_view);
        var4TextView = (TextView) findViewById(R.id.variant4_text_view);
        translateTextView = (TextView) findViewById(R.id.translate_text_view);
        verbFormsTextView = (TextView) findViewById(R.id.verb_formen);

        Log.i(LOG_TAG, "lenght verb = " + verbs.length);
        //Отобразить первый глагол
        startVerb();
    }

    //Показать следующий глагол
    private void startVerb() {
        if(presentVerbNum == verbs.length - 1){
            //Старт активити с результатом
        }
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
    }

    //Показать следущий этап (Для нового глагола - startVerb)
    private void showNextPart() {
        int colorWhite = getResources().getColor(R.color.colorTextOrIcons); //Бедый цвет для не нажатых вариантов
        String[] variants = new String[4]; //Варианты ответа
        String[] mixVars; //перемешанные варианты
        switch (presentPart){
            case 0:
                var1CardView.setCardBackgroundColor(colorWhite);
                var2CardView.setCardBackgroundColor(colorWhite);
                var3CardView.setCardBackgroundColor(colorWhite);
                var4CardView.setCardBackgroundColor(colorWhite);
                verbFormsTextView.setText(verbs[presentVerbNum].getInfinitive());
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
                verbFormsTextView.setText(verbs[presentVerbNum].getInfinitive() + " - " + verbs[presentVerbNum].getPrateritum());
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
                verbFormsTextView.setText(verbs[presentVerbNum].getInfinitive() + " - " + verbs[presentVerbNum].getPrateritum() + " - " + verbs[presentVerbNum].getPerfekt().split(" ")[0]);
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
            boolean result = checkResult(answer.getText().toString());
            CardView cardAnswerView = (CardView) v;
            if (result) {
                cardAnswerView.setCardBackgroundColor(getResources().getColor(R.color.colorTrueAnswer));
                if(presentPart == 3){
                    verbFormsTextView.setText(verbs[presentVerbNum].getInfinitive() + " - "
                            + verbs[presentVerbNum].getPrateritum() + " - "
                            + verbs[presentVerbNum].getPerfekt().split(" ")[0] + " "
                            + verbs[presentVerbNum].getPerfekt().split(" ")[1]);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showNextPart();
                    }
                }, 500);
            } else {
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
}
