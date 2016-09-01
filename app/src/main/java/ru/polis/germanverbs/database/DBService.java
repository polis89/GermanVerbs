package ru.polis.germanverbs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import ru.polis.germanverbs.enums.Language;
import ru.polis.germanverbs.objects.Result;
import ru.polis.germanverbs.objects.Verb;

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
                Log.i(LOG_TAG, "Adding: " + line[0]);

                //Если есть 3 форма инфинитив
                if(line[0].contains("(")){
                    String[] tempValues = line[0].split("[\\(\\)]");
                    contentValues.put(DBHelper.TABLE_WORD_KEY_INFINITIVE, tempValues[0]);
                    contentValues.put(DBHelper.TABLE_WORD_KEY_INFINITIVE_3PERSON, tempValues[1]);

                } else {
                    contentValues.put(DBHelper.TABLE_WORD_KEY_INFINITIVE, line[0]);
                }
                contentValues.put(DBHelper.TABLE_WORD_KEY_PRATERITUM, line[1]);
                contentValues.put(DBHelper.TABLE_WORD_KEY_PERFEKT, line[2]);
                contentValues.put(DBHelper.TABLE_WORD_KEY_PROGRESS, 0);
                contentValues.put(DBHelper.TABLE_WORD_KEY_IS_ACTIVE, 1);

                //Add translates to CV
                contentValues.put(Language.ENG.getNameForDB(), line[9]);
                contentValues.put(Language.RUS.getNameForDB(), line[10]);
                contentValues.put(Language.ESP.getNameForDB(), line[11]);
                contentValues.put(Language.FRA.getNameForDB(), line[12]);

                //Write in table WORD
                long word_id = writableDatabase.insert(DBHelper.TABLE_WORD_NAME, null, contentValues);

                //Add wrongAnswers to DB
                contentValues = new ContentValues();
                contentValues.put(DBHelper.TABLE_MISTAKE_KEY_WORD_ID, word_id);
                contentValues.put(DBHelper.TABLE_MISTAKE_KEY_PRATERITUM1, line[3]);
                contentValues.put(DBHelper.TABLE_MISTAKE_KEY_PRATERITUM2, line[4]);
                contentValues.put(DBHelper.TABLE_MISTAKE_KEY_PRATERITUM3, line[5]);
                contentValues.put(DBHelper.TABLE_MISTAKE_KEY_PERFEKT1, line[6]);
                contentValues.put(DBHelper.TABLE_MISTAKE_KEY_PERFEKT2, line[7]);
                contentValues.put(DBHelper.TABLE_MISTAKE_KEY_PERFEKT3, line[8]);

                //Write in table MISTAKES
                writableDatabase.insert(DBHelper.TABLE_MISTAKE_NAME, null, contentValues);
            }
        }
        cursor.close();
        writableDatabase.close();
        dbHelper.close();
    }

    public DBHelper getDBHelper() {
        return dbHelper;
    }

    public Verb[] getRandomVerbs(int verbCount, Language language) throws NotEnoghtVerbsException {
        Verb[] verbs = new Verb[verbCount];
        Cursor cursor = dbHelper.getReadableDatabase().
                rawQuery("SELECT " + DBHelper.TABLE_WORD_NAME + "." + DBHelper.TABLE_WORD_KEY_ID + ", "
                        + DBHelper.TABLE_WORD_KEY_INFINITIVE + ", " +
                        DBHelper.TABLE_WORD_KEY_INFINITIVE_3PERSON + ", " +
                        DBHelper.TABLE_WORD_KEY_PRATERITUM + ", " +
                        DBHelper.TABLE_WORD_KEY_PERFEKT + ", " +
                        language.getNameForDB() + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PRATERITUM1 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PRATERITUM2 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PRATERITUM3 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PERFEKT1 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PERFEKT2 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PERFEKT3 +
                        " FROM " + DBHelper.TABLE_WORD_NAME +
                        " INNER JOIN " + DBHelper.TABLE_MISTAKE_NAME +
                        " ON " + DBHelper.TABLE_WORD_NAME + "." + DBHelper.TABLE_WORD_KEY_ID + " = " + DBHelper.TABLE_MISTAKE_NAME + "." + DBHelper.TABLE_MISTAKE_KEY_WORD_ID +
                        " WHERE " + DBHelper.TABLE_WORD_NAME + "." + DBHelper.TABLE_WORD_KEY_IS_ACTIVE + " = 1", null);
        int countAllVerbs = cursor.getCount();
        if(countAllVerbs < verbCount) throw new NotEnoghtVerbsException();
        Random random = new Random(System.currentTimeMillis());
        HashSet<Integer> setRandomInt = new HashSet<>();
        while(true){
            setRandomInt.add(random.nextInt(countAllVerbs));
            if(setRandomInt.size() == verbCount){break;}
        }
        int index = 0;
        for(int i : setRandomInt){
            cursor.moveToPosition(i);
            int id = cursor.getInt(0);
            String infinitive = cursor.getString(1);
            String infinitive_3_person = cursor.getString(2);
            String prateritum = cursor.getString(3);
            String perfekt = cursor.getString(4);
            String translate = cursor.getString(5);
            String prat_miss_1 = cursor.getString(6);
            String prat_miss_2 = cursor.getString(7);
            String prat_miss_3 = cursor.getString(8);
            String perf_miss_1 = cursor.getString(9);
            String perf_miss_2 = cursor.getString(10);
            String perf_miss_3 = cursor.getString(11);
            Verb verb = new Verb(id, infinitive, infinitive_3_person, prateritum, perfekt, translate, prat_miss_1, prat_miss_2, prat_miss_3, perf_miss_1, perf_miss_2, perf_miss_3);
            verbs[index] = verb;
            index++;
            Log.i(LOG_TAG, "Add Random Verb = " + verb.toString());
        }
        return verbs;
    }

    public void changeIsActive(int verb_id) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = writableDatabase.rawQuery("SELECT " + DBHelper.TABLE_WORD_KEY_IS_ACTIVE +
                " FROM " + DBHelper.TABLE_WORD_NAME +
                " WHERE " + DBHelper.TABLE_WORD_KEY_ID + " = " + verb_id, null);
        cursor.moveToFirst();
        int isActiveInt = cursor.getInt(0);
        int newIsActiveInt = isActiveInt == 1 ? 0 : 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.TABLE_WORD_KEY_IS_ACTIVE, newIsActiveInt);
        writableDatabase.update(DBHelper.TABLE_WORD_NAME, contentValues, DBHelper.TABLE_WORD_KEY_ID + " = " +verb_id, null);
        cursor.close();
        writableDatabase.close();
    }

    public void changeProgress(Verb[] verbs, Result[] results) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        for(int i = 0; i < verbs.length; i++) {
            Cursor cursor = writableDatabase.rawQuery("SELECT " + DBHelper.TABLE_WORD_KEY_PROGRESS +
                    " FROM " + DBHelper.TABLE_WORD_NAME +
                    " WHERE " + DBHelper.TABLE_WORD_KEY_ID + " = " + verbs[i].getId(), null);
            cursor.moveToFirst();
            int progress = cursor.getInt(0);
            int newProgress = progress + results[i].getProgress();
            if(newProgress > 100) { //Отмена изучения глагола при достижении более 100 процентов
                newProgress = 100;
                changeIsActive(verbs[i].getId());
                writableDatabase = dbHelper.getWritableDatabase();
            }
            if(newProgress < 0) newProgress = 0;
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.TABLE_WORD_KEY_PROGRESS, newProgress);
            writableDatabase.update(DBHelper.TABLE_WORD_NAME, contentValues, DBHelper.TABLE_WORD_KEY_ID + " = " + verbs[i].getId(), null);
            cursor.close();
        }
        writableDatabase.close();
    }

    public void resetProgress(){
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.TABLE_WORD_KEY_PROGRESS, 0);
        writableDatabase.update(DBHelper.TABLE_WORD_NAME, contentValues, null, null);
        writableDatabase.close();
    }

    public Verb[] getAllActiveVerbs(Language language) {
        Cursor cursor = dbHelper.getReadableDatabase().
                rawQuery("SELECT " + DBHelper.TABLE_WORD_NAME + "." + DBHelper.TABLE_WORD_KEY_ID + ", "
                        + DBHelper.TABLE_WORD_KEY_INFINITIVE + ", " +
                        DBHelper.TABLE_WORD_KEY_INFINITIVE_3PERSON + ", " +
                        DBHelper.TABLE_WORD_KEY_PRATERITUM + ", " +
                        DBHelper.TABLE_WORD_KEY_PERFEKT + ", " +
                        language.getNameForDB() + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PRATERITUM1 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PRATERITUM2 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PRATERITUM3 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PERFEKT1 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PERFEKT2 + ", " +
                        DBHelper.TABLE_MISTAKE_KEY_PERFEKT3 +
                        " FROM " + DBHelper.TABLE_WORD_NAME +
                        " INNER JOIN " + DBHelper.TABLE_MISTAKE_NAME +
                        " ON " + DBHelper.TABLE_WORD_NAME + "." + DBHelper.TABLE_WORD_KEY_ID + " = " + DBHelper.TABLE_MISTAKE_NAME + "." + DBHelper.TABLE_MISTAKE_KEY_WORD_ID +
                        " WHERE " + DBHelper.TABLE_WORD_NAME + "." + DBHelper.TABLE_WORD_KEY_IS_ACTIVE + " = 1", null);
        int countAllVerbs = cursor.getCount();
        Verb[] verbs = new Verb[countAllVerbs];
        cursor.moveToFirst();
        for(int i = 0; i < verbs.length; i++){
            int id = cursor.getInt(0);
            String infinitive = cursor.getString(1);
            String infinitive_3_person = cursor.getString(2);
            String prateritum = cursor.getString(3);
            String perfekt = cursor.getString(4);
            String translate = cursor.getString(5);
            String prat_miss_1 = cursor.getString(6);
            String prat_miss_2 = cursor.getString(7);
            String prat_miss_3 = cursor.getString(8);
            String perf_miss_1 = cursor.getString(9);
            String perf_miss_2 = cursor.getString(10);
            String perf_miss_3 = cursor.getString(11);
            Verb verb = new Verb(id, infinitive, infinitive_3_person, prateritum, perfekt, translate, prat_miss_1, prat_miss_2, prat_miss_3, perf_miss_1, perf_miss_2, perf_miss_3);
            verbs[i] = verb;
            cursor.moveToNext();
        }
        return verbs;
    }

    public class NotEnoghtVerbsException extends Exception {
    }
}
