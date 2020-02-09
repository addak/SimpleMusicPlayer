package com.bignerdranch.simplemusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MusicPlayerActivity extends AppCompatActivity{

    public static Intent newIntent(Context context) {

        Intent i = new Intent(context, MusicPlayerActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.music_player_holder);

        if( fragment == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.music_player_holder, MusicPlayerFragment.newInstance()).commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
