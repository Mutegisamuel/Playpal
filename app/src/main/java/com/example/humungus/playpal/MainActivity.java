package com.example.humungus.playpal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.Key;

import static android.content.ContentValues.TAG;

public class  MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 4000;
    public static final int MEDIA_LOADER_ID = 79;

    private SimpleCursorAdapter adapter;
    private ListAdapter mediaAdapter = null;
    private TextView selectedFile = null;
    private SeekBar seekbar = null;
    private MediaPlayer player = null;
    private ImageButton play = null;
    private ImageButton prev = null;
    private ImageButton next = null;
    private LinearLayout popup;

    private boolean isStarted = true;
    private String currentFile = "";
    private boolean isMovingSeekBar = false;

    Cursor cursor;
    private final Handler handler = new Handler();

    private Runnable updatePositionRunnable = new Runnable() {
        public void run() {
            updatePosition();
        }
    };




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedFile = (TextView) findViewById(R.id.selectedfile);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        play = (ImageButton) findViewById(R.id.playButton);
        prev = (ImageButton) findViewById(R.id.prevButton);
        next = (ImageButton) findViewById(R.id.nextButton);

        player = new MediaPlayer();

        popup = (LinearLayout) findViewById(R.id.popup);
        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), nowplaying.class);
                startActivity(intent);
            }
        });

        player.setOnCompletionListener(onCompletion);
        player.setOnErrorListener(onError);
        seekbar.setOnSeekBarChangeListener(seekBarChange);

        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);

        if (null != cursor) {
            cursor.moveToFirst();

            mediaAdapter = new MediaCursorAdapter(
                    this,
                    R.layout.list_item,
                    cursor);

                ListView listView = findViewById(R.id.list);
                listView.setAdapter(mediaAdapter);




            play.setOnClickListener(onButtonPress);
            next.setOnClickListener(onButtonPress);
            prev.setOnClickListener(onButtonPress);


        }
            getPermissionToReadMusic();
            setupCursorAdapter();

        ListView lvSongs = (ListView) findViewById(R.id.list);
        lvSongs.setAdapter(adapter);
        getSupportLoaderManager().initLoader(MEDIA_LOADER_ID,
                new Bundle(), this);
    }


    private void setupCursorAdapter(){
        String[] uiBindFrom = {MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION};
        int[] uiBindTo = {R.id.displayname,R.id.title,R.id.duration};
        adapter = new SimpleCursorAdapter(
                this,R.layout.list_item,
                cursor, uiBindFrom, uiBindTo,
                0);
    }

    // Identifier for the permission request
    private static final int READ_MUSIC_PERMISION_REQUEST = 1;

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToReadMusic() {
//         1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
//         checking the build version since Context.checkSelfPermission(...) is only available
//         in Marshmallow
//         2) Always check for permission (even if permission has already been granted)
//         since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_MUSIC_PERMISION_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_MUSIC_PERMISION_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = shouldShowRequestPermissionRationale( this, Manifest.permission.READ_EXTERNAL_STORAGE);

                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @org.jetbrains.annotations.Contract(pure = true)
    private boolean shouldShowRequestPermissionRationale(MainActivity mainActivity, String readExternalStorage) {
        return true;
    }

    @Override
        protected void onDestroy (){
        super.onDestroy();

        handler.removeCallbacks(updatePositionRunnable);

        player.stop();
        player.reset();
        player.release();

        player = null;
}


private  void startPlay(String file) {
    Log.i(TAG, "selected: "+file);

    selectedFile.setText(file);
    seekbar.setProgress(0);

    player.stop();
    player.reset();

    try{
        player.setDataSource(file);
        player.prepare();
        player.start();
    } catch (IOException e) {
        e.printStackTrace();
    }catch (IllegalArgumentException e){
        e.printStackTrace();
    }catch (IllegalStateException e){
        e.printStackTrace();
    }

    seekbar.setMax(player.getDuration());

    play.setImageResource(android.R.drawable.ic_media_pause);


    updatePosition();

    isStarted = true;
}


    private void stopPlay (){
        player.stop();
        player.reset();
        play.setImageResource(android.R.drawable.ic_media_play);
        handler.removeCallbacks(updatePositionRunnable);
        seekbar.setProgress(0);

        isStarted = false;
}

private void updatePosition (){
    handler.removeCallbacks(updatePositionRunnable);

    seekbar.setProgress(player.getCurrentPosition());

    handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
}

public View.OnClickListener onButtonPress = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playButton:{
                if (player.isPlaying()) {
                    handler.removeCallbacks(updatePositionRunnable);
                    player.pause();
                    play.setImageResource(android.R.drawable.ic_media_play);
                }else{
                    if(isStarted){
                        player.start();
                        play.setImageResource(android.R.drawable.ic_media_pause);

                        updatePosition();
                    }else{
                        startPlay(currentFile);
                    }
                }
                break;
            }
            case R.id.nextButton:{
                int seekto = player.getCurrentPosition() + STEP_VALUE;

                if (seekto > player.getDuration())
                    seekto = player.getDuration();

                player.pause();
                player.seekTo(seekto);
                player.start();

                break;
            }
            case R.id.prevButton:{
                int seekto = player.getCurrentPosition() - STEP_VALUE;

                if(seekto > 0);
                seekto = 0;

                player.pause();
                player.seekTo(seekto);
                player.start();

                break;
            }
        }

    }

};

private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {
    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlay();
    }
};

private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
};

private SeekBar.OnSeekBarChangeListener seekBarChange = new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(isMovingSeekBar){
            player.seekTo(progress);

            Log.i(TAG, "onSeekBarChange");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isMovingSeekBar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isMovingSeekBar = false;
    }
};

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projectionFields = new String[] {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST};
        // Construct the loader
        CursorLoader cursorLoader = new CursorLoader(MainActivity.this,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // URI
                projectionFields, // projection fields
                null, // the selection criteria
                null, // the selection args
                null // the sort order
        );
        // Return the loader for use
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

