package com.maxim.visionplayer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static android.provider.Telephony.Mms.Part.FILENAME;

/**
 * Created by maxim on 3/28/2018.
 */

public class SettingsPage extends Fragment {

    private Properties props = new Properties();
    private Toolbar toolbar;
    private Button buttonRed;
    private Button buttonGreen;
    private Button buttonYellow;
    private Context context;

    public SettingsPage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_page, container, false);

        final BottomNavigationView nav = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        buttonRed = (Button) view.findViewById(R.id.button);
        buttonGreen = (Button) view.findViewById(R.id.button2);
        buttonYellow = (Button) view.findViewById(R.id.button3);

        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorRed));
                storeColour(getResources().getColor(R.color.colorRed));
                nav.setItemBackgroundResource(R.color.colorRed);
            }
        });

        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorGreen));
                storeColour(getResources().getColor(R.color.colorGreen));
                nav.setItemBackgroundResource(R.color.colorGreen);
            }
        });

        buttonYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPurple));
                storeColour(getResources().getColor(R.color.colorPurple));
                nav.setItemBackgroundResource(R.color.colorPurple);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void storeColour(int colour) {
        SharedPreferences prefs = context.getSharedPreferences("ToolbarColour", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("colour", colour);
        editor.apply();
    }
}
