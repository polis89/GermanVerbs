package ru.polis.germanverbs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment to show all Verbs
 *
 * Created by Dmitrii Polianskii on 24.04.2016.
 */
public class VerbsFragment extends Fragment {
    private static VerbsFragment instanse;

    public static Fragment getInstance() {
        if(instanse == null){
            instanse = new VerbsFragment();
        }
        return instanse;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verbs, container, false);
        return view;
    }
}
