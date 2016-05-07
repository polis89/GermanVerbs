package ru.polis.germanverbs.games;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import ru.polis.germanverbs.R;

/**
 *
 * Created by Dmitrii on 07.05.2016.
 */
public class CardsGameActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Развертка layout
        setContentView(R.layout.cards_game_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textView = (TextView) findViewById(R.id.cards_game_text_view_main);
        textView.setText("CardsGameActivity");

    }
}
