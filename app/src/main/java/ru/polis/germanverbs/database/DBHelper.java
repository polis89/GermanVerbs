package ru.polis.germanverbs.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.polis.germanverbs.enums.Language;

/**
 * To open DB
 *
 * Created by Dmitrii Polianskii on 27.04.2016.
 */
public class DBHelper extends SQLiteOpenHelper{
    public static final String TAG = "DBHelper";
    public static final String DATA_BASE_NAME_FULL = "wordsDB.sqlite";
    public static final int DB_VERSION = 3;

    //Названия и поля баз
    public static final String TABLE_WORD_NAME = "word";
    public static final String TABLE_WORD_KEY_ID = "_id";
    public static final String TABLE_WORD_KEY_INFINITIVE = "infinitive";
    public static final String TABLE_WORD_KEY_INFINITIVE_3PERSON = "infinitive_3_person";
    public static final String TABLE_WORD_KEY_PRATERITUM = "prateritum";
    public static final String TABLE_WORD_KEY_PERFEKT = "perfekt";
    public static final String TABLE_WORD_KEY_IS_ACTIVE = "is_active";
    public static final String TABLE_WORD_KEY_PROGRESS = "progress";

    public DBHelper(Context context) {
        super(context, DATA_BASE_NAME_FULL, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "---Create Database---");
        db.execSQL("CREATE TABLE " + TABLE_WORD_NAME + " ( " +
                TABLE_WORD_KEY_ID + " integer primary key, " +
                TABLE_WORD_KEY_INFINITIVE + " text UNIQUE NOT NULL, " +
                TABLE_WORD_KEY_INFINITIVE_3PERSON + " text, " +
                TABLE_WORD_KEY_PRATERITUM + " text NOT NULL, " +
                TABLE_WORD_KEY_PERFEKT + " text NOT NULL, " +
                Language.RUS.getNameForDB() + " text NOT NULL, " +
                Language.ENG.getNameForDB() + " text NOT NULL, " +
                TABLE_WORD_KEY_PROGRESS + " INTEGER, " +
                TABLE_WORD_KEY_IS_ACTIVE + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "---Update Database---");
        db.execSQL("drop table if exists " + TABLE_WORD_NAME);
        onCreate(db);
    }
}
