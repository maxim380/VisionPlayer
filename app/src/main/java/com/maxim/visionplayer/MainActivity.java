package com.maxim.visionplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.maxim.visionplayer.Fragments.FriendsPage;
import com.maxim.visionplayer.Fragments.LibraryPage;
import com.maxim.visionplayer.Fragments.NoPermissionsPage;
import com.maxim.visionplayer.Fragments.NoSongsFragment;
import com.maxim.visionplayer.Fragments.PlayerPage;
import com.maxim.visionplayer.Fragments.SettingsPage;
import com.maxim.visionplayer.Models.AudioFile;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_REQUEST_CODE = 1;

    private MediaPlayerService mediaPlayer;
    private boolean serviceBound = false;
    private ArrayList<AudioFile> audioList;
    private boolean permissionGranted;
    private int currentSongIndex;
    private FloatingActionButton fabAddFriend;
    private FloatingActionButton fabRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAddFriend = findViewById(R.id.fabAddFriend);
        fabRefresh = findViewById(R.id.fabRefresh);
        changeColor(getColor());
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
        disableFABs();
        if (audioList != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("files", audioList);

            LibraryPage fragment = new LibraryPage();
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.content, fragment, "FragmentName");
            fragmentTransaction.commit();
        } else {
            //No songs found on phone
            BottomNavigationView nav = findViewById(R.id.navigation);
            nav.setVisibility(View.INVISIBLE);
            NoSongsFragment fragment = new NoSongsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.content, fragment, "FragmentName");
            fragmentTransaction.commit();
        }
    }

    private void loadNowPlayingPage() {
        disableFABs();
        PlayerPage fragment = new PlayerPage();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }

    private void loadFriendsPage() {
        enableFABs();
        FriendsPage fragment = new FriendsPage();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "FragmentName");
        fragmentTransaction.commit();
    }

    private void loadSettingsPage() {
        disableFABs();
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

    private void disableFABs() {
        fabRefresh.setVisibility(View.GONE);
        fabAddFriend.setVisibility(View.GONE);
    }

    private void enableFABs() {
        fabAddFriend.setVisibility(View.VISIBLE);
        fabRefresh.setVisibility(View.VISIBLE);
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
        if (serviceBound) {
            unbindService(serviceConnection);
            mediaPlayer.stopSelf();
        }
    }

    public void loadAudio() {
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
                audioList.add(new AudioFile(data, title, album, artist, getAlbumArt(data)));
            }
        }
        cursor.close();
    }


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
        if (navigation.getVisibility() == View.INVISIBLE) {
            navigation.setVisibility(View.VISIBLE);
        }
        loadAudio();
        loadLibraryPage();
    }

    public ArrayList<AudioFile> getAudioList() {
        return this.audioList;
    }

    public AudioFile getCurrentSong() {
        if (audioList.size() != 0) {
            return audioList.get(currentSongIndex);
        }
        return null;
    }

    public AudioFile getFirstSong() {
        if (audioList.size() > 0) {
            return audioList.get(0);
        }
        return null;
    }

    public MediaPlayerService getMediaPlayer() {
        return this.mediaPlayer;
    }

    public AudioFile getNextSong() {
        if (audioList.size() > 0 && currentSongIndex != audioList.size() - 1) {
            return audioList.get(currentSongIndex + 1);
        }
        return null;
    }

    public AudioFile getPreviousSong() {
        if (audioList.size() > 0 && currentSongIndex > 0) {
            return audioList.get(currentSongIndex - 1);
        }
        return null;
    }

    public int getColor() {
        SharedPreferences prefs = getSharedPreferences("colorPrefs", Context.MODE_PRIVATE);

        String color = prefs.getString("colorString", "x");

        switch (color) {
            case "blue":
                return getResources().getColor(R.color.colorPrimary);
            case "red":
                return getResources().getColor(R.color.colorRed);
            case "purple":
                return getResources().getColor(R.color.colorPurple);
            case "green":
                return getResources().getColor(R.color.colorGreen);
            default:
                return getResources().getColor(R.color.colorPrimary);
        }
    }

    public void changeColor(int color) {
        getWindow().setStatusBarColor(color);
        fabRefresh.setBackgroundTintList(ColorStateList.valueOf(color));
        fabAddFriend.setBackgroundTintList(ColorStateList.valueOf(color));
        BottomNavigationView nav = (BottomNavigationView) findViewById(R.id.navigation);
        switch (color) {
            case -12627531:
                nav.setItemBackgroundResource(R.color.colorPrimary);
                break;
            case -65536:
                nav.setItemBackgroundResource(R.color.colorRed);
                break;
            case -10092289:
                nav.setItemBackgroundResource(R.color.colorPurple);
                break;
            case -16744448:
                nav.setItemBackgroundResource(R.color.colorGreen);
                break;

        }
    }

    public Bitmap getAlbumArt(String songpath) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songpath);
        byte[] data = mmr.getEmbeddedPicture();
        //coverart is an Imageview object

        // convert the byte array to a bitmap
        if (data != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            coverart.setImageBitmap(bitmap); //associated cover art in bitmap
//            coverart.setAdjustViewBounds(true);
//            coverart.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
            return bitmap;
        } else {
            return null;
        }
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
