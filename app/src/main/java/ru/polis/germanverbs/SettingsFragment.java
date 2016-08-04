package ru.polis.germanverbs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import ru.polis.germanverbs.database.DBHelper;
import ru.polis.germanverbs.database.DBService;

public class SettingsFragment extends Fragment {
    private TextView cardsNumber;
    private TextView fillGapsNumber;
    private TextView typoNumber;

    public static Fragment getNewInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        view.findViewById(R.id.settings_cards_relative_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog(1);
            }
        });
        view.findViewById(R.id.settings_fill_gaps_relative_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog(2);
            }
        });
        view.findViewById(R.id.settings_typo_relative_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog(3);
            }
        });
        view.findViewById(R.id.settings_language_relative_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showChooseLanguageDialog();
            }
        });
        view.findViewById(R.id.settings_reset_relative_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClearProgressAsyncTask(DBService.getInstance(getActivity())).execute();
            }
        });
        cardsNumber = (TextView)view.findViewById(R.id.settings_cards_number);
        fillGapsNumber = (TextView)view.findViewById(R.id.settings_fill_the_gaps_number);
        typoNumber = (TextView)view.findViewById(R.id.settings_type_words_number);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
        cardsNumber.setText("" + sharedPreferences.getInt(MainActivity.SHARED_PREF_CARDS_GAME_WORD_COUNT, 15));
        fillGapsNumber.setText("" + sharedPreferences.getInt(MainActivity.SHARED_PREF_TYPE_WORD_GAME_WORD_COUNT, 15));
        typoNumber.setText("" + sharedPreferences.getInt(MainActivity.SHARED_PREF_FULL_TYPE_WORD_GAME_WORD_COUNT, 15));
        return view;
    }

    // @param number_of_preference
    // 1 - CardsGame
    // 2 - FillTheGapsGame
    // 3 - TypeWordGame
    private void showNumberPickerDialog(int number_of_preference) {
        int value_of_preference = 0;
        int dialog_title_resours_id = 0;
        String preference_name = "";
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE);
        switch (number_of_preference){
            case 1:
                value_of_preference = sharedPreferences.getInt(MainActivity.SHARED_PREF_CARDS_GAME_WORD_COUNT, 15);
                dialog_title_resours_id = R.string.number_picker_dialog_cards_game;
                preference_name = MainActivity.SHARED_PREF_CARDS_GAME_WORD_COUNT;
                break;
            case 2:
                value_of_preference = sharedPreferences.getInt(MainActivity.SHARED_PREF_TYPE_WORD_GAME_WORD_COUNT, 15);
                dialog_title_resours_id = R.string.number_picker_dialog_fill_the_gaps_game;
                preference_name = MainActivity.SHARED_PREF_TYPE_WORD_GAME_WORD_COUNT;
                break;
            case 3:
                value_of_preference = sharedPreferences.getInt(MainActivity.SHARED_PREF_FULL_TYPE_WORD_GAME_WORD_COUNT, 15);
                dialog_title_resours_id = R.string.number_picker_dialog_type_the_word_game;
                preference_name = MainActivity.SHARED_PREF_FULL_TYPE_WORD_GAME_WORD_COUNT;
                break;
        }
        final int value_of_preference_final = value_of_preference;
        final String preference_name_final = preference_name;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.number_picker_layout, null);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.number_picker);
        if(number_of_preference == 1){
            numberPicker.setMinValue(4);
        } else {
            numberPicker.setMinValue(1);
        }
        numberPicker.setMaxValue(156);
        numberPicker.setValue(value_of_preference);
        builder.setView(dialogView);
        builder.setTitle(dialog_title_resours_id);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int picker_num = numberPicker.getValue();
                if(picker_num != value_of_preference_final){
                    SharedPreferences.Editor edit = getActivity().getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE).edit();
                    edit.putInt(preference_name_final, picker_num);
                    edit.apply();
                    switch (preference_name_final){
                        case MainActivity.SHARED_PREF_CARDS_GAME_WORD_COUNT:
                            cardsNumber.setText("" + picker_num);
                            break;
                        case MainActivity.SHARED_PREF_TYPE_WORD_GAME_WORD_COUNT:
                            fillGapsNumber.setText("" + picker_num);
                            break;
                        case MainActivity.SHARED_PREF_FULL_TYPE_WORD_GAME_WORD_COUNT:
                            typoNumber.setText("" + picker_num);
                            break;
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    //Класс для сброса прогресса в БД в паралельном потоке
    private class ClearProgressAsyncTask extends AsyncTask<Void, Void, Void> {
        private DBService dbService;

        public ClearProgressAsyncTask(DBService dbService) {
            this.dbService = dbService;
        }

        @Override
        protected Void doInBackground(Void... params) {
            dbService.resetProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SQLiteDatabase writableDatabase = DBService.getInstance(getActivity()).getDBHelper().getReadableDatabase();
            VerbsFragment verbsFragment = ((MainActivity) getActivity()).verbsFragment;
            ((CursorAdapter)verbsFragment.getListAdapter()).changeCursor(writableDatabase.query(DBHelper.TABLE_WORD_NAME, null, null, null, null, null, null));
        }
    }
}
