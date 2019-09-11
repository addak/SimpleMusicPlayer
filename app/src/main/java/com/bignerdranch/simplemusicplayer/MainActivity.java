package com.bignerdranch.simplemusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ListFragment.Callbacks {

    private final int PERMISSION_READ_EXT_STORAGE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        if (!checkPermissionForReadExternalStorage()) {

            // Permission is not granted
            // Should we show an explanation?
            try {
                requestPermissionForReadExternalStorage();
            }
            catch(Exception e) {
                Log.e("MainActivity", "Unable to grant permission");
            }
        }
        else{
            FragmentManager fragmentManager = getSupportFragmentManager();

            PlayerFragment playerFragment = (PlayerFragment) fragmentManager.findFragmentById(R.id.player);
            ListFragment listFragment = (ListFragment) fragmentManager.findFragmentById(R.id.audio_file_list);

            if(playerFragment == null && listFragment == null){
                listFragment = ListFragment.newInstance();

                playerFragment = PlayerFragment.newInstance();

                fragmentManager.beginTransaction()
                        .add(R.id.player , playerFragment)
                        .add(R.id.audio_file_list,listFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onClickViewHolder(AudioFile song) {

        PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.player);

        if(SoundController.getmMediaPlayer() != null && SoundController.getmMediaPlayer().isPlaying()){
            SoundController.stopPlayer();
        }
        SoundController.setMediaPlayer(song, playerFragment.getContext());

        playerFragment.setPlay();
        playerFragment.setmSong(song);
        playerFragment.updatePlayer();
    }

    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExternalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_READ_EXT_STORAGE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_EXT_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ListFragment list = ListFragment.newInstance();

                    PlayerFragment player = PlayerFragment.newInstance();
                    fragmentTransaction.add(R.id.player , player);
                    fragmentTransaction.add(R.id.audio_file_list,list);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(this,"Permission not Granted!",Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
