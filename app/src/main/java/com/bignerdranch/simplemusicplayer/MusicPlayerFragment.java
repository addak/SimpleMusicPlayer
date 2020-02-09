package com.bignerdranch.simplemusicplayer;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.indatus.smoothseekbar.library.SmoothSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicPlayerFragment extends Fragment implements  SeekBar.OnSeekBarChangeListener{
    private static String PREVIOUS = "Previous";
    private static String NEXT = "Next";

    private View mView;
    private ImageButton mPlayPauseImageButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mTitleField;
    private TextView mArtistField;
    private TextView mAlbumField;
    private AudioFile mSong;
    private ImageView mAlbumArt;
    private ViewTreeObserver mViewTreeObserver;
    private SmoothSeekBar mSongSeekBar;
    private ImageButton mShuffleButton;

    EventBus mEventBus;


    public static MusicPlayerFragment newInstance(){

        MusicPlayerFragment fragment = new MusicPlayerFragment();
        return fragment;
    }

    public MusicPlayerFragment() {
        // Required empty public constructor
    }

    public interface Callbacks{
        void onChangeState();
    }

    public void setmSong(AudioFile song){
        mSong = song;
        updateUI();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SoundController.setMusicPlayerFragmentActive(true);
        mEventBus = EventBus.getDefault();
        mEventBus.register(this);

        mView = inflater.inflate(R.layout.fragment_music_player, container, false);

        mPlayPauseImageButton = mView.findViewById(R.id.playpauseButton);
        mTitleField = mView.findViewById(R.id.music_player_title);
        mArtistField = mView.findViewById(R.id.music_player_artist);
        mAlbumField = mView.findViewById(R.id.music_player_album);
        mAlbumArt = mView.findViewById(R.id.album_art);
        mPrevButton = mView.findViewById(R.id.previous);
        mNextButton = mView.findViewById(R.id.next);
        mSongSeekBar = mView.findViewById(R.id.musicProgress);
        mShuffleButton = mView.findViewById(R.id.shufflebutton);


        mPlayPauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SoundController.getmMediaPlayer() == null){
                    Toast.makeText(getContext(),"Please Choose a Song to Play", Toast.LENGTH_SHORT).show();
                }
                else if(SoundController.isPlaying()){
                    mSongSeekBar.pauseAnimating();
                    SoundController.pausePlayer();
                    mPlayPauseImageButton.setImageResource(R.drawable.play_button);
                }
                else{
                    SoundController.resumePlayer();
                    mSongSeekBar.beginAnimating();
                    mPlayPauseImageButton.setImageResource(R.drawable.pause_button);
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSong == null){
                    Toast.makeText(getContext(),"Please Choose a Song to Play", Toast.LENGTH_SHORT).show();
                }
                else{

                    SoundController.changeSong(NEXT);
                    mSong = SoundController.getmCurSong();
                    onNextPrev();
                    updateUI();
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSong == null){
                    Toast.makeText(getContext(),"Please Choose a Song to Play", Toast.LENGTH_SHORT).show();
                }
                else{
                    SoundController.changeSong(PREVIOUS);
                    mSong = SoundController.getmCurSong();
                    onNextPrev();
                    updateUI();
                }
            }
        });

        mShuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundController.setShuffle( !SoundController.isShuffling() );

                Log.v("MusicPlayerFragment", SoundController.isShuffling() + " ");
                mShuffleButton.setSelected( SoundController.isShuffling() );
            }
        });

        mShuffleButton.setSelected( SoundController.isShuffling() );

        mSong = SoundController.getmCurSong();

        mSongSeekBar.setOnSeekBarChangeListener(this);

        mSongSeekBar.post(new Runnable() {
            @Override
            public void run() {
                if( SoundController.isPlaying()){
                    mSongSeekBar.beginAnimating();
                }
            }
        });


        mSongSeekBar.setEndTime( SoundController.getmMediaPlayer().getDuration() );
        mSongSeekBar.setProgress( getInitialSeekPosition() );


        mViewTreeObserver = mAlbumArt.getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                updateUI();
            }
        });

        return mView;
    }

    public void onNextPrev(){
        if(SoundController.getmMediaPlayer() != null && SoundController.getmMediaPlayer().isPlaying()){
            SoundController.stopPlayer();
            mSongSeekBar.endAnimation();
        }

        SoundController.setMediaPlayer(mSong);

        SoundController.startPlayer();
        mSongSeekBar.setProgress( getInitialSeekPosition() );
        mSongSeekBar.setEndTime( SoundController.getmMediaPlayer().getDuration() );
        mSongSeekBar.beginAnimating();
    }

    public void updateUI(){

        if(mSong != null){

            mTitleField.setText( mSong.getmTitle());

            mArtistField.setText(mSong.getmArtist());

            mAlbumField.setText(mSong.getmAlbum());

            if(SoundController.isPlaying())
            {
                mPlayPauseImageButton.setImageResource(R.drawable.pause_button);
            }
            else{
                mPlayPauseImageButton.setImageResource(R.drawable.play_button);
            }

            int height = mAlbumArt.getHeight();
            int width = mAlbumArt.getWidth();

            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mSong.getmPath());


            byte [] data = mmr.getEmbeddedPicture();

            if(data != null)
            {
                Bitmap bitmap = AlbumFetchUtil.getScaledBitmap(data, height, width);
                mAlbumArt.setImageBitmap(bitmap);

//                if(hasImage(mAlbumArt)) mAlbumArt.setImageBitmap(bitmap); //associated cover art in bitmap
//                else{
//                    mAlbumArt.setImageResource(android.R.color.transparent);
//                    mAlbumArt.setImageBitmap(bitmap);
//                }
            }
            else{
                mAlbumArt.setImageResource(R.drawable.default_album_art);
            }
        }
        else{
            mAlbumArt.setImageResource(R.drawable.default_album_art);
        }
    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    private int  getInitialSeekPosition(){
        MediaPlayer mediaPlayer = SoundController.getmMediaPlayer();

        double percentageComplete =  mediaPlayer.getCurrentPosition() * (1 / (double) mediaPlayer.getDuration());

        double max = (double) mSongSeekBar.getMax();

        int value = (int) (percentageComplete * max);

        return value;
    }

    private int getPositionToSeek(){
        return (int) ((double) mSongSeekBar.getProgress() * (1 / (double) mSongSeekBar.getMax()) * (double)SoundController.getmMediaPlayer().getDuration());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
        if( fromUser )
            seekBar.setProgress(position);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mSongSeekBar.pauseAnimating();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        MediaPlayer mediaPlayer = SoundController.getmMediaPlayer();

        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.seekTo(getPositionToSeek());
            mediaPlayer.start();
            mSongSeekBar.beginAnimating();
        }
        else{
            mediaPlayer.seekTo(getPositionToSeek());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().setResult(Activity.RESULT_OK);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEventBus.unregister(this);
        SoundController.setMusicPlayerFragmentActive(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event){
        if( event.equals("Update UI")){
            mSong = SoundController.getmCurSong();
            onNextPrev();
            updateUI();
        }
    }

}
