package com.maxim.visionplayer.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxim.visionplayer.R;

/**
 * Created by maxim on 4/5/2018.
 */

public class NoSongsFragment extends Fragment{

    public NoSongsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nosongs, container, false);
    }
}
