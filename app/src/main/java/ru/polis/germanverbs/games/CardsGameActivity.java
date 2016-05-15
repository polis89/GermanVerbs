package ru.polis.germanverbs.games;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
    private Verb presentVerb; //текущий глагол

    private TextView translateTextView;
    private TextView verbFormsTextView;

    private TextView var1TextView;
    private TextView var2TextView;
    private TextView var3TextView;
    private TextView var4TextView;

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

        //Инициализация слушания ответов
        try {
            findViewById(R.id.variant1_card_view).setOnClickListener(new AnswerListener());
            findViewById(R.id.variant2_card_view).setOnClickListener(new AnswerListener());
            findViewById(R.id.variant3_card_view).setOnClickListener(new AnswerListener());
            findViewById(R.id.variant4_card_view).setOnClickListener(new AnswerListener());
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

        Log.i(LOG_TAG, "first verb = " + verbs[0].getClass().getName());
        Log.i(LOG_TAG, "lenght verb = " + verbs.length);
        //Отобразить первый глагол
        startVerb(0);
    }

    private void startVerb(int verb_num) {
        Log.i(LOG_TAG, "verb is null = " + verbs[verb_num].getInfinitive());
        presentVerb = verbs[verb_num];
        translateTextView.setText(presentVerb.getTranslate());
        verbFormsTextView.setText("");

        //Генерация выриантов ответов
        String[] variants = getInfinitiveVariants();

        //Отображение вариантов в Layout
        var1TextView.setText(variants[0]);
        var2TextView.setText(variants[1]);
        var3TextView.setText(variants[2]);
        var4TextView.setText(variants[3]);
    }

    private String[] getInfinitiveVariants() {
        String[] vars = new String[4];
        vars[0] = presentVerb.getInfinitive(); // Добавление правильного варианта
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
        String[] mixedVars = new String[4];
        for(int i = 0; i < 4;){ //Перемешивание вариантов
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

        }
    }
}
