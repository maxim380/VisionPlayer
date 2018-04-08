package com.maxim.visionplayer.Fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maxim.visionplayer.Models.AudioFile;
import com.maxim.visionplayer.MainActivity;
import com.maxim.visionplayer.MediaPlayerService;
import com.maxim.visionplayer.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerPage extends Fragment {

    private ImageView nextImg;
    private ImageView prevImg;
    private ImageView playImg;
    private ImageView albumArt;
    private TextView titleText;
    private TextView infoText;

    private MainActivity activity;
    private MediaPlayerService mediaPlayer;

    public PlayerPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_page, container, false);

        activity = (MainActivity) getActivity();
        mediaPlayer = activity.getMediaPlayer();


        titleText = (TextView) view.findViewById(R.id.textViewTitle);
        infoText = (TextView) view.findViewById(R.id.textViewSongInfo);
        nextImg = (ImageView) view.findViewById(R.id.nextImg);
        prevImg = (ImageView) view.findViewById(R.id.prevImg);
        playImg = (ImageView) view.findViewById(R.id.playImg);
        albumArt = (ImageView) view.findViewById(R.id.albumArt);

        setSongInfo(activity.getCurrentSong());

        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                playImg.setTag(R.drawable.ic_play_arrow);
            } else {
                playImg = (ImageView) view.findViewById(R.id.playImg);
                playImg.setTag(R.drawable.ic_pause);
                playImg.setImageResource(R.drawable.ic_pause);
            }
        }



        createListeners();
        return view;
    }

    private void createListeners() {
        nextImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(activity.getNextSong() != null) {
                    AudioFile tmp = activity.getNextSong();
                    activity.playAudio(tmp.getData(), activity.getCurrentSongIndex() + 1);
                    playImg.setTag(R.drawable.ic_pause);
                    playImg.setImageResource(R.drawable.ic_pause);
                    setSongInfo(tmp);
                } else {
                    System.out.println("Helemaal niets doen");
                }
            }
        });

        prevImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(activity.getPreviousSong() != null) {
                    AudioFile tmp = activity.getPreviousSong();
                    activity.playAudio(tmp.getData(), activity.getCurrentSongIndex() - 1);
                    playImg.setTag(R.drawable.ic_pause);
                    playImg.setImageResource(R.drawable.ic_pause);
                    setSongInfo(tmp);
                } else {
                    System.out.println("Helemaal niets doen");
                }
            }
        });

        playImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if ((int) playImg.getTag() == R.drawable.ic_play_arrow) {
                        mediaPlayer.playMedia();
                        playImg.setImageResource(R.drawable.ic_pause);
                        playImg.setTag(R.drawable.ic_pause);
                    } else {
                        mediaPlayer.pauseMedia();
                        playImg.setImageResource(R.drawable.ic_play_arrow);
                        playImg.setTag(R.drawable.ic_play_arrow);
                    }
                } else {
                    if(activity.getFirstSong() != null) {
                        activity.playAudio(activity.getFirstSong().getData(), 0);
                        mediaPlayer = activity.getMediaPlayer();
                        setSongInfo(activity.getFirstSong());
                        playImg.setImageResource(R.drawable.ic_pause);
                        playImg.setTag(R.drawable.ic_pause);
                    }
                }
            }
        });
    }

    public void setSongInfo(AudioFile file) {
        if(file != null) {
            titleText.setText(file.getTitle());
            infoText.setText(file.getArtist() + " - " + file.getAlbum());
            if(activity.currentSongIsLastSong()) {
                nextImg.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            }
            if(activity.currentSongIsFirstSong()) {
                prevImg.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            } else {
                prevImg.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                nextImg.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            }
            albumArt.setImageBitmap(file.getAlbumArt());
        }
    }
}
