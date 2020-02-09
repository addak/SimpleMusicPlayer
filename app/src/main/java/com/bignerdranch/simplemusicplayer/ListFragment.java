package com.bignerdranch.simplemusicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


public class ListFragment extends Fragment {
    private static final int LIST_CHANGE = 2;

    private RecyclerView mRecyclerView;
    private List<AudioFile> mSongs;
    private Callbacks mListCallbacks;

    public static ListFragment newInstance() {

        Bundle args = new Bundle();

        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface Callbacks{
        void onClickViewHolder();
        void onReturnFromMusicPlayer();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListCallbacks = null;
    }

    private class AudioItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleField;
        private TextView mAlbumField;
        private TextView mArtistField;
        private AudioFile mSong;
        private int mPosition;
        private RelativeLayout mRelativeLayout;

        public AudioItemHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.fragment_list_item, parent, false));

            mTitleField = itemView.findViewById(R.id.title);
            mArtistField = itemView.findViewById(R.id.artist);
            mAlbumField = itemView.findViewById(R.id.album);
            mRelativeLayout = itemView.findViewById(R.id.ViewHolder);

            itemView.setOnClickListener(this);
        }

        //Binds data from the crime to the fields of the respective ViewHolder
        private void bind(AudioFile song){
            mSong = song;
            mTitleField.setText(song.getmTitle());
            mAlbumField.setText(song.getmAlbum());
            mArtistField.setText(song.getmArtist());
        }

        //Defines what happens when a ViewHolder is clicked
        @Override
        public void onClick(View view) {
            /**
             * Intent intent = CrimeActivity.CrimeActivityIntent(getActivity(), mCrime.getId());
             * startActivity(intent);
             */
            SoundController.setmCurSong(mSong, getAdapterPosition());
            startActivityForResult(MusicPlayerActivity.newIntent(getActivity()), LIST_CHANGE);
            mListCallbacks.onClickViewHolder();
        }
    }

    private class AudioAdapter extends RecyclerView.Adapter<AudioItemHolder>{

        private List<AudioFile> mSongs;
        public AudioAdapter(List<AudioFile> songs) {
            mSongs = songs;
        }

        @NonNull
        @Override
        public AudioItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new AudioItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull AudioItemHolder holder, int position) {
            AudioFile sound = mSongs.get(position);
            holder.bind(sound);
        }

        @Override
        public int getItemCount() {
            return mSongs.size();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongs = SoundController.get(getActivity().getApplicationContext()).getSongs();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list,container,false);

        mRecyclerView = v.findViewById(R.id.song_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new AudioAdapter(mSongs));

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        SoundController.saveData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
