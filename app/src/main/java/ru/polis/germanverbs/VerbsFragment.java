package ru.polis.germanverbs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import ru.polis.germanverbs.database.DBHelper;
import ru.polis.germanverbs.database.DBService;
import ru.polis.germanverbs.enums.Language;

/**
 * Fragment to show all Verbs
 *
 * Created by Dmitrii Polianskii on 24.04.2016.
 */
public class VerbsFragment extends ListFragment{
    private static final String LOG_TAG = "ListFragment";
    private Language language;
    private Cursor cursor;
    private SQLiteDatabase sqLiteDatabase;

    public static Fragment getNewInstance() {
        return new VerbsFragment();
    }

    //Метод для установки адаптера глаголов, если первый - запуск то запускается setAdapter() после заполения БД
    public void setAdapter(Context context) {
        Log.i(LOG_TAG, "setAdapter");
        language = ((MainActivity)context).language;
        sqLiteDatabase = DBService.getInstance(context).getDBHelper().getReadableDatabase();
        cursor = sqLiteDatabase.query(DBHelper.TABLE_WORD_NAME, null, null, null, null, null, null);
        setListAdapter(new VerbCursorAdapter(context, cursor, true));
    }

    //Вызов при первом запуске после заполения БД
    public void setAdapter() {
        setAdapter(getActivity());
    }

    public class VerbCursorAdapter extends CursorAdapter {
        private LayoutInflater cursorInflater;

        public VerbCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return cursorInflater.inflate(R.layout.list_item_verb, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.verb_checkbox);
            TextView verbName = (TextView) view.findViewById(R.id.text_verb_name);
            TextView verbDescription = (TextView) view.findViewById(R.id.text_verb_description);
            RoundCornerProgressBar roundCornerProgressBar = (RoundCornerProgressBar) view.findViewById(R.id.verb_progress_bar);

            String name = cursor.getString(cursor.getColumnIndex(DBHelper.TABLE_WORD_KEY_INFINITIVE));
            String translate = " (" + cursor.getString(cursor.getColumnIndex(language.getNameForDB())) + ")";
            name += translate;
            verbName.setText(name);
            int isActive = cursor.getInt(cursor.getColumnIndex(DBHelper.TABLE_WORD_KEY_IS_ACTIVE));
            checkBox.setChecked(isActive == 1);
            String infinitive = cursor.getString(cursor.getColumnIndex(DBHelper.TABLE_WORD_KEY_INFINITIVE));
            String infinitive_3 = cursor.getString(cursor.getColumnIndex(DBHelper.TABLE_WORD_KEY_INFINITIVE_3PERSON));
            if(infinitive_3 != null){
                infinitive += "(" + infinitive_3 + ")";
            }
            String prat = cursor.getString(cursor.getColumnIndex(DBHelper.TABLE_WORD_KEY_PRATERITUM));
            String perfect = cursor.getString(cursor.getColumnIndex(DBHelper.TABLE_WORD_KEY_PERFEKT));
            int progress = cursor.getInt(cursor.getColumnIndex(DBHelper.TABLE_WORD_KEY_PROGRESS));
            String description = infinitive + " - " + prat + " - " + perfect;
            verbDescription.setText(description);
            roundCornerProgressBar.setProgress(progress);

            int verb_id = cursor.getInt(0);
            checkBox.setOnClickListener(new OnCheckBoxTouchListener(verb_id));
        }

        private class OnCheckBoxTouchListener implements View.OnClickListener {
            private int verb_id;

            public OnCheckBoxTouchListener(int position) {
                this.verb_id = position;
            }

            @Override
            public void onClick(View v) {
                new ChangeIsActiveAsyncTask(DBService.getInstance(getActivity()), verb_id).execute();
            }
        }

        //Класс для изменения isActive в БД в паралельном потоке
        private class ChangeIsActiveAsyncTask extends AsyncTask<Void, Void, Void> {
            private DBService dbService;
            private int verb_id; //Id глагола в БД

            public ChangeIsActiveAsyncTask(DBService dbService, int verb_id) {
                this.dbService = dbService;
                this.verb_id = verb_id;
            }

            @Override
            protected Void doInBackground(Void... params) {
                dbService.changeIsActive(verb_id);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                SQLiteDatabase writableDatabase = DBService.getInstance(getActivity()).getDBHelper().getReadableDatabase();
                changeCursor(writableDatabase.query(DBHelper.TABLE_WORD_NAME, null, null, null, null, null, null));
            }
        }
    }
}
