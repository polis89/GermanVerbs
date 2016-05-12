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
import ru.polis.germanverbs.objects.Verb;

/**
 * Fragment to show all type of Practices (Games)
 *
 * Created by Dmitrii Polianskii on 24.04.2016.
 */
public class PracticeFragment extends Fragment{
    public static final String LOG_TAG = "PracticeFragment";
    private static final String RANDOM_VERB_INTENT_EXTRA = "random_verbs";

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
        CardView viewGame2 = (CardView) view.findViewById(R.id.card_view_game_2);
        CardView viewGame3 = (CardView) view.findViewById(R.id.card_view_game_3);
        CardView viewGame4 = (CardView) view.findViewById(R.id.card_view_game_4);

        ImageView infoView1 = (ImageView) view.findViewById(R.id.help_game_1);
        ImageView infoView2 = (ImageView) view.findViewById(R.id.help_game_2);
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
            switch (v.getId()){
                case R.id.cards_game_view:
                    FragmentActivity activity = getActivity();
                    Intent intent = new Intent(activity, CardsGameActivity.class);
                    int verbCount = activity.getSharedPreferences(MainActivity.SHARED_PREF, Context.MODE_PRIVATE)
                            .getInt(MainActivity.SHARED_PREF_CARDS_GAME_WORD_COUNT, 20);
                    DBService dbService = DBService.getInstance(activity);
                    Verb[] randomVerbs;
                    try {
                        randomVerbs = dbService.getRandomVerbs(verbCount, ((MainActivity)activity).language);
                        intent.putExtra(RANDOM_VERB_INTENT_EXTRA, randomVerbs);
                        startActivity(intent);
                    } catch (DBService.NotEnoghtVerbsException e) {
                        Toast.makeText(activity, getString(R.string.not_enought_verb_message), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.card_view_game_2:
                    Toast.makeText(getContext(), "Game 2", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.card_view_game_3:
                    Toast.makeText(getContext(), "Game 3", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.card_view_game_4:
                    Toast.makeText(getContext(), "Game 4", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.help_game_1:
                    Toast.makeText(getContext(), "Info Game 1", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.help_game_2:
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
