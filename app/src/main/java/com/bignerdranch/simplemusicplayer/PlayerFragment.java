package com.bignerdranch.simplemusicplayer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlayerFragment extends Fragment {

    //private final String ARG_TITLE = "title";
    //private final String ARG_ARTIST = "artist";
    //private final String ARG_ALBUM = "album";
    private final String ARG_PLAYING = "playing";

    private boolean mPlaying;
    private View mPlayerView;
    private static AudioFile mSong;
    private TextView mPlayerTitle;
    private TextView mPlayerArtist;
    private TextView mPlayerAlbum;
    private Button mPlayPauseButton;
    private Button mStopButton;


    public static PlayerFragment newInstance() {


        PlayerFragment fragment = new PlayerFragment();
        return fragment;
    }

    public void setmSong(AudioFile song){
        mSong = song;
        updatePlayer();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mPlaying = savedInstanceState.getBoolean(ARG_PLAYING);
        }
        Log.v("PlayerFragment","Value of mPLaying =" + mPlaying);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mPlayerView = inflater.inflate(R.layout.fragment_player, container,false);

        mPlayerTitle = mPlayerView.findViewById(R.id.player_title);
        mPlayerArtist = mPlayerView.findViewById(R.id.player_artist);
        mPlayerAlbum = mPlayerView.findViewById(R.id.player_album);

        if(mSong != null){
            updatePlayer();
        }

        mPlayPauseButton = mPlayerView.findViewById(R.id.playpause);

        if(mPlaying){

            mPlayPauseButton.setText("Pause");
        }
        else{
            mPlayPauseButton.setText("Play");
        }

        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SoundController.getmMediaPlayer() == null){
                    Toast.makeText(getContext(),"Please Choose a Song to Play", Toast.LENGTH_SHORT).show();
                }
                else if(mPlaying){

                    SoundController.pausePlayer();
                    mPlaying = false;
                    mPlayPauseButton.setText("Play");
                }
                else{

                    SoundController.resumePlayer();
                    mPlaying = true;
                    mPlayPauseButton.setText("Pause");
                }
            }
        });

        return mPlayerView;
    }

    public void updatePlayer(){

        mPlayerTitle = (TextView) mPlayerView.findViewById(R.id.player_title);
        mPlayerTitle.setText( mSong.getmTitle());

        mPlayerArtist = (TextView) mPlayerView.findViewById(R.id.player_artist);
        mPlayerArtist.setText(mSong.getmArtist());

        mPlayerAlbum = (TextView) mPlayerView.findViewById(R.id.player_album);
        mPlayerAlbum.setText(mSong.getmAlbum());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ARG_PLAYING,mPlaying);

        Log.v("PlayerFragment","Value of mPLaying =" + mPlaying);
    }

    public void setPlay(){
        mPlaying = true;
        mPlayPauseButton.setText("Pause");
        SoundController.startPlayer();
    }
}
