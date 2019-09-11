package com.bignerdranch.simplemusicplayer;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundController {

    private ArrayList<AudioFile> mSongs;
    private static SoundController mSoundController;
    private static MediaPlayer mMediaPlayer;

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

        return tempAudioList;
    }

    private SoundController(Context context){
        mSongs = getAllAudioFromDevice(context);
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

    public static void setMediaPlayer(AudioFile song, Context context){
            mMediaPlayer = MediaPlayer.create(context, Uri.parse(song.getmPath()));
    }
}
