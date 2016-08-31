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
    public static final int DB_VERSION = 4;

    //Названия и поля баз
    public static final String TABLE_WORD_NAME = "word"; //Таблица со словами
    public static final String TABLE_WORD_KEY_ID = "_id";
    public static final String TABLE_WORD_KEY_INFINITIVE = "infinitive";
    public static final String TABLE_WORD_KEY_INFINITIVE_3PERSON = "infinitive_3_person";
    public static final String TABLE_WORD_KEY_PRATERITUM = "prateritum";
    public static final String TABLE_WORD_KEY_PERFEKT = "perfekt";
    public static final String TABLE_WORD_KEY_IS_ACTIVE = "is_active";
    public static final String TABLE_WORD_KEY_PROGRESS = "progress";

    public static final String TABLE_MISTAKE_NAME = "mistake"; //Таблица с ошибками
    public static final String TABLE_MISTAKE_KEY_ID = "_id";
    public static final String TABLE_MISTAKE_KEY_WORD_ID = "word_id";
    public static final String TABLE_MISTAKE_KEY_PRATERITUM1 = "prateritum_1";
    public static final String TABLE_MISTAKE_KEY_PRATERITUM2 = "prateritum_2";
    public static final String TABLE_MISTAKE_KEY_PRATERITUM3 = "prateritum_3";
    public static final String TABLE_MISTAKE_KEY_PERFEKT1 = "perfekt_1";
    public static final String TABLE_MISTAKE_KEY_PERFEKT2 = "perfekt_2";
    public static final String TABLE_MISTAKE_KEY_PERFEKT3 = "perfekt_3";

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
                Language.ESP.getNameForDB() + " text NOT NULL, " +
                Language.FRA.getNameForDB() + " text NOT NULL, " +
                TABLE_WORD_KEY_PROGRESS + " INTEGER, " +
                TABLE_WORD_KEY_IS_ACTIVE + " INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_MISTAKE_NAME + " ( " +
                TABLE_MISTAKE_KEY_ID + " integer primary key, " +
                TABLE_MISTAKE_KEY_WORD_ID + " integer, " +
                TABLE_MISTAKE_KEY_PRATERITUM1 + " text NOT NULL, " +
                TABLE_MISTAKE_KEY_PRATERITUM2 + " text NOT NULL, " +
                TABLE_MISTAKE_KEY_PRATERITUM3 + " text NOT NULL, " +
                TABLE_MISTAKE_KEY_PERFEKT1 + " text NOT NULL, " +
                TABLE_MISTAKE_KEY_PERFEKT2 + " text NOT NULL, " +
                TABLE_MISTAKE_KEY_PERFEKT3 + " text NOT NULL, " +
                "FOREIGN KEY(" + TABLE_MISTAKE_KEY_WORD_ID + ") " +
                "REFERENCES " + TABLE_WORD_NAME + "(" + TABLE_WORD_KEY_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "---Update Database---");
        db.execSQL("drop table if exists " + TABLE_WORD_NAME);
        onCreate(db);
    }
}
