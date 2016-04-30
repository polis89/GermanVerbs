package ru.polis.germanverbs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import ru.polis.germanverbs.enums.Language;

/**
 * Service to connect with DB
 * Singleton
 *
 * Created by Dmitrii Polianskii on 27.04.2016.
 */
public class DBService {
    public static final String LOG_TAG = "DBService";
    private static DBService dbService;
    private DBHelper dbHelper;

    public DBService(Context context) {
        dbHelper = new DBHelper(context);
    }

    public static DBService getInstance(Context context){
        if(dbService == null){
            dbService = new DBService(context);
        }
        return dbService;
    }

    public void addWordsInDB(ArrayList<String[]> listOfWords) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        //Проверка есть ли в БД записи
        Cursor cursor = writableDatabase.query(
                DBHelper.TABLE_WORD_NAME,
                new String[]{DBHelper.TABLE_WORD_KEY_INFINITIVE},
                null, null, null, null, null);
        if(!cursor.moveToFirst()) {
            for (String[] line : listOfWords) {
                ContentValues contentValues = new ContentValues();

                //Add word data to CV
                contentValues.put(DBHelper.TABLE_WORD_KEY_INFINITIVE, line[0]);
                contentValues.put(DBHelper.TABLE_WORD_KEY_PRATERITUM, line[1]);
                contentValues.put(DBHelper.TABLE_WORD_KEY_PERFEKT, line[2]);
                contentValues.put(DBHelper.TABLE_WORD_KEY_PROGRESS, 0);
                contentValues.put(DBHelper.TABLE_WORD_KEY_IS_ACTIVE, 1);

                //Add translates to CV
                contentValues.put(Language.ENG.getNameForDB(), line[3]);
                contentValues.put(Language.RUS.getNameForDB(), line[4]);

                //Write in DB
                writableDatabase.insert(DBHelper.TABLE_WORD_NAME, null, contentValues);
                Log.i(LOG_TAG, "Insert word in DB: " + Arrays.toString(line));
            }
        }
        cursor.close();
        writableDatabase.close();
        dbHelper.close();
    }
}
