package ru.polis.germanverbs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import ru.polis.germanverbs.database.DBService;
import ru.polis.germanverbs.games.CardsGameActivity;
import ru.polis.germanverbs.games.FullWordTypeGameActivity;
import ru.polis.germanverbs.games.TrueFalseGameActivity;
import ru.polis.germanverbs.games.TypeWordGameActivity;
import ru.polis.germanverbs.objects.Verb;

/**
 * Fragment to show all type of Practices (Games)
 *
 * Created by Dmitrii Polianskii on 24.04.2016.
 */
public class PracticeFragment extends Fragment{
    public static final String LOG_TAG = "PracticeFragment";
    public static final String RANDOM_VERB_INTENT_EXTRA = "random_verbs";

    public static Fragment getNewInstance() {
        Log.i(LOG_TAG, "getNewInstance");
        return new PracticeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate Fragment
        View view = inflater.inflate(R.layout.fragment_practice, container, false);

        //Add listener to CardViews and InfoButtons
        GameOnClickListener onClickListener = new GameOnClickListener();

        CardView viewGame1 = (CardView) view.findViewById(R.id.cards_game_view);
        CardView viewGame2 = (CardView) view.findViewById(R.id.card_view_true_false_game);
        CardView viewGame3 = (CardView) view.findViewById(R.id.card_view_type_word);
        CardView viewGame4 = (CardView) view.findViewById(R.id.card_view_game_type_full_words);

        ImageView infoView1 = (ImageView) view.findViewById(R.id.help_cards_game);
        ImageView infoView2 = (ImageView) view.findViewById(R.id.help_true_false_game);
        ImageView infoView3 = (ImageView) view.findViewById(R.id.help_game_3);
        ImageView infoView4 = (ImageView) view.findViewById(R.id.help_game_4);

        viewGame1.setOnClickListener(onClickListener);
        viewGame2.setOnClickListener(onClickListener);
        viewGame3.setOnClickListener(onClickListener);
        viewGame4.setOnClickListener(onClickListener);

        infoView1.setOnClickListener(onClickListener);
        infoView2.setOnClickListener(onClickListener);
        infoView3.setOnClickListener(onClickListener);
        infoView4.setOnClickListener(onClickListener);
        return view;
    }

    private class GameOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            FragmentActivity activity = getActivity();
            DBService dbService = DBService.getInstance(activity);
            Intent intent;
            int verbCount;
            Verb[] randomVerbs;
            switch (v.getId()){
                case R.id.cards_game_view:
                    intent = new Intent(activity, CardsGameActivity.class);
                    verbCount = activity.getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE)
                            .getInt(MainActivity.SHARED_PREF_CARDS_GAME_WORD_COUNT, 4);
                    try {
                        randomVerbs = dbService.getRandomVerbs(verbCount, ((MainActivity)activity).language);
                        intent.putExtra(RANDOM_VERB_INTENT_EXTRA, randomVerbs);
                        startActivity(intent);
                    } catch (DBService.NotEnoghtVerbsException e) {
                        Toast.makeText(activity, getString(R.string.not_enought_verb_message), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.card_view_true_false_game:
                    intent = new Intent(activity, TrueFalseGameActivity.class);
                    Verb[] allActiveVerbs;
                    allActiveVerbs = dbService.getAllActiveVerbs(((MainActivity)activity).language);
                    intent.putExtra(RANDOM_VERB_INTENT_EXTRA, allActiveVerbs);
                    startActivity(intent);
                    break;
                case R.id.card_view_type_word:
                    intent = new Intent(activity, TypeWordGameActivity.class);
                    verbCount = activity.getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE)
                            .getInt(MainActivity.SHARED_PREF_TYPE_WORD_GAME_WORD_COUNT, 4);
                    try {
                        randomVerbs = dbService.getRandomVerbs(verbCount, ((MainActivity)activity).language);
                        intent.putExtra(RANDOM_VERB_INTENT_EXTRA, randomVerbs);
                        startActivity(intent);
                    } catch (DBService.NotEnoghtVerbsException e) {
                        Toast.makeText(activity, getString(R.string.not_enought_verb_message), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.card_view_game_type_full_words:
                    intent = new Intent(activity, FullWordTypeGameActivity.class);
                    verbCount = activity.getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE)
                            .getInt(MainActivity.SHARED_PREF_FULL_TYPE_WORD_GAME_WORD_COUNT, 4);
                    try {
                        randomVerbs = dbService.getRandomVerbs(verbCount, ((MainActivity)activity).language);
                        intent.putExtra(RANDOM_VERB_INTENT_EXTRA, randomVerbs);
                        startActivity(intent);
                    } catch (DBService.NotEnoghtVerbsException e) {
                        Toast.makeText(activity, getString(R.string.not_enought_verb_message), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.help_cards_game:
                    Toast.makeText(getContext(), "Info Game 1", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.help_true_false_game:
                    Toast.makeText(getContext(), "Info Game 2", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.help_game_3:
                    Toast.makeText(getContext(), "Info Game 3", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.help_game_4:
                    Toast.makeText(getContext(), "Info Game 4", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
