package com.maxim.visionplayer.FAB;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;

/**
 * Created by maxim on 4/8/2018.
 */

public class FABSpeedDial extends SpeedDialMenuAdapter {
    @Override
    public int getCount() {
        return 3;
    }

    @NotNull
    @Override
    public SpeedDialMenuItem getMenuItem(Context context, int i) {
        return null;
    }
}
