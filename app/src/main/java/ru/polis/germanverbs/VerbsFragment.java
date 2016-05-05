package ru.polis.germanverbs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

import ru.polis.germanverbs.database.DBHelper;
import ru.polis.germanverbs.database.DBService;

/**
 * Fragment to show all Verbs
 *
 * Created by Dmitrii Polianskii on 24.04.2016.
 */
public class VerbsFragment extends ListFragment {
    private static final String LOG_TAG = "ListFragment";
    private static VerbsFragment instanse;

    public static Fragment getInstance() {
        if(instanse == null){
            instanse = new VerbsFragment();
        }
        return instanse;
    }

    public static Fragment getNewInstance() {
        return new VerbsFragment();
    }

    //Метод для установки адаптера глаголов, если первый - запуск то запускается setAdapter() после заполения БД
    public void setAdapter(Context context) {
        Log.i(LOG_TAG, "setAdapter");
        SQLiteDatabase writableDatabase = DBService.getInstance(context).getDBHelper().getReadableDatabase();
        Cursor cursor = writableDatabase.query(DBHelper.TABLE_WORD_NAME, null, null, null, null, null, null);
        setListAdapter(new SimpleCursorAdapter(
                context,
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{DBHelper.TABLE_WORD_KEY_INFINITIVE},
                new int[]{android.R.id.text1},
                0));
    }

    //Вызов при первом запуске после заполения БД
    public void setAdapter() {
        setAdapter(getActivity());
    }
}
