package com.bignerdranch.simplemusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PlayerFragment extends Fragment {

    //private final String ARG_TITLE = "title";
    //private final String ARG_ARTIST = "artist";
    //private final String ARG_ALBUM = "album";
    private final int PLAYER_CHANGE = 3;

    private View mPlayerView;
    private static AudioFile mSong;
    private TextView mPlayerTitle;
    private TextView mPlayerArtist;
    private TextView mPlayerAlbum;
    private ImageButton mPlayPauseButton;
    private Button mStopButton;

    EventBus mEventBus;


    public static PlayerFragment newInstance() {


        PlayerFragment fragment = new PlayerFragment();
        return fragment;
    }

    public void setmSong(AudioFile song){
        mSong = song;
        updateUI();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);

        mPlayerView = inflater.inflate(R.layout.fragment_player, container,false);

        mPlayerTitle = mPlayerView.findViewById(R.id.player_title);
        mPlayerArtist = mPlayerView.findViewById(R.id.player_artist);
        mPlayerAlbum = mPlayerView.findViewById(R.id.player_album);

        mPlayPauseButton = mPlayerView.findViewById(R.id.playpause);

        if(mSong != null){
            updateUI();
        }

        if( SoundController.getmMediaPlayer() != null){

            if(SoundController.isPlaying()){

                mPlayPauseButton.setImageResource(R.drawable.pause_fragment_button);
            }
            else{
                mPlayPauseButton.setImageResource(R.drawable.play_fragment_button);
            }
        }

        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SoundController.getmMediaPlayer() == null){
                    Toast.makeText(getContext(),"Please Choose a Song to Play", Toast.LENGTH_SHORT).show();
                }
                else if(SoundController.isPlaying()){

                    SoundController.pausePlayer();
                    mPlayPauseButton.setImageResource(R.drawable.play_fragment_button);
                }
                else{

                    SoundController.resumePlayer();
                    mPlayPauseButton.setImageResource(R.drawable.pause_fragment_button);
                }
            }
        });

        RelativeLayout relativeLayout = mPlayerView.findViewById(R.id.relativelayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(MusicPlayerActivity.newIntent(getActivity()), PLAYER_CHANGE );
            }
        });

        return mPlayerView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateUI(){

        if(mSong != null){
            mPlayerTitle.setText( mSong.getmTitle());

            mPlayerArtist.setText(mSong.getmArtist());

            mPlayerAlbum.setText(mSong.getmAlbum());

            if(SoundController.isPlaying()){

                mPlayPauseButton.setImageResource(R.drawable.pause_fragment_button);
            }
            else{
                mPlayPauseButton.setImageResource(R.drawable.play_fragment_button);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void setPlay(){
        mPlayPauseButton.setImageResource(R.drawable.pause_fragment_button);
        SoundController.startPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEventBus.unregister(this);
    }

    @Override
    public void onResume() {
        mSong =SoundController.getmCurSong();
        updateUI();
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event){
        if( event.equals("Update UI") && !SoundController.isMusicPlayerFragmentActive()){
            mSong = SoundController.getmCurSong();
            SoundController.setMediaPlayer(mSong);

            SoundController.startPlayer();
            updateUI();
        }
        else if(event.equals("Initialize")){
            mSong = SoundController.getmCurSong();
            updateUI();
        }
    }
}
