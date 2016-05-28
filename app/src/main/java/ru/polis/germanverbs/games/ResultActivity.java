package ru.polis.germanverbs.games;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import ru.polis.germanverbs.MainActivity;
import ru.polis.germanverbs.R;
import ru.polis.germanverbs.database.DBService;
import ru.polis.germanverbs.objects.Result;
import ru.polis.germanverbs.objects.Verb;

/**
 *
 *
 * Created by Dmitrii on 22.05.2016.
 */
public class ResultActivity extends AppCompatActivity {
    private static final int PERFECT_GRENZE = 100;
    private static final int VERY_GOOD_GRENZE = 90;
    private static final int GOOD_GRENZE = 80;
    private static final int NOT_GOOD_GRENZE = 60;
    private static final int BAD_GRENZE = 40;
    private static final int AWFUL_GRENZE = 20;

    public static final String VERBS_INTENT_EXTRA = "verbs_for_result";
    public static final String RESULT_INTENT_EXTRA = "result_extra";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Развертка layout
        setContentView(R.layout.result_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Достаем глаголы и результаты из интента
        Parcelable[] parcelableArrayExtraVerbs = getIntent().getParcelableArrayExtra(VERBS_INTENT_EXTRA);
        Parcelable[] parcelableArrayExtraResult = getIntent().getParcelableArrayExtra(RESULT_INTENT_EXTRA);
        Verb[] verbs = new Verb[parcelableArrayExtraVerbs.length];
        Result[] results = new Result[parcelableArrayExtraVerbs.length];
        for (int i = 0; i < parcelableArrayExtraVerbs.length; i++){
            verbs[i] = (Verb) parcelableArrayExtraVerbs[i];
            results[i] = (Result) parcelableArrayExtraResult[i];
        }

        //Вычисляем количество правильных и не правильных ответов
        int trueAnsCnt = 0;
        int falseAnsCnt = 0;
        for(Result res : results){
            trueAnsCnt += res.getAnswerTrueCount();
            falseAnsCnt += res.getAnswerFalseCount();
        }
        float resultInPercent;
        if(falseAnsCnt + trueAnsCnt == 0){
            resultInPercent = 0;
        }else {
            resultInPercent = (trueAnsCnt * 100) / (falseAnsCnt + trueAnsCnt);
        }

        //Заплонение Вью информацией
        TextView trueAnsTextView = (TextView) findViewById(R.id.rightAnswerTextView);
        TextView falseAnsTextView = (TextView) findViewById(R.id.falseAnswerTextView);
        TextView percentTextView = (TextView) findViewById(R.id.resultPercentAnswerTextView);
        trueAnsTextView.setText(getResources().getString(R.string.true_answer_cnt, trueAnsCnt));
        falseAnsTextView.setText(getResources().getString(R.string.false_answer_cnt, falseAnsCnt));
        percentTextView.setText(getResources().getString(R.string.result_percent, (int)resultInPercent) + "%");

        //Установка значения ProgressBar
        RoundCornerProgressBar progressBar = (RoundCornerProgressBar) findViewById(R.id.resultProgressBar);
        progressBar.setProgress(resultInPercent);

        //Установка значения ТекстВью резтата
        TextView resultTextView = (TextView) findViewById(R.id.resultTextView);
        if(resultInPercent >= PERFECT_GRENZE){
            resultTextView.setText(getResources().getString(R.string.result_perfect));
        } else if (resultInPercent >= VERY_GOOD_GRENZE){
            resultTextView.setText(getResources().getString(R.string.result_very_good));
        } else if (resultInPercent >= GOOD_GRENZE){
            resultTextView.setText(getResources().getString(R.string.result_good));
        } else if (resultInPercent >= NOT_GOOD_GRENZE){
            resultTextView.setText(getResources().getString(R.string.result_not_good));
        } else if (resultInPercent >= BAD_GRENZE){
            resultTextView.setText(getResources().getString(R.string.result_bad));
        } else if (resultInPercent >= AWFUL_GRENZE){
            resultTextView.setText(getResources().getString(R.string.result_very_bad));
        } else {
            resultTextView.setText(getResources().getString(R.string.result_awful));
        }
        new ChangeProgressAsyncTask(DBService.getInstance(getApplicationContext()), verbs, results).execute();
    }

    public void onFabClick(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Класс для изменения прогресса в БД в паралельном потоке
    private class ChangeProgressAsyncTask extends AsyncTask<Void, Void, Void> {
        private DBService dbService;
        private Verb[] verbs;
        private Result[] results;

        public ChangeProgressAsyncTask(DBService dbService, Verb[] verbs, Result[] results) {
            this.dbService = dbService;
            this.verbs = verbs;
            this.results = results;
        }

        @Override
        protected Void doInBackground(Void... params) {
            dbService.changeProgress(verbs, results);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }
}
