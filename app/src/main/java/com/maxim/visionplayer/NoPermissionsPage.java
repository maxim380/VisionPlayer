package com.maxim.visionplayer;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by maxim on 4/4/2018.
 */

public class NoPermissionsPage extends Fragment {

    private MainActivity activity;

    public NoPermissionsPage() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nopermission, container, false);

        activity = (MainActivity)getActivity();
        Button button = view.findViewById(R.id.permissionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.checkPermission();
            }
        });
        return view;
    }
}
