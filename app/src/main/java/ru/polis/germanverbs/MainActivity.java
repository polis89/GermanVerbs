package ru.polis.germanverbs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ru.polis.germanverbs.database.DBService;
import ru.polis.germanverbs.enums.Language;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "MainActivityLog";

    public static final String SHARED_PREF = "prefs"; //имя SharedPrefs
    public static final String SHARED_PREF_LANGUAGE_TAG = "language"; //Переменная языка в SharedPrefs - возвращает по-типу Locale
    public static final String SHARED_PREF_FIRST_LAUNCH_TAG = "first_launch"; //Переменная для определения первого запуска в SharedPrefs
    public static final String SHARED_PREF_CARDS_GAME_WORD_COUNT = "cards_game_word_count"; //Количество слов для изучения в игре-карточки
    public static final String SHARED_PREF_TYPE_WORD_GAME_WORD_COUNT = "type_word_game_word_count";
    public static final String SHARED_PREF_FULL_TYPE_WORD_GAME_WORD_COUNT = "full_type_word_game_word_count";

    public Language language;
    private BottomBar bottomBar;
    private MenuItem searchMenuItem;

    private  VerbsFragment verbsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Проверка на первый запуск
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean(SHARED_PREF_FIRST_LAUNCH_TAG, true);

        //Init verbsFragment
        verbsFragment = (VerbsFragment) VerbsFragment.getNewInstance();

        if(firstStart){
            firstStart();
        } else {
            String langString = sharedPreferences.getString(SHARED_PREF_LANGUAGE_TAG, "en");
            Log.i(LOG_TAG, "Lang = " + langString);
            language = Language.getLanguageByLocale(langString);
        }

        //Развертка layout
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init ViewPager
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                switch (position){
                    case 0:
                        fragment = PracticeFragment.getNewInstance();
                        break;
                    case 1:
                        fragment = verbsFragment;
                        break;
                    case 2:
                        fragment = SettingsFragment.getNewInstance();
                        break;
                    default:
                        fragment = PracticeFragment.getNewInstance();
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        final ViewPager pagerMain = (ViewPager) findViewById(R.id.pagerMain);
        assert pagerMain != null;
        pagerMain.setAdapter(fragmentPagerAdapter);

        bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.noTopOffset();

        pagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                bottomBar.selectTabAtPosition(position, true);
                if(position == 1){
                    if(searchMenuItem != null) {
                        searchMenuItem.setVisible(true);
                    }
                }else{
                    searchMenuItem.setVisible(false);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Bind BottomBar with ViewPager
        bottomBar.setItemsFromMenu(R.menu.menu_bottom_bar, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.bb_menu_practice:
                        pagerMain.setCurrentItem(0);
                        break;
                    case R.id.bb_menu_verbs:
                        pagerMain.setCurrentItem(1);
                        break;
                    case R.id.bb_menu_settings:
                        pagerMain.setCurrentItem(2);
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
            }
        });
        if(!firstStart){
            verbsFragment.setAdapter(this);
        }
    }

    private void firstStart() {
        Log.i(LOG_TAG, "first start");
        //Извлечение списка слов и открытие DBService для FillDBAsyncTask
        try {
            InputStream inStream = getAssets().open("words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            ArrayList<String[]> listOfWords = new ArrayList<>();
            String line;
            while((line = reader.readLine()) != null){
                listOfWords.add(line.split("-"));
            }
            new FillDBAsyncTask(DBService.getInstance(this), listOfWords).execute(); //Заполнение ДБ в паралельном потоке
        } catch (IOException e) {
            Log.e(LOG_TAG, "can't open words.txt");
        }

        //Установка языка на выбранный в системе
        String langLocale = getResources().getConfiguration().locale.getLanguage();
        if(langLocale == null) langLocale = "en"; //Если нет установленного языка - выбор английского
        language = Language.getLanguageByLocale(langLocale);
        SharedPreferences.Editor edit = getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit();
        //Запись языка в SP
        edit.putString(SHARED_PREF_LANGUAGE_TAG, langLocale);
        //Запись в SharedPrefs настроек по-умолчанию
        edit.putInt(SHARED_PREF_CARDS_GAME_WORD_COUNT, 4);
        edit.putInt(SHARED_PREF_TYPE_WORD_GAME_WORD_COUNT, 4);
        //Запись в SharedPrefs что первый старт уже был
        edit.putBoolean(SHARED_PREF_FIRST_LAUNCH_TAG, false).apply();
    }

    private void showChooseLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_choose_language, null);
        final RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.chooseLanguageRadioGroup);
        for(Language l : Language.values()){
            RadioButton radioButtonView = new RadioButton(this);
            Drawable image = getResources().getDrawable(l.getImageResID());
            int h = image.getIntrinsicHeight();
            int w = image.getIntrinsicWidth();
            image.setBounds( 0, 0, w, h );
            radioButtonView.setCompoundDrawables( image, null, null, null );
            radioButtonView.setCompoundDrawablePadding(16);
            radioButtonView.setText(l.getDescription());
            radioGroup.addView(radioButtonView);
            if(l == language) {
                int indexOfChild = radioGroup.indexOfChild(radioButtonView) + 1;
                radioGroup.check(indexOfChild);
            }
        }
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = (radioGroup.getCheckedRadioButtonId() - 1) % Language.values().length;
                Toast.makeText(getApplicationContext(), "checked: " + id, Toast.LENGTH_SHORT).show();
                Language newLanguage = Language.values()[id];
                if(!newLanguage.equals(language)) {
                    SharedPreferences preference = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString(SHARED_PREF_LANGUAGE_TAG, newLanguage.getLocale());
                    editor.apply();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setTitle(R.string.chooseLanguageTitle);
        builder.create();
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem languageMenuItem = menu.getItem(1);
        languageMenuItem.setIcon(language.getImageResID());
        searchMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_language) {
            showChooseLanguageDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        bottomBar.onSaveInstanceState(outState);
    }

    //Класс для заполнения ДБ в паралельном потоке
    private class FillDBAsyncTask extends AsyncTask<Void, Void, Void>{
        private DBService dbService;
        private ArrayList<String[]> listOfWords;//Строчки для добавления в ДБ

        public FillDBAsyncTask(DBService dbService, ArrayList<String[]> listOfWords) {
            this.dbService = dbService;
            this.listOfWords = listOfWords;
        }

        @Override
        protected Void doInBackground(Void... params) {
            dbService.addWordsInDB(listOfWords);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            verbsFragment.setAdapter();
        }
    }
}
