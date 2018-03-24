package com.maxim.visionplayer;

import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class ListFragment extends Fragment{

    ArrayList<AudioFile> files;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        Bundle bundle = this.getArguments();
        files = (ArrayList<AudioFile>)bundle.getSerializable("files");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listRecyclerView);

        ListAdapter listAdapter = new ListAdapter();
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    private class ListAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new com.maxim.visionplayer.ListFragment.ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((com.maxim.visionplayer.ListFragment.ListViewHolder) holder).bindView(position);
        }

        @Override
        public int getItemCount() {
            return files.size();
        }
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mItemText;
        private ImageView mItemImage;

        public ListViewHolder(View itemView) {
            super(itemView);
            mItemText = (TextView) itemView.findViewById(R.id.itemText);
            mItemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemView.setOnClickListener(this);
        }

        public void bindView(int position) {
            mItemText.setText(files.get(position).getTitle());
        }

        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Hallo! " + this.getAdapterPosition(), Toast.LENGTH_SHORT).show();
            MainActivity activity = (MainActivity) getActivity();
            activity.playAudio(files.get(this.getAdapterPosition()).getData());
        }
    }

}


