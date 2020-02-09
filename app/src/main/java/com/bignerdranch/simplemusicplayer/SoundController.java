package com.bignerdranch.simplemusicplayer;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SoundController {

    private static ArrayList<AudioFile> mSongs;
    private static SoundController mSoundController;
    private static SaveState mSaveStateManager;
    private static MediaPlayer mMediaPlayer;
    private static AudioFile mCurSong;
    private static int mCurPos;
    private static Context mContext;

    private static boolean mMusicPlayerFragmentActive = false;

    private static boolean mShuffle = false;

    public static boolean isMusicPlayerFragmentActive() {
        return mMusicPlayerFragmentActive;
    }

    public static void setMusicPlayerFragmentActive(boolean musicPlayerFragmentActive) {
        mMusicPlayerFragmentActive = musicPlayerFragmentActive;
    }


    private ArrayList<AudioFile> getAllAudioFromDevice(final Context context) {

        final ArrayList<AudioFile> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.TITLE,MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST};
        Cursor c = context.getContentResolver().query(uri,
                projection,
                null,
                null,
                null);

        if (c != null) {
            while (c.moveToNext()) {

                AudioFile audioModel = new AudioFile();
                String name = c.getString(0);
                String path = c.getString(1);
                String album = c.getString(2);
                String artist = c.getString(3);

                //String name = path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));

                //name = name.trim();

                audioModel.setTitle(name);
                audioModel.setAlbum(album);
                audioModel.setArtist(artist);
                audioModel.setPath(path);

                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                tempAudioList.add(audioModel);
            }
            c.close();
        }

        Collections.sort(tempAudioList, AudioFile.AudioTitleComparator);

        return tempAudioList;
    }

    private SoundController(Context context){

        mSongs = getAllAudioFromDevice(context);
        mSaveStateManager = SaveState.getInstance(context);
        mContext = context;
        mCurSong = getAudioFileusingState();

        if(mCurSong != null){
            setMediaPlayer(mCurSong);
            mMediaPlayer.seekTo(mCurPos);
            EventBus bus = EventBus.getDefault();
            bus.post("Initialize");
        }
    }

    public static SoundController get(Context context){

        if( mSoundController == null) mSoundController = new SoundController(context);

        return mSoundController;

    }

    public List<AudioFile> getSongs(){

        return mSongs;
    }

    public static MediaPlayer getmMediaPlayer(){
        return mMediaPlayer;
    }

    public static void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            Log.v("MainActivity","Released Resources!");
        }
    }

    public static void stopPlayer()
    {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        releaseMediaPlayer();
        mMediaPlayer = null;

    }


    public static void startPlayer(){
        mMediaPlayer.start();
    }

    public static void resumePlayer(){
        mMediaPlayer.start();
    }

    public static void pausePlayer(){
        mMediaPlayer.pause();
    }

    public static void setMediaPlayer(AudioFile song){
            mMediaPlayer = MediaPlayer.create(mContext, Uri.parse(song.getmPath()));
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    SoundController.changeSong("Next");
                    EventBus bus = EventBus.getDefault();
                    bus.post("Update UI");
                }
            });
    }

    public static boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    public static AudioFile getmCurSong() {
        return mCurSong;
    }

    public static void setmCurSong(AudioFile CurSong, int position) {
        mCurSong = CurSong;
        mCurPos = position;
        Log.v("SoundController","Value of position: "+mCurPos);
    }

    public static boolean isShuffling() {
        return mShuffle;
    }

    public static void setShuffle(boolean shuffle) {
        mShuffle = shuffle;
    }

    public static void changeSong(String direction){
        if (mShuffle) {
            Random randomizer = new Random();
            mCurPos = randomizer.nextInt(mSongs.size());
            mCurSong = mSongs.get( mCurPos );

        }
        else{
            if(direction == "Next"){
                mCurPos = (mCurPos + 1) % mSongs.size();
                mCurSong = mSongs.get(mCurPos);
            }
            else{

                if(mCurPos == 0) mCurPos = mSongs.size() - 1;
                else mCurPos = (mCurPos - 1) % mSongs.size();

                mCurSong = mSongs.get(mCurPos);
            }
        }
    }

    public static void saveData(){

        if( mMediaPlayer!= null ){
            int curPos = mMediaPlayer.getCurrentPosition();

            mSaveStateManager.saveState(mCurSong.getmTitle(), curPos);
        }
    }

    public static AudioFile getAudioFileusingState(){
        String songtitle = mSaveStateManager.getTitle();

        for(AudioFile audio : mSongs){
            if(audio.getmTitle().equals(songtitle)){
                mCurPos = mSaveStateManager.getLastPosition();
                return audio;
            }
        }

        return null;
    }
}
