package com.bignerdranch.simplemusicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SaveState {

    private static final String SONG_TITLE = "Title";
    private static final String LAST_POSITION = "Position";

    private static SaveState mSaveState;
    private static SharedPreferences mStateManager;

    private SaveState(Context context){
        mStateManager = context.getSharedPreferences( context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static SaveState getInstance(Context context){

        if( mSaveState == null ){
            mSaveState = new SaveState(context);
        }

        return mSaveState;
    }

    public static void saveState(String title, int curPos){
        SharedPreferences.Editor editor = mStateManager.edit();
        editor.putString(SONG_TITLE, title);
        editor.putInt(LAST_POSITION, curPos);
        editor.commit();
    }

    public static String getTitle(){
        return mStateManager.getString(SONG_TITLE,null);
    }

    public static int getLastPosition(){

        return mStateManager.getInt(LAST_POSITION,-1);
    }
}
