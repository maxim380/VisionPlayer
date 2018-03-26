package com.maxim.visionplayer;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageTwo extends Fragment {

    private ImageView nextImg;
    private ImageView prevImg;
    private ImageView playImg;
    private TextView titleText;
    private TextView infoText;

    private MainActivity activity;
    private MediaPlayerService mediaPlayer;

    public PageTwo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_two, container, false);

        activity = (MainActivity) getActivity();
        AudioFile tmp = activity.getCurrentSong();
        mediaPlayer = activity.getMediaPlayer();


        titleText = (TextView) view.findViewById(R.id.textViewTitle);
        infoText = (TextView) view.findViewById(R.id.textViewSongInfo);
        nextImg = (ImageView) view.findViewById(R.id.nextImg);
        prevImg = (ImageView) view.findViewById(R.id.prevImg);
        playImg = (ImageView) view.findViewById(R.id.playImg);

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

            }
        });

        prevImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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
        }
    }
}
