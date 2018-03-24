package com.maxim.visionplayer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageTwo extends Fragment {


    public PageTwo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_two, container, false);

        TextView textView = (TextView) view.findViewById(R.id.textView2);
        Bundle bundle = this.getArguments();
        AudioFile tmp = (AudioFile)bundle.getSerializable("currentSong");

        if(tmp != null) {
            textView.setText(tmp.getTitle());
        }

        return view;
    }

}
