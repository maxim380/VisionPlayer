package com.maxim.visionplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_REQUEST_CODE = 1;

    private MediaPlayerService mediaPlayer;
    private boolean serviceBound = false;
    private ArrayList<AudioFile> audioList;
    private boolean permissionGranted;
    private int currentSongIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(getColour());
        checkPermission();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_library:
                    loadLibraryPage();
                    return true;
                case R.id.navigation_nowPlaying:
                    loadNowPlayingPage();
                    return true;
                case R.id.navigation_friends:
                    loadFriendsPage();
                    return true;
                case R.id.navigation_settings:
                    loadSettingsPage();
                    return true;
            }
            return false;
        }
    };

    private void loadLibraryPage() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("files", audioList);

        LibraryPage fragment = new LibraryPage();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }

    private void loadNowPlayingPage() {
        PlayerPage fragment = new PlayerPage();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }

    private void loadFriendsPage() {
        FriendsPage fragment = new FriendsPage();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }

    private void loadSettingsPage() {
        SettingsPage fragment = new SettingsPage();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }


    private void loadNoPermissionsPage() {
        NoPermissionsPage fragment = new NoPermissionsPage();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mediaPlayer = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public void playAudio(String media, int index) {
        //Check is service is active
        if (!serviceBound) {
            currentSongIndex = index;
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            currentSongIndex = index;
            mediaPlayer.playMedia(media);
        }
    }

    private void setTitle(String title) {
        TextView titleView = (TextView)findViewById(R.id.title);
        titleView.setText(title);
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_REQUEST_CODE);
            permissionGranted = false;
        } else {
            permissionGranted = true;
            loadApp();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serviceBound) {
            unbindService(serviceConnection);
            mediaPlayer.stopSelf();
        }
    }

    public void loadAudio() {
//
//        if(!permissionGranted) {
//            return;
//        }

        ContentResolver contentResolver = getContentResolver();


        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                String albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

//                Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
//
//                Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
//                Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                // Save to audioList
                audioList.add(new AudioFile(data, title, album, artist, ""));
            }
        }
        cursor.close();
    }

//    public void checkPermission() {
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Permission is not granted
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed; request the permission
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        1);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else {
//            // Permission has already been granted
//            permissionGranted = true;
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permissionGranted = true;
                    loadApp();
                } else {
                    permissionGranted = false;
                    disableApp();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void disableApp() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setVisibility(View.INVISIBLE);
        loadNoPermissionsPage();
    }

    public void loadApp() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //TODO fix this
//        navigation.setItemBackgroundResource(getColour());
        loadAudio();
        loadLibraryPage();
    }

    public ArrayList<AudioFile> getAudioList() {
        return this.audioList;
    }

    public AudioFile getCurrentSong() {
        if(audioList.size() != 0) {
            return audioList.get(currentSongIndex);
        }
        return null;
    }

    public AudioFile getFirstSong() {
        if(audioList.size() > 0) {
            return audioList.get(0);
        }
        return null;
    }

    public MediaPlayerService getMediaPlayer() {
        return this.mediaPlayer;
    }

    public AudioFile getNextSong() {
        if(audioList.size() > 0 && currentSongIndex != audioList.size() - 1) {
            return audioList.get(currentSongIndex + 1);
        }
        return null;
    }

    public AudioFile getPreviousSong() {
        if(audioList.size() > 0 && currentSongIndex > 0) {
            return audioList.get(currentSongIndex - 1);
        }
        return null;
    }

    public int getColour() {
        SharedPreferences prefs = getSharedPreferences("ToolbarColour", Context.MODE_PRIVATE);
        return prefs.getInt("colour", getResources().getColor(R.color.colorPrimary));
    }

    public int getCurrentSongIndex() {
        return this.currentSongIndex;
    }

    public boolean currentSongIsFirstSong() {
        return currentSongIndex == 0;
    }

    public boolean currentSongIsLastSong() {
        return currentSongIndex == audioList.size() - 1;
    }

    public boolean isPermissionGranted() {
        return this.permissionGranted;
    }
}
